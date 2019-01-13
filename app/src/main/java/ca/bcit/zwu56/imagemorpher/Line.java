package ca.bcit.zwu56.imagemorpher;

public class Line {
    Point strPoint, endPoint;

    public Line(Point start, Point end) {
        strPoint = start;
        endPoint = end;
    }

    /** return true if the start points of this line and the specified line are equal within
     * deviation and the same to end point. */
    public boolean equalsWithDeviation(Line ol, float deviation) {
        if (this == ol)
            return true;
        return strPoint.equalsWithDeviation(ol.strPoint, deviation)
                && endPoint.equalsWithDeviation(ol.endPoint, deviation);
    }

    /** return true if start or end point is equal within deviation to the specified point. */
    public boolean containsWithDeviation(Point p, float deviation) {
        return strPoint.equalsWithDeviation(p, deviation) || endPoint.equalsWithDeviation(p, deviation);
    }

    /** return true if this start point equal to the other's start point and the same to end point */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Line))
            return false;

        Line l = (Line)obj;
        return strPoint.equals(l.strPoint) && endPoint.equals(l.endPoint);
    }
}
