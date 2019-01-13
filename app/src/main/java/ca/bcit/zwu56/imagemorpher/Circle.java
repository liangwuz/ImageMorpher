package ca.bcit.zwu56.imagemorpher;

import android.graphics.Paint;

public class Circle {
    final float x;
    final float y;
    final float radius;
    final Paint paint;

    Circle(float x, float y, float radius, Paint paint) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.paint = paint;
    }
}
