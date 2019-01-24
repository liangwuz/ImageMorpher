package ca.bcit.zwu56.ImageMorph;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class MorphResultTab2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.morph_result, container, false);

        displayImage = rootView.findViewById(R.id.display_image);

        return rootView;
    }

    // current display image
    private ImageView displayImage;

    public void displayImages(List<Bitmap> images) {
        displayImage.setImageBitmap(images.get(0));
    }
}