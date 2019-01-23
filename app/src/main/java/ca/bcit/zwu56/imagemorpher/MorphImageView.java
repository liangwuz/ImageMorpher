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

    //------------ static

    /** ImageView container for start and end image, so event on one view can update all */
    static List<MorphImageView> connectedViews = new ArrayList<>();
    private static int lineEditMode = 0x0000000f; // default drawing mode

    public static final int DRAW_MODE = 0x0000000f;
    // second f from the right indicate drawing circles at the end of the line
    public static final int EDIT_MODE = 0x000000ff;
    public static final int DELETE_MODE = 0x000000fe;

    /** change how the drawn lines displaying and can be edited */
    public static void setLineEditMode(int lm) {
        lineEditMode = lm;
        updateLinesDrawing(); // update the how the line are drawn on the screen
    }

    /** paint for the circle at the end of user drawn lines to indicate edition. */
    private static Paint circlePaint;
    private static int circleRadius = 40;

    /** DRAW_MODE: only draw the lines.
     * EDIT_MODE: draw blue circle on the endpoints
     * DELETE_MODE: draw red circle on the endpoints.*/
    private static void updateLinesDrawing() {
        circlePaint.setColor(Color.RED);
        switch (lineEditMode) {
            case EDIT_MODE:
                circlePaint.setColor(Color.BLUE); // blue circle color
            case DELETE_MODE: // red circle color
            case DRAW_MODE:
                for (MorphImageView v : connectedViews) {
                    v.invalidate(); // onDraw will update the views
                }
        }
    }

    /** clear all user drawn lines on all ImageViews.
     * called when user open a different image for editing */
    private static void clearAllLines() {
        for (MorphImageView v : connectedViews) {
            v.clearDrawnLines();
        }
    }

    //----------------------- members

    /** all drawn line of this view */
    private List<Vector> drawnLines = new ArrayList<>();
    /** image bitmap */
    private Bitmap originBitmap;
    /** paint for drawing lines */
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

        // default circle paint
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(Color.RED);
        circlePaint.setStrokeWidth(7);

        // register to static member. events on one view can update all views.
        connectedViews.add(this);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
            for (Vector l : drawnLines) { // draw all stored lines
                canvas.drawLine(l.strPoint.x, l.strPoint.y, l.endPoint.x, l.endPoint.y, linePaint);
                if ((lineEditMode & 0x000000f0) != 0) { // draw circle to indicate edition is allowed
                    canvas.drawCircle(l.strPoint.x, l.strPoint.y, circleRadius, circlePaint);
                    canvas.drawCircle(l.endPoint.x, l.endPoint.y, circleRadius, circlePaint);
                }
            }
        if (strPoint != null && endPoint != null) { // the line the user is drawing / editing
            canvas.drawLine(strPoint.x, strPoint.y, endPoint.x, endPoint.y, linePaint);
            canvas.drawCircle(strPoint.x, strPoint.y, circleRadius, circlePaint);
            canvas.drawCircle(endPoint.x, endPoint.y, circleRadius, circlePaint);
        }
    }

    /** current editing line start point */
    private Point strPoint;
    private Point endPoint;
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (lineEditMode) {
                    case DRAW_MODE:
                        drawModeMouseDown(e); break;
                    case EDIT_MODE:
                        editModeMouseDown(e); break;
                    case DELETE_MODE:
                        deleteModeMouseDown(e); break;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                switch (lineEditMode) {
                    case DRAW_MODE:
                        drawModeMouseMove(e); break;
                    case EDIT_MODE:
                        editModeMouseMove(e); break;
                }
                break;

            case MotionEvent.ACTION_UP:
                switch (lineEditMode) {
                    case DRAW_MODE:
                        drawModeMouseUp(e); break;
                    case EDIT_MODE:
                        editModeMouseUp(e); break;
                }
                break;
        }
        return true;
    }

    /** store the start point of the new line on all views. */
    private void drawModeMouseDown(MotionEvent e) {
        for(MorphImageView view : connectedViews) {
            view.strPoint = new Point(e.getX(), e.getY());
            view.endPoint = view.strPoint; // store end point to prevent onDraw line jumping
            view.invalidate();
        }
    }

    /** remember the location of the editing line, when finish editing, put the line back to correct location
     * to line up with other views. */
    private int editLineIndex;
    private Vector editLine;
    /** editing point to be changed coordinates. */
    private Point editPoint;
    /** when click on a valid line, remove this line from the list, and store its origin location.
     * when the mouse release, place back the edited line. */
    private void editModeMouseDown(MotionEvent e) {
        Point clickedPoint = new Point(e.getX(), e.getY());
        editLineIndex = getLineIndex(clickedPoint, circleRadius);
        if (editLineIndex == -1)
            return;

        // remove the line from collection, so it would not be drawn.
        editLine = drawnLines.remove(editLineIndex);
        // strPoint is the fixed point that does not changed, store the reference of the editing point
        // for updating in mouse down
        if (editLine.strPoint.isWithinRadius(clickedPoint, circleRadius)) {
            editPoint = editLine.strPoint;
            strPoint = editLine.endPoint;
            endPoint = editPoint;
        } else {
            editPoint = editLine.endPoint;
            strPoint = editLine.strPoint;
            endPoint = editPoint;
        }
        invalidate();
    }

    /** delete the clicked line */
    private void deleteModeMouseDown(MotionEvent e) {
        int index = getLineIndex(e.getX(), e.getY(), circleRadius);
        if (index != -1)
            for (MorphImageView view : connectedViews) {
                view.drawnLines.remove(index); // remove the line
                view.invalidate();
            }
    }

    /** draw the movement of the line on the fly on all views by updating endPoint */
    private void drawModeMouseMove(MotionEvent e) {
        for(MorphImageView view : connectedViews)
            view.editModeMouseMove(e);
    }

    /** updating the endPoint of the current drawing line. */
    private void editModeMouseMove(MotionEvent e) {
        endPoint = new Point(e.getX(), e.getY());
        invalidate();
    }

    /** add new line to drawn line list, and clean up the drawing line info. */
    private void drawModeMouseUp(MotionEvent e) {
        for (MorphImageView view : connectedViews) {
            view.drawLine(view.strPoint, view.endPoint);
            view.strPoint = null;
            view.endPoint = null; // clean up, so onDraw method would not drawn it
            view.invalidate();
        }
    }

    /** update the edited line and put it back to the right index to line up with other view. */
    private void editModeMouseUp(MotionEvent e) {
        if (editLineIndex == -1)
            return; // error checking
        editPoint.setPoint(e.getX(), e.getY());
        drawnLines.add(editLineIndex, editLine);
        strPoint = null;
        endPoint = null;
        invalidate();
    }

    /**
     * draw this line by putting it into the line list, and onDraw method will draw it.
     * @param line to be added
     */
    public void drawLine(Vector line) {
        drawnLines.add(line);
    }
    public void drawLine(Point start, Point end) {
        drawLine(new Vector(start, end));
    }

    /**
     * return the index of the line includes point (x, y), with the deviation radius.
     * if the start or end point of a line is within the circle, the index of this line is returned.
     * if there are multiple lines, only the first qualified line is returned
     * @param point of the line
     * @param deviation bearable deviation of this point
     * @return line index or -1
     */
    private int getLineIndex(Point point, float deviation) {
        for (int i = 0; i < drawnLines.size(); ++i)
            if (drawnLines.get(i).containsWithDeviation(point, deviation))
                return i;
        return -1;
    }
    private int getLineIndex(float x, float y, float deviation) {
        return getLineIndex(new Point(x, y), deviation);
    }

    /**
     * get the image bitmap of this view
     * @return image bitmap
     */
    public Bitmap getImageBitmap() { return originBitmap; }

    @Override
    public void setImageBitmap(Bitmap bm) {
        MorphImageView.clearAllLines();
        originBitmap = bm;
        super.setImageBitmap(bm);
    }

    /**
     * get the reference to the drawn lines of this view.
     * if the lines in this reference is modified, the lines of this view will be modified as well
     * @return reference to all drawn lines
     */
    public List<Vector> getDrawnLines() { return drawnLines; }

    /**
     * set new lines for this view, the view will not be update immediately, invalidate method need
     * to be called
     * @param lines new lines to be drawn on this view
     */
    public void setDrawnLines(List<Vector> lines) { drawnLines = lines; }

    /**
     * Remove all drawn lines, the view will not be update immediately, invalidate method need
     * to be called
     */
    public void clearDrawnLines() { drawnLines.clear(); }
}
