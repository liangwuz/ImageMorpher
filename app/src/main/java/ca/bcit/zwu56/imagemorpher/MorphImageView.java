package ca.bcit.zwu56.imagemorpher;

import android.content.Context;
import android.graphics.*;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.*;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MorphImageView extends ImageView {
    private List<Line> drawnLines = new ArrayList<>();
    private Canvas canvas;
    private Bitmap bitmap;

    private Paint linePaint;

    public MorphImageView(Context context) {
        this(context, null);
    }

    public MorphImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(7);

    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Line l : drawnLines)
             canvas.drawLine(l.strPoint.x, l.strPoint.y, l.endPoint.x, l.endPoint.y, linePaint);
        if (strPoint != null)
            canvas.drawLine(strPoint.x, strPoint.y, endPoint.x, endPoint.y, linePaint);
    }

    private Point strPoint; // current drawing line start point
    private Point endPoint;
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent e) {
        float x = e.getX(), y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                strPoint = new Point(x, y);
                endPoint = strPoint;
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                endPoint = new Point(x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                endPoint = new Point(x, y);
                drawnLines.add(new Line(strPoint, endPoint));
                invalidate();
                break;
        }
        return true;
    }

    /** line paint setter */
    public void setLinePaint(Paint paint) { linePaint = paint; }
    /** return a copy of the line paint */
    public Paint getLinePaint() { return new Paint(linePaint); }

    /** draw new line using chosen paint */
    public void drawLine(float x0, float y0, float x1, float y1) {
        drawnLines.add(new Line(new Point(x0, y0), new Point(x1, y1)));
        invalidate();
    }

    public void drawLine(Line line) {
        drawnLines.add(line);
        invalidate();
    }
}
