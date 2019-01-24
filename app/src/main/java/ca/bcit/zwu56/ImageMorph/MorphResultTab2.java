package ca.bcit.zwu56.ImageMorph;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import java.util.List;

public class MorphResultTab2 extends Fragment {

    ViewFlipper imagesFlipper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.morph_result, container, false);

        imagesFlipper = rootView.findViewById(R.id.images_flipper);

        // register swipe gesture to image flipper
        final GestureDetector gestureDetector = new GestureDetector(new MyGestureDetector(imagesFlipper));
        imagesFlipper.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return false;
                } else {
                    return true;
                }
            }
        });

        return rootView;
    }

    /**
     * Display all the images for flipping
     * @param images all images
     */
    public void displayImages(List<Bitmap> images) {
        imagesFlipper.removeAllViews();

        for (Bitmap bitmap : images) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageBitmap(bitmap);
            imagesFlipper.addView(imageView);
        }

    }

    public void startAutoSwipe(int ms) {
        imagesFlipper.setFlipInterval(ms);
        imagesFlipper.startFlipping();
    }

    public void stopAutoSwipe() {
        imagesFlipper.stopFlipping();
    }
}

// image swipe gesture.
class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private ViewFlipper viewFlipper;

    MyGestureDetector(ViewFlipper viewFlipper) {
        this.viewFlipper = viewFlipper;
    }

    @Override
    public boolean onFling(MotionEvent str, MotionEvent end, float velocityX, float velocityY) {
        if (Math.abs(str.getY() - end.getY()) > SWIPE_MAX_OFF_PATH)
            return false;
        if (str.getX() - end.getX() > SWIPE_MIN_DISTANCE
                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            viewFlipper.showNext();
        } else if (end.getX() - str.getX() > SWIPE_MIN_DISTANCE
                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            viewFlipper.showPrevious();
        }
        return super.onFling(str, end, velocityX, velocityY);
    }
}