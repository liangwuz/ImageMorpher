package ca.bcit.zwu56.imagemorpher;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    MorphImageView srcImgView, desImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        srcImgView = findViewById(R.id.srcImgView);
        desImgView = findViewById(R.id.desImgView);

        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(7);
        srcImgView.setLinePaint(linePaint);
        srcImgView.drawLine(0, 0, 100, 100);
    }
}
