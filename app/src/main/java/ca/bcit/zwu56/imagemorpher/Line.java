package ca.bcit.zwu56.imagemorpher;

public class Line {
    Point strPoint, endPoint;

    /**
     * construct a new line
     * @param start starting point
     * @param end ending point
     */
    public Line(Point start, Point end) {
        strPoint = start;
        endPoint = end;
    }

    /**
     * return true if start or end point of this line is close to the provided point.
     * close meaning: the provided point is within the circle center at the endpoint with a radius of
     * the provided deviation
     * @param p point used for checking
     * @param deviation circle radius
     * @return true if this line contains the point
     */
    public boolean containsWithDeviation(Point p, float deviation) {
        return strPoint.isWithinRadius(p, deviation) || endPoint.isWithinRadius(p, deviation);
    }

    /**
     * return true if both the start and end point of the provided line are equal to the start and
     * end point of this line.
     * @param obj another line
     * @return true if equal
     */
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
