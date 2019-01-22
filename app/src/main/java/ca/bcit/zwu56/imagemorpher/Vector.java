package ca.bcit.zwu56.imagemorpher;

public class Vector {

    /**
     * calculate and return the dot product of two vector
     * @param v1 vector
     * @param v2 vector
     * @return result of the dot product
     */
    public static double dotProduct(Vector v1, Vector v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }

    // x, y value of the vector
    double x, y;
    // start point and end point, can be null if this vector in constructed using x, y value
    Point strPoint, endPoint;

    /**
     * vector constructor using x and y value
     * @param x coordinate
     * @param y coordinate
     */
    public Vector(double x, double y) {
        this.x  = x;
        this.y = y;
    }

    /**
     * construct vector/line using starting point and end point
     * @param start point
     * @param end point
     */
    public Vector(Point start, Point end) {
        strPoint = start;
        endPoint= end;
        x = end.x - start.x;
        y = end.y - start.y;
    }

    /**
     * copy constructor
     * @param ol other line to be copied
     */
    public Vector(Vector ol) {
        strPoint = new Point(ol.strPoint);
        endPoint = new Point(ol.endPoint);
        x = ol.x;
        y = ol.y;
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
        if (!(obj instanceof Vector))
            return false;

        Vector l = (Vector)obj;
        return strPoint.equals(l.strPoint) && endPoint.equals(l.endPoint);
    }


    /**
     * @return the magnitude of this vector
     */
    public double getAbsolute() {
        return Math.sqrt(x * x + y * y);
    }


    /**
     * @return the normal of this vector
     */
    public Vector getNormal() {
        return new Vector(-y, x);
    }
    /**
     * add another vector to this vector, only change x, y. the origin start and end point of this vector will
     * not be changed
     * @param ov other vector
     */
    public void add(Vector ov) {
        x += ov.x;
        y += ov.y;
    }

    /**
     * magnify this vector with the specified ratio
     * @param ratio for magnification
     * @return this vector after magnification
     */
    public Vector magnify(double ratio) {
        x *= ratio;
        y *= ratio;
        return this;
    }

    /**
     * normalize this vector
     * @return this vector after normalized
     */
    public Vector normalize() {
        double abs = getAbsolute();
        x /= abs;
        y /= abs;
        return this;
    }

    public String toString() {
        return "("+ x + ", " + y + ")";
    }
}
