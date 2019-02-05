package ca.bcit.zwu56.ImageMorph;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileNotFoundException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    pickImageTab = new PickImageTab1();
                    return pickImageTab;
                case 1:
                    displayTab = new MorphResultTab2();
                    return displayTab;
                default:
                    return null;
            }
        }
        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }


    // tabs for picking images and drawing lines
    private PickImageTab1 pickImageTab;
    private MorphResultTab2 displayTab;

    /** open image picker */
    public void openImageClick(View view) {
//        pickImageTab.openImageClick(view);
        int requestCode;
        switch (view.getId()) {
            case R.id.openSrcImgBtn:
                requestCode = PickImageTab1.strImgRequestCode;
                break;
            case R.id.openDesImgBtn:
                requestCode = PickImageTab1.endImgRequestCode;
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
        try {
            Bitmap immuBitmap = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(data.getData()));
            pickImageTab.onPickImgResult(immuBitmap, requestCode);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** radio button selection change event */
    public void lineEditModeSelect(View view) {
        pickImageTab.lineEditModeSelect(view);
    }

    /** start morphing */
    public void morphBtnClick(View view) {
        pickImageTab.morphBtnClick();
    }

    /**
     * post the images to display tab for displaying
     * @param bitmaps images
     */
    public void browseMorphResult(List<Bitmap> bitmaps) {
        // parse result to tab2 gallery
        if (bitmaps == null || bitmaps.size() == 0)
            return;
        displayTab.displayImages(bitmaps);
        mViewPager.setCurrentItem(1);
    }

    /** start auto result images flipping */
    public void flipBtnClick(View view) {
        Button flipBtn = findViewById(R.id.start_stop_flip_btn);
        if (flipBtn.getText().toString().equals("START")) {
            flipBtn.setText("STOP");
            flipBtn.setTextColor(Color.RED);
            try {
                Integer ms = Integer.parseInt(((EditText)findViewById(R.id.flip_gap)).getText().toString());
                displayTab.startAutoSwipe(ms);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        } else {
            flipBtn.setText("START");
            flipBtn.setTextColor(Color.BLACK);
            displayTab.stopAutoSwipe();
        }
    }

    public void gotoSrcFrame(View view) {
        displayTab.gotoSrcFrame();
    }

    public void gotoDstFrame(View view) {
        displayTab.gotoDstFrame();
    }

    public void gotoPrevFrame(View view) {
        displayTab.gotoPrevFrame();
    }

    public void gotoNextFrame(View view) {
        displayTab.gotoNextFrame();
    }
}
