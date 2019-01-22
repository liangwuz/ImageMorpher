package ca.bcit.zwu56.imagemorpher;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
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
                double delX = dstPoint.x - dstVectors.get(i).strPoint.x;
                double delY = dstPoint.y - dstVectors.get(i).strPoint.y;
                // distance is to the start point
               distanceToLine = Math.sqrt(delX * delX + delY * delY);
            } else if (fractionalOffset > 1) {
                // distance is to the end point
                double delX = dstPoint.x - dstVectors.get(i).endPoint.x;
                double delY = dstPoint.y - dstVectors.get(i).endPoint.y;
                distanceToLine = Math.sqrt(delX * delX + delY * delY);
            }

            // the weight of this source point
            double weight = Math.pow(1.0 / (0.01 + distanceToLine) , 2);
            weightSum += weight;
            // delta  = dest - src
            Vector delta = new Vector(xp, dstPoint);
            delta.magnify(weight);
            deltaSum.add(delta);
        }
        deltaSum.magnify(1.0 / weightSum);
        return (new Point(dstPoint)).subtract(deltaSum);
    }

    public static List<Bitmap> drawIntermediateFrames(int numOfFrame, Bitmap srcImg, Bitmap dstImg,
                                                      List<Vector> srcLines, List<Vector> dstLines) {
        int x,y, width = srcImg.getWidth(), height = srcImg.getHeight();
        Point lineStrPoint, lineEndPoint;
        Vector srcLine, dstLine;
        List<Vector> intermediateLines =  new ArrayList<>();
        // result bitmap container
        List<Bitmap> result = new ArrayList<>();

        for (int i = 0; i < numOfFrame; ++i) {
            double dstWeight = 1.0 / (numOfFrame + 1);
            double srcWeight = 1 - dstWeight;

            // create numOfFrame empty bitmap
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            // create intermediate lines
            for (int j = 0; j < srcLines.size(); ++j) {
                srcLine = srcLines.get(j);
                dstLine = dstLines.get(j);

                // intermediate line start point
                x = calIntermediateNum(srcLine.strPoint.x, dstLine.strPoint.x, i, numOfFrame);
                y = calIntermediateNum(srcLine.strPoint.y, dstLine.strPoint.y, i, numOfFrame);
                lineStrPoint = new Point(x, y);

                // intermediate line end point
                x = calIntermediateNum(srcLine.endPoint.x, dstLine.endPoint.x, i, numOfFrame);
                y = calIntermediateNum(srcLine.endPoint.y, dstLine.endPoint.y, i, numOfFrame);
                lineEndPoint = new Point(x, y);
                intermediateLines.add(new Vector(lineStrPoint, lineEndPoint));
            }

            // for each pixel in the empty bitmap do:
            // get corresponding pixel from the src image
            // get corresponding pixel from the end image
            // weight * src pixel + weight * dst pixel fro cross resolve
            // draw the point the the result pixel
            for (x = 0; x < width; ++x) {
                for(y = 0; y < height; ++y) {
                    Point pixelPoint = new Point(x, y);
                    Point srcImgPoint = inverseMapping(srcLines, intermediateLines, pixelPoint);
                    Point dstImgPoint = inverseMapping(dstLines, intermediateLines, pixelPoint);

                    int a = 0, r = 0, g = 0, b = 0;

                    int pixel = getPixelAtPoint(srcImg, srcImgPoint);
                    a += Color.alpha(pixel) * srcWeight;
                    a =255;
                    r += Color.red(pixel) * srcWeight;
                    g += Color.green(pixel) * srcWeight;
                    b += Color.blue(pixel) * srcWeight;
//                    pixel = getPixelAtPoint(dstImg, dstImgPoint);
//                    a += Color.alpha(pixel) * dstWeight;
//                    r += Color.red(pixel) * dstWeight;
//                    g += Color.green(pixel) * dstWeight;
//                    b += Color.blue(pixel) * dstWeight;

                    int color = Color.argb(a, r, g, b);
                    Paint paint = new Paint();
                    paint.setColor(color);
                    canvas.drawPoint(x, y, paint);
                }
            }

            result.add(bitmap);
        }
        return result;
    }

    private static int calIntermediateNum(float src, float dst, int index, int total) {
         return Math.round((dst - src) / (total + 1) * (index+1) + src);
    }

    private static int getPixelAtPoint(Bitmap img, Point point) {
        int x = (int)point.x, y = (int)point.y, width = img.getWidth(), height = img.getHeight();

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
