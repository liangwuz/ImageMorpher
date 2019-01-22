package ca.bcit.zwu56.imagemorpher;

import java.util.List;

public class PointMapping {

    /**
     * get the distance from the specified point to the vector.
     * does not check if the point's fraction offset is within 0 to 1
     * @param vector vector
     * @param point point
     * @return the distance
     */
    public static double getDistance(Vector vector, Point point) {
        Vector pToStr = new Vector(point, vector.strPoint);
        Vector normal = vector.getNormal();
        return Vector.dotProduct(pToStr, normal) / normal.getAbsolute();
    }

    /**
     * calculate the fraction offset of the point projecting onto the vector
     * @param vector to be projected onto
     * @param point for projection
     * @return fraction offset
     */
    public static double getFractionalOffset(Vector vector, Point point) {
        Vector strToP = new Vector(vector.strPoint, point);
        return Vector.dotProduct(strToP, vector) / (vector.x * vector.x + vector.y * vector.y);
    }

    // store intermediate result to prevent duplicate calculation
    private static double distanceToLine, fractionalOffset;
    // calculate the distance and fractional offset from the point to vector
    private static void calHelper(Vector vector, Point point) {
        distanceToLine = getDistance(vector, point);
        fractionalOffset = getFractionalOffset(vector, point);
    }

    /**
     * Calculate and return the source point for mapping to destination point using the lines.
     * @param srcVectors collection of lines in the source image
     * @param dstVectors collection of lines in the destination image
     * @param dstPoint destination point
     * @return source point
     */
    public static Point inverseMapping(List<Vector> srcVectors, List<Vector> dstVectors, Point dstPoint) {
        int size = srcVectors.size();
        double weightSum = 0;
        Vector deltaSum = new Vector(0, 0);

        for (int i = 0; i < size; ++i) {
            calHelper(dstVectors.get(i), dstPoint);

            // use the copy instead of changing the origin line
            Vector srcVector = new Vector(srcVectors.get(i));
            Vector normal = srcVector.getNormal();

            srcVector.magnify(fractionalOffset);
            normal.normalize().magnify(distanceToLine);

            // use the start point to calculate the mapped point regarding to this source line.
            // the srcVector will not be used outside of this loop.
            Point xp = srcVector.strPoint.add(srcVector).subtract(normal);

            if (fractionalOffset < 0) {
                // distance is to the start point
            } else if (fractionalOffset > 1) {
                // distance is to the end point
            }

            // the weight of this source point
            double weight = Math.pow(1.0 / (0.01 + distanceToLine) , 1);
            weightSum += weight;
            // delta  = dest - src
            Vector delta = new Vector(xp, dstPoint);
            delta.magnify(weight);
            deltaSum.add(delta);
        }
        deltaSum.magnify(1.0 / weightSum);
        return dstPoint.subtract(deltaSum);
    }

}
