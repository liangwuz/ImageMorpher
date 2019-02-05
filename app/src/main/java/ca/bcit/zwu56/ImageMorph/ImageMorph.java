package ca.bcit.zwu56.ImageMorph;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

class ImageMorph {

    /**
     * get the distance from the specified point to the vector.
     * does not check if the point's fraction offset is within 0 to 1
     * @param vector vector
     * @param point point
     * @return the distance
     */
    private static double getDistance(Vector vector, Point point) {
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
    private static double getFractionalOffset(Vector vector, Point point) {
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

//    /**
//     * Calculate and return the source point for mapping to destination point using the lines.
//     * @param srcVectors collection of lines in the source image
//     * @param dstVectors collection of lines in the destination image
//     * @param dstPoint destination point
//     * @return source point
//     */
//    private static Point inverseMapping(List<Vector> srcVectors, List<Vector> dstVectors, Point dstPoint) {
//        int size = srcVectors.size();
//        double weightSum = 0;
//        // accumulation of delta * weight
//        Vector deltaSum = new Vector(0, 0);
//
//        for (int i = 0; i < size; ++i) {
//            calHelper(dstVectors.get(i), dstPoint);
//
//            // use the copy instead of changing the origin line
//            Vector srcVector = new Vector(srcVectors.get(i));
//            Vector normal = srcVector.getNormal();
//
//            // vectors that the start point will walk along
//            srcVector.magnify(fractionalOffset);
//            normal.normalize().magnify(distanceToLine);
//
//            // use the start point to calculate the mapped point regarding to this source line.
//            // the srcVector will not be used latter of this loop, so it wound be affected.
//            Point xp = srcVector.strPoint.add(srcVector).subtract(normal);
//
//            // recalculate distance for calculating weight if fractionalOffset is outside 0-1
//            if (fractionalOffset < 0) {
//                // distance is to the start point
//                double delX = dstPoint.x - dstVectors.get(i).strPoint.x;
//                double delY = dstPoint.y - dstVectors.get(i).strPoint.y;
//               distanceToLine = Math.sqrt(delX * delX + delY * delY);
//            } else if (fractionalOffset > 1) {
//                // distance is to the end point
//                double delX = dstPoint.x - dstVectors.get(i).endPoint.x;
//                double delY = dstPoint.y - dstVectors.get(i).endPoint.y;
//                distanceToLine = Math.sqrt(delX * delX + delY * delY);
//            }
//
//            // the weight of this source point
//            double weight = Math.pow(1.0 / (0.01 + distanceToLine) , 2);
//            weightSum += weight;
//            // delta  = dest - src
//            Vector delta = new Vector(xp, dstPoint);
//            delta.magnify(weight);
//            deltaSum.add(delta);
//        }
//        deltaSum.magnify(1.0 / weightSum);
//        return (new Point(dstPoint)).subtract(deltaSum);
//    }


    private static Point srcMapPoint, dstMapPoint;
    // calculate the mapped point in both start image and end image at the same time using static variable
    private static void inverseMapping(List<Vector> srcVectors, List<Vector> intermediateVectors,
                                        List<Vector> dstVectors, Point point) {
        int size = srcVectors.size();
        double weightSum = 0;
        // accumulation of delta * weight
        Vector srcDeltaSum = new Vector(0, 0), dstDeltaSum = new Vector(0, 0);

        for (int i = 0; i < size; ++i) {
            // use static variable to avoid duplicate calculation
            calHelper(intermediateVectors.get(i), point);

            // use the copy instead of changing the origin line
            Vector srcVector = new Vector(srcVectors.get(i));
            Vector srcNormal = srcVector.getNormal();
            // vectors that the start point will walk along
            srcVector.magnify(fractionalOffset);
            srcNormal.normalize().magnify(distanceToLine);

            // duplication for end image
            Vector dstVector = new Vector(dstVectors.get(i));
            Vector dstNormal = dstVector.getNormal();
            dstVector.magnify(fractionalOffset);
            dstNormal.normalize().magnify(distanceToLine);

            // use the start point to calculate the mapped point regarding to this source line.
            // the srcVector will not be used latter of this loop, so it wound be affected.
            Point srcMapPoint = srcVector.strPoint.add(srcVector).subtract(srcNormal);
            Point dstMapPoint = dstVector.strPoint.add(dstVector).subtract(dstNormal);

            // recalculate distance for calculating weight if fractionalOffset is outside 0-1
            if (fractionalOffset < 0) {
                // distance is to the start point
                double delX = point.x - intermediateVectors.get(i).strPoint.x;
                double delY = point.y - intermediateVectors.get(i).strPoint.y;
                distanceToLine = Math.sqrt(delX * delX + delY * delY);
            } else if (fractionalOffset > 1) {
                // distance is to the end point
                double delX = point.x - intermediateVectors.get(i).endPoint.x;
                double delY = point.y - intermediateVectors.get(i).endPoint.y;
                distanceToLine = Math.sqrt(delX * delX + delY * delY);
            }

            // the weight of this mapped point
            double weight = Math.pow(1.0 / (0.01 + distanceToLine) , 2);
            weightSum += weight;
            // delta  = dest - src
            Vector delta = new Vector(srcMapPoint, point);
            delta.magnify(weight);
            srcDeltaSum.add(delta);

            // delta for dst image
            delta = new Vector(dstMapPoint, point);
            delta.magnify(weight);
            dstDeltaSum.add(delta);
        }
        srcDeltaSum.magnify(1.0 / weightSum);
        srcMapPoint = (new Point(point)).subtract(srcDeltaSum);

        dstDeltaSum.magnify(1.0 / weightSum);
        dstMapPoint = (new Point(point).subtract(dstDeltaSum));
    }

    /**
     * calculate and return the specified number of frames based on the provided lines and images
     * @param numOfFrame number of intermediate between start and end images
     * @param srcImg start image
     * @param dstImg end image
     * @param srcLines lines on the start image
     * @param dstLines paired lines on the end iamge
     * @return list of intermediate frames
     */
    static List<Bitmap> drawIntermediateFrames(int numOfFrame, Bitmap srcImg, Bitmap dstImg,
                                                      List<Vector> srcLines, List<Vector> dstLines) {
        // x, y coordinates of a point and image size
        int width = srcImg.getWidth(), height = srcImg.getHeight();
        // result bitmap container
        List<Bitmap> result = new ArrayList<>();

        for (int i = 0; i < numOfFrame; ++i) {
            // intermediate lines for mapping pixels
            List<Vector> intermediateLines =  new ArrayList<>();
            // cross resolve weight for the start image and end image
            double dstWeight = 1.0 / (numOfFrame + 1) * (i + 1);
            double srcWeight = 1 - dstWeight;

            // create numOfFrame empty intermediate bitmap / images
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            // create intermediate lines
            for (int j = 0; j < srcLines.size(); ++j) {
                // a pair of lines from the start image and end image
                Vector srcLine = srcLines.get(j);
                Vector dstLine = dstLines.get(j);

                // intermediate line start point
                float x = calIntermediateNum(srcLine.strPoint.x, dstLine.strPoint.x, i, numOfFrame);
                float y = calIntermediateNum(srcLine.strPoint.y, dstLine.strPoint.y, i, numOfFrame);
                Point lineStrPoint = new Point(x, y);

                // intermediate line end point
                x = calIntermediateNum(srcLine.endPoint.x, dstLine.endPoint.x, i, numOfFrame);
                y = calIntermediateNum(srcLine.endPoint.y, dstLine.endPoint.y, i, numOfFrame);
                Point lineEndPoint = new Point(x, y);
                intermediateLines.add(new Vector(lineStrPoint, lineEndPoint));
            }

            // for each pixel in the empty bitmap do:
            // get corresponding pixel from the src image
            // get corresponding pixel from the end image
            // weight * src pixel + weight * dst pixel fro cross resolve
            // draw the result pixel back to the bitmap
            for (int x = 0; x < width; ++x) {
                for(int y = 0; y < height; ++y) {
                    Point pixelPoint = new Point(x, y); // pixel point to be painted
//                    Point srcMapPoint = inverseMapping(srcLines, intermediateLines, pixelPoint); // pixel point from start image
//                    Point dstMapPoint = inverseMapping(dstLines, intermediateLines, pixelPoint);

                    inverseMapping(srcLines, intermediateLines, dstLines, pixelPoint);

                    // result pixel argb
                    int a = 0, r = 0, g = 0, b = 0;

                    // pixel value from start image
                    int pixel = getPixelAtPoint(srcImg, srcMapPoint);
                    a += Color.alpha(pixel) * srcWeight;
                    r += Color.red(pixel) * srcWeight;
                    g += Color.green(pixel) * srcWeight;
                    b += Color.blue(pixel) * srcWeight;

                    // pixel value from end image
                    pixel = getPixelAtPoint(dstImg, dstMapPoint);
                    a += Color.alpha(pixel) * dstWeight;
                    r += Color.red(pixel) * dstWeight;
                    g += Color.green(pixel) * dstWeight;
                    b += Color.blue(pixel) * dstWeight;

                    Paint paint = new Paint();
                    paint.setColor(Color.argb(a, r, g, b));
                    canvas.drawPoint(x, y, paint);
                }
            }
            result.add(bitmap);
        }
        srcMapPoint = null;
        dstMapPoint = null;
        return result;
    }

    // calculate the intermediate coordinate base on the frame index, start and end coordinates
    private static float calIntermediateNum(float src, float dst, int index, int total) {
         return (dst - src) / (total + 1) * (index+1) + src;
    }

    // get the pixel at the specified point, if x, y coordinate is out of bound, it be rounded to the bound.
    private static int getPixelAtPoint(Bitmap img, Point point) {
        int
            x = (int)point.x, // x,y coordinate
            y = (int)point.y,
            width = img.getWidth(), // image bound
            height = img.getHeight();

        if (x < 0)
            x = 0;
        else if (x >= width)
            x = width-1;

        if (y < 0)
            y = 0;
        else if (y >= height)
            y = height-1;

        return img.getPixel(x, y);
    }
}
