package ca.bcit.zwu56.imagemorpher;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.ParcelFormatException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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

    /** radio button selection change event */
    public void lineEditModeSelect(View view) {
        switch (view.getId()) {
            case R.id.drawRadio:
                MorphImageView.setLineEditMode(MorphImageView.LineEditMode.DRAW);
                break;
            case R.id.editRadio:
                MorphImageView.setLineEditMode(MorphImageView.LineEditMode.EDIT);
                break;
            case R.id.deleteRadio:
                MorphImageView.setLineEditMode(MorphImageView.LineEditMode.DELETE);
                break;
        }
    }

    public void morphBtnClick(View view) {
        try {
            int frames = Integer.parseInt(((EditText)findViewById(R.id.frameNum)).getText().toString());
        } catch (NumberFormatException ex) {}
    }
}
