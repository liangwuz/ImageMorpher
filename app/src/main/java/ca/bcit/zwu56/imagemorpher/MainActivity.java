package ca.bcit.zwu56.imagemorpher;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MorphImageView srcImgView, desImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        srcImgView = findViewById(R.id.srcImgView);
        desImgView = findViewById(R.id.desImgView);
    }
}
