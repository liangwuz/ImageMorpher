package ca.bcit.zwu56.imagemorpher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    /** two image containers */
    MorphImageView strImgView, endImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        strImgView = findViewById(R.id.srcImgView);
        endImgView = findViewById(R.id.desImgView);
    }

    /** request code for distinct where to put the opened image */
     final int
            strImgRequestCode = 0,
            endImgRequestCode = 1;

    /** open image picker */
    public void openImageClick(View view) {
        int requestCode;
        switch (view.getId()) {
            case R.id.openSrcImgBtn:
                requestCode = strImgRequestCode;
                break;
            case R.id.openDesImgBtn:
                requestCode = endImgRequestCode;
                break;
            default:
                throw new UnsupportedOperationException("only src and dest will be clicked");
        }
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    /** load the picked image to the corresponding ImageView container */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)
            return;

        MorphImageView imgContainer = pickImageViewContainer(requestCode);
        try {
            //Immutable bitmap cannot be passed to Canvas constructor
            Bitmap immuBitmap = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(data.getData()));
            // get bitmap config
            Bitmap.Config config;
            if(immuBitmap.getConfig() != null){
                config = immuBitmap.getConfig();
            }else{
                config = Bitmap.Config.ARGB_8888;
            }
            // Mutable bitmap for drawing
            Bitmap bitmap = Bitmap.createBitmap(
                    immuBitmap.getWidth(),
                    immuBitmap.getHeight(),
                    config);

            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(immuBitmap, 0, 0, null);
            imgContainer.setImageBitmap(bitmap);

        } catch (FileNotFoundException e) {}
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
    public void morphBtnClick(View view) {
        try {
            int frames = Integer.parseInt(((EditText)findViewById(R.id.frameNum)).getText().toString());
//            @todo implement morphing function
        } catch (NumberFormatException ex) {}
    }
}
