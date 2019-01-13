package ca.bcit.zwu56.imagemorpher;

public class Point {
    final float x, y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /** return true if the specified point is with the radius of deviation of this point. */
    public boolean equalsWithDeviation(Point p, float deviation) {
        if (this == p)
            return true;
        float xdiff = x - p.x, ydiff = y - p.y;
        return deviation * deviation > xdiff * xdiff + ydiff * ydiff;
    }

    /** true if x and y differences are within 0.000001. */
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
