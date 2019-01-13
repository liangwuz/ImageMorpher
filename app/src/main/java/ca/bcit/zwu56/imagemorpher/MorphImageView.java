package ca.bcit.zwu56.imagemorpher;

import android.content.Context;
import android.graphics.*;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MorphImageView extends ImageView {
    static List<MorphImageView> connectedViews = new ArrayList<>();
    enum EditLineMode {DRAW, EDIT, REMOVE }

    private List<Line> drawnLines = new ArrayList<>();
    private Canvas usingCanvas; // @todo
    private Bitmap originBitmap; // @todo

    private Paint linePaint;

    public MorphImageView(Context context) {
        this(context, null);
    }

    public MorphImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // default line paint
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(7);

        connectedViews.add(this);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Line l : drawnLines)
             canvas.drawLine(l.strPoint.x, l.strPoint.y, l.endPoint.x, l.endPoint.y, linePaint);
        for (Circle c : drawnCircles)
            canvas.drawCircle(c.x, c.y, c.radius, c.paint);
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
                for(MorphImageView view : connectedViews) {
                    view.strPoint = new Point(x, y);
                    view.endPoint = strPoint;
                    view.invalidate();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                for(MorphImageView view : connectedViews) {
                    view.endPoint = new Point(x, y);
                    view.invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                for(MorphImageView view : connectedViews) {
                    view.endPoint = new Point(x, y);
                    view.drawnLines.add(new Line(strPoint, endPoint));
                    view.invalidate();
                }
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
        drawLine(new Point(x0, y0), new Point(x1, y1));
    }

    public void drawLine(Point start, Point end) {
        drawLine(new Line(start, end));
    }

    public void drawLine(Line line) {
        drawnLines.add(line);
    }

    /** remove the line start from (x0, y0) and end at (x1, y1), with the deviation radius,
     * if there are multiple lines, only the first qualified line is removed */
    public void removeLine(float x0, float y0, float x1, float y1, float deviation) {
        drawnLines.remove(getLine(x0, y0, x1, y1, deviation));
    }

    public void removeLine(Point str, Point end, float deviation) {
        drawnLines.remove(getLine(str, end, deviation));
    }

    /** remove the line with an end point at (x, y) within deviation radius,
     * if there are multiple lines, only the first qualified line is removed */
    public void removeLine(float x, float y, float deviation) {
        drawnLines.remove(getLine(x, y, deviation));
    }

    public void removeLine(Point point, float deviation) {
        drawnLines.remove(getLine(point, deviation));
    }

    public void removeLine(Line ol, float deviation) {
        for (Line l : drawnLines)
            if (l.equalsWithDeviation(ol, deviation)) {
                drawnLines.remove(l);
                return;
            }
    }

    /** remove the specified line. */
    public void removeLine(Line l) {
        drawnLines.remove(l);
    }

    /** return the line start from (x0, y0) and end at (x1, y1), with the deviation radius or
     * null is return, if there are multiple lines, only the first qualified line is returned */
    public Line getLine(float x0, float y0, float x1, float y1, float deviation) {
        return getLine(new Point(x0, y0), new Point(x1, y1), deviation);
    }

    public Line getLine(Point start, Point end, float deviation) {
        Line check = new Line(start, end);
        for (Line l : drawnLines)
            if (l.equalsWithDeviation(check, deviation))
                return l;
        return null;
    }

    /** return the line contains end Point (x0, y0), with the deviation radius,
     * if there are multiple lines, only the first qualified line is returned */
    public Line getLine(float x, float y, float deviation) {
        return getLine(new Point(x, y), deviation);
    }

    public Line getLine(Point point, float deviation) {
        for (Line l : drawnLines)
            if (l.containsWithDeviation(point, deviation))
                return l;
        return null;
    }

    public Bitmap getOriginBitmap() { return originBitmap; }
    public void setOriginBitmap(Bitmap b) {
//        @todo implement setting new bitmap for drawing
    }

    /** return the present line of this view, if the underline line are modified, the
     * display lines of this view will be modified as well */
    public List<Line> getDrawnLines() { return drawnLines; }
    public void setDrawnLines(List<Line> lines) { drawnLines = lines; }
    public void clearDrawnLines() { drawnLines.clear(); }


    private final List<Circle> drawnCircles = new ArrayList<>();
    public void drawCircle(float x, float y, float radius, Paint paint) {
        drawnCircles.add(new Circle(x, y, radius, paint));
    }
    public void clearCircles() { drawnCircles.clear(); }
}
