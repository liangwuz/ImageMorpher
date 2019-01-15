package ca.bcit.zwu56.imagemorpher;

public class Point {
    float x, y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Reset x, y of this point
     * @param X
     * @param Y
     */
    public void setPoint(float X, float Y) {
        x = X;
        y = Y;
    }

    /**
     * check if the other point is within radius of this point
     * @param op other point
     * @param radius bearable radius
     * @return true if the specified point is within radius
     */
    public boolean isWithinRadius(Point op, float radius) {
        if (this == op)
            return true;
        float xdiff = x - op.x, ydiff = y - op.y;
        return radius * radius > xdiff * xdiff + ydiff * ydiff;
    }

    /**
     * check if two point are the same with bearable deviation of 0.000001
     * @param obj other point
     * @return true if x and y differences are within 0.000001.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Point))
            return false;

        Point p = (Point)obj;
        return Math.abs(x - p.x) < 0.000001 && Math.abs(y - p.y) < 0.000001;
    }
}
