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

    //------------ static

    static List<MorphImageView> connectedViews = new ArrayList<>();
    private static int lineEditMode = 0x0000000f; // default drawing
    public static final int DRAW_MODE = 0x0000000f;
    public static final int EDIT_MODE = 0x000000ff;
    public static final int DELETE_MODE = 0x000000fe;

    /** change how the drawn lines displaying and editing */
    public static void setLineEditMode(int lm) {
        lineEditMode = lm;
        updateLinesDrawing();
    }

    /** clear all lines on all imageview */
    private static void clearLines() {
        for (MorphImageView v : connectedViews) {
            v.clearDrawnLines();
        }
    }

    private static Paint circlePaint;
    private static int circleRadius = 20;
    /** draw: only draw the lines.
     * edit: draw blue circle on the endpoints
     * delete: draw red circle on the endpoints.*/
    private static void updateLinesDrawing() {
        circlePaint.setColor(Color.RED);
        switch (lineEditMode) {
            case DRAW_MODE:
                for (MorphImageView v : connectedViews) {
                    v.invalidate();
                }
                break;
            case EDIT_MODE:
                circlePaint.setColor(Color.BLUE);
            case DELETE_MODE:
                for (MorphImageView v : connectedViews) {
                    v.invalidate();
                }
        }
    }

    //----------------------- members

    private List<Line> drawnLines = new ArrayList<>();
    private Bitmap originBitmap; //@todo check if bitmap stay unchanged

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

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(Color.RED);
        circlePaint.setStrokeWidth(7);

        connectedViews.add(this); // mirror action on touch event
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
            for (Line l : drawnLines) {
                canvas.drawLine(l.strPoint.x, l.strPoint.y, l.endPoint.x, l.endPoint.y, linePaint);
                if ((lineEditMode & 0x000000f0) != 0) {
                    canvas.drawCircle(l.strPoint.x, l.strPoint.y, circleRadius, circlePaint);
                    canvas.drawCircle(l.endPoint.x, l.endPoint.y, circleRadius, circlePaint);
                }
            }
        if (strPoint != null && endPoint != null) {
            canvas.drawLine(strPoint.x, strPoint.y, endPoint.x, endPoint.y, linePaint);
            canvas.drawCircle(strPoint.x, strPoint.y, circleRadius, circlePaint);
            canvas.drawCircle(endPoint.x, endPoint.y, circleRadius, circlePaint);
        }
    }

    private Point strPoint; // current drawing line start point
    private Point endPoint;
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (lineEditMode) {
                    case DRAW_MODE:
                        drawModeMouseDown(e); break;
                    case EDIT_MODE:
                        editMouseDown(e); break;
                    case DELETE_MODE:
                        deleteMouseDown(e); break;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                switch (lineEditMode) {
                    case DRAW_MODE:
                        drawModeMouseMove(e); break;
                    case EDIT_MODE:
                        editMouseMove(e); break;
                }
                break;

            case MotionEvent.ACTION_UP:
                switch (lineEditMode) {
                    case DRAW_MODE:
                        drawModeMouseUp(e); break;
                    case EDIT_MODE:
                        editMouseUp(e); break;
                }
                break;
        }
        return true;
    }

    /** store the start point of the new line. */
    private void drawModeMouseDown(MotionEvent e) {
        for(MorphImageView view : connectedViews) {
            view.strPoint = new Point(e.getX(), e.getY());
            view.endPoint = view.strPoint;
            view.invalidate();
        }
    }

    private int editLineIndex;
    private Line editLine;
    private Point editPoint;
    /** when click on a valid line, remove this line from the list, and store its origin location.
     * when the mouse release, place back the edited line. */
    private void editMouseDown(MotionEvent e) {
        Point clickedPoint = new Point(e.getX(), e.getY());
        editLineIndex = getLineIndex(clickedPoint, circleRadius);
        if (editLineIndex == -1)
            return;

        editLine = drawnLines.remove(editLineIndex);
        if (editLine.strPoint.equalsWithDeviation(clickedPoint, circleRadius)) {
            editPoint = editLine.strPoint;
            strPoint = editLine.endPoint;
        } else {
            editPoint = editLine.endPoint;
            strPoint = editLine.strPoint;
        }
        endPoint = strPoint;
        invalidate();
    }

    /** delete the clicked line */
    private void deleteMouseDown(MotionEvent e) {
        int index = getLineIndex(e.getX(), e.getY(), circleRadius);
        if (index != -1)
            for (MorphImageView view : connectedViews) {
                view.drawnLines.remove(index); // remove the line
                view.invalidate();
            }
    }

    /** draw the movement of the line on the fly on all imageview */
    private void drawModeMouseMove(MotionEvent e) {
        for(MorphImageView view : connectedViews) {
            view.endPoint = new Point(e.getX(), e.getY());
            view.invalidate();
        }
    }

    private void editMouseMove(MotionEvent e) {
        endPoint = new Point(e.getX(), e.getY());
        invalidate();
    }

    /** add new line to imageview point list. */
    private void drawModeMouseUp(MotionEvent e) {
        for (MorphImageView view : connectedViews) {
            view.drawLine(view.strPoint, view.endPoint);
            view.strPoint = null;
            view.endPoint = null; // clean up
            view.invalidate();
        }
    }

    private void editMouseUp(MotionEvent e) {
        if (editLineIndex == -1)
            return;
        editPoint.setPoint(e.getX(), e.getY());
        drawnLines.add(editLineIndex, editLine);
        strPoint = null;
        endPoint = null;
        invalidate();
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
        int index = getLineIndex(point, deviation);
        return index == -1 ? null : drawnLines.get(index);
    }

    public int getLineIndex(float x, float y, float deviation) {
        return getLineIndex(new Point(x, y), deviation);
    }

    /** get index of the line with the specified endpoint */
    public int getLineIndex(Point point, float deviation) {
        for (int i = 0; i < drawnLines.size(); ++i)
            if (drawnLines.get(i).containsWithDeviation(point, deviation))
                return i;
        return -1;
    }


    public Bitmap getImageBitmap() { return originBitmap; }
    @Override
    public void setImageBitmap(Bitmap bm) {
        MorphImageView.clearLines();
        originBitmap = bm;
        super.setImageBitmap(bm);
    }

    /** return the present line of this view, if the underline line are modified, the
     * display lines of this view will be modified as well */
    public List<Line> getDrawnLines() { return drawnLines; }
    public void setDrawnLines(List<Line> lines) { drawnLines = lines; }
    public void clearDrawnLines() { drawnLines.clear(); }
}
