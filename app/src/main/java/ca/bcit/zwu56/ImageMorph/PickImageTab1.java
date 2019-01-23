package ca.bcit.zwu56.ImageMorph;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.List;

public class PickImageTab1 extends Fragment {

    View rootView;
    /** two image containers */
    MorphImageView strImgView, endImgView;
    private ProgressBar morphingBar;
    private Button morphBtn;
    /** start and end image dimension */
    private int imgWidth, imgHeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.pick_image, container, false);
        strImgView = rootView.findViewById(R.id.srcImgView);
        endImgView = rootView.findViewById(R.id.desImgView);
        morphBtn = rootView.findViewById(R.id.morphBtn);
        morphingBar = rootView.findViewById(R.id.morph_wait_bar);
        return rootView;
    }


    /** request code for distinct where to put the opened image */
    final static int
            strImgRequestCode = 0,
            endImgRequestCode = 1;

//    /** open image picker */
//    public void openImageClick(View view) {
//        int requestCode;
//        switch (view.getId()) {
//            case R.id.openSrcImgBtn:
//                requestCode = strImgRequestCode;
//                break;
//            case R.id.openDesImgBtn:
//                requestCode = endImgRequestCode;
//                break;
//            default:
//                throw new UnsupportedOperationException("only src and dest will be clicked");
//        }
//        Intent intent = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, requestCode);
//    }

    /** load the picked image to the corresponding ImageView container */
    public void onPickImgResult(Bitmap immuBitmap, int requestCode) {

        MorphImageView imgContainer = pickImageViewContainer(requestCode);

        // get bitmap config
        Bitmap.Config config;
        if(immuBitmap.getConfig() != null){
            config = immuBitmap.getConfig();
        }else{
            config = Bitmap.Config.ARGB_8888;
        }

        // Android does not have onLoaeded event, i have no idea when to init these.
        imgWidth = strImgView.getWidth();
        imgHeight = strImgView.getHeight();
        // Mutable bitmap for drawing
        Bitmap bitmap = Bitmap.createBitmap(
                imgWidth,
                imgHeight,
                config);

        Canvas canvas = new Canvas(bitmap);
        Rect dest = new Rect(0,0, imgWidth, imgHeight);
        canvas.drawBitmap(immuBitmap, null, dest, null);
        imgContainer.setImageBitmap(bitmap);
    }

    /**
     * based on the request code to return the correct ImageView container (str or end)
     * @param requestCode strImgRequestCode or endImgRequestCode
     * @return strImgView or endImgView
     */
    private MorphImageView pickImageViewContainer(int requestCode) {
        switch (requestCode){
            case strImgRequestCode:
                return strImgView;
            case endImgRequestCode:
                return endImgView;
            default:
                throw new UnsupportedOperationException("only src and dest will load image");
        }
    }

    /** radio button selection change event */
    public void lineEditModeSelect(View view) {
        switch (view.getId()) {
            case R.id.drawRadio:
                MorphImageView.setLineEditMode(MorphImageView.DRAW_MODE);
                break;
            case R.id.editRadio:
                MorphImageView.setLineEditMode(MorphImageView.EDIT_MODE);
                break;
            case R.id.deleteRadio:
                MorphImageView.setLineEditMode(MorphImageView.DELETE_MODE);
                break;
        }
    }

    /** start morphing */
    public void morphBtnClick() {
        morphingBar.setVisibility(View.VISIBLE);
        morphBtn.setEnabled(false);

        //
        new AsyncTask<Void, Void, Void>() {
            List<Bitmap> images;
            @Override
            public Void doInBackground(Void ... params) {
                try {
                    int frames = Integer.parseInt(((EditText)rootView.findViewById(R.id.frameNum)).getText().toString());

                    images = ImageMorph.drawIntermediateFrames(frames, strImgView.getImageBitmap(),
                            endImgView.getImageBitmap(), strImgView.getDrawnLines(), endImgView.getDrawnLines());
                } catch (NumberFormatException ex) {}
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                morphingBar.setVisibility(View.GONE);
                morphBtn.setEnabled(true);
                ((MainActivity)getActivity()).browseMorphResult(images);
            }
        }.execute();
    }
}
