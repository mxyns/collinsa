package fr.insalyon.mxyns.collinsa.utils.geo;


import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;

/**
 * Classe de calculs et méthodes géométriques
 */
public class Geometry {

    public static Vec2f[] getRectangleCorners(Rect rect) {

        Vec2f v1 = new Vec2f((float)Math.cos(rect.getRot()), (float)Math.sin(rect.getRot()));
        Vec2f v2 = new Vec2f(-v1.y, v1.x);

        v1.mult(rect.getSize().x * 0.5f);
        v2.mult(rect.getSize().y * 0.5f);

        Vec2f pos = rect.getPos();

        return new Vec2f[] {
                             pos.copy().sub(v1).sub(v2),
                             pos.copy().sub(v1).add(v2),
                             pos.copy().add(v1).add(v2),
                             pos.copy().add(v1).sub(v2),
                             };
    }

    public static Vec2f getMinPos(Vec2f... vecs) {

        Vec2f minVec = vecs[0].copy();
        for (Vec2f vec : vecs) {
            if (vec.x < minVec.x)
                minVec.setX(vec.x);
            if (vec.y < minVec.y)
                minVec.setY(vec.y);
        }

        return minVec;
    }

    public static Vec2f getMaxPos(Vec2f... vecs) {

        Vec2f maxVec = vecs[0].copy();
        for (Vec2f vec : vecs) {
            if (vec.x > maxVec.x)
                maxVec.setX(vec.x);
            if (vec.y > maxVec.y)
                maxVec.setY(vec.y);
        }

        return maxVec;
    }

    // FIXME: not working
    public static boolean circleIntersectRect(Circle circle, Rect rect) {

        //p'x = cos(theta) * (px-ox) - sin(theta) * (py-oy) + ox
        //p'y = sin(theta) * (px-ox) + cos(theta) * (py-oy) + oy

        double cos = Math.cos(rect.getRot());
        double sin = Math.sin(-rect.getRot());

        Vec2d rPos = rect.getPos().toDouble();
        Vec2d unrotatedCirclePos = circle.getPos().toDouble();
            unrotatedCirclePos.sub(rPos.x, rPos.y)
                              .mult(cos, sin)
                              .add(-sin * (circle.getPos().y - rPos.x), cos * (circle.getPos().y - rPos.x))
                              .add(rPos.x, rPos.y);

        // temporary variables to set edges for testing

        double rx = rPos.x - rect.getSize().x * 0.5;
        double ry = rPos.y - rect.getSize().y * 0.5;
        double testX = unrotatedCirclePos.x;
        double testY = unrotatedCirclePos.y;

        // which edge is closest?
        if (unrotatedCirclePos.x < rx)         testX = rx;      // test left edge
        else if (unrotatedCirclePos.x > rx+rect.getSize().x) testX = rx+rect.getSize().x;   // right edge
        if (unrotatedCirclePos.y < ry)         testY = ry;      // top edge
        else if (unrotatedCirclePos.y > ry+rect.getSize().y) testY = ry+rect.getSize().y;   // bottom edge


        double dist = dist(unrotatedCirclePos.x, unrotatedCirclePos.y, testX, testY);

        // if the distance is less than the radius, collision!
        return (sqrdDist(unrotatedCirclePos.x, unrotatedCirclePos.y, testX, testY) <= circle.r*circle.r);
    }
    // FIXME: not working
    public static boolean circleIntersectRect_2(Circle circle, Rect rect) {

        //p'x = cos(theta) * (px-ox) - sin(theta) * (py-oy) + ox
        //p'y = sin(theta) * (px-ox) + cos(theta) * (py-oy) + oy

        Vec2d unrotatedCirclePos = rotatePointAboutCenter(circle.getPos(), rect.getPos(), -rect.getRot());
        double unrotatedCircleX = unrotatedCirclePos.x;
        double unrotatedCircleY  = unrotatedCirclePos.y;

        // Closest point in the rectangle to the center of circle rotated backwards(unrotated)
        double closestX, closestY;

        // Find the unrotated closest x point from center of unrotated circle
        if (unrotatedCircleX  < rect.getPos().x)
            closestX = rect.getPos().x;
        else if (unrotatedCircleX  > rect.getPos().x + rect.getSize().x)
            closestX = rect.getPos().x + rect.getSize().x;
        else
            closestX = unrotatedCircleX ;

        // Find the unrotated closest y point from center of unrotated circle
        if (unrotatedCircleY < rect.getPos().y)
            closestY = rect.getPos().y;
        else if (unrotatedCircleY > rect.getPos().y + rect.getSize().y)
            closestY = rect.getPos().y + rect.getSize().y;
        else
            closestY = unrotatedCircleY;

        double dist = dist(unrotatedCircleX, unrotatedCircleY, closestX, closestY);


        return dist < circle.r;
    }

    public static boolean circleIntersectRectByClamping(Circle circle, Rect rect) {

        //p'x = cos(theta) * (px-ox) - sin(theta) * (py-oy) + ox
        //p'y = sin(theta) * (px-ox) + cos(theta) * (py-oy) + oy

        Vec2d unrotatedCirclePos = rotatePointAboutCenter(circle.getPos(), rect.getPos(), -rect.getRot());
        Vec2d clampedPosition = clampPointToRect(unrotatedCirclePos, rect);
        double dist = unrotatedCirclePos.dist(clampedPosition);

        return dist < circle.r;
    }

    /**
     * Separating Axis Theorem spécifique à des rectangles
     * @param rectA 1er rectangle
     * @param rectB 2eme rectangle
     * @return true si les rectangles sont en collision
     */
    public static boolean rectOnRectSAT(Rect rectA, Rect rectB) {

        Vec2f[] cornersA = rectA.getCorners();
        Vec2f[] cornersB = rectB.getCorners();

        Vec2f axis1 = new Vec2f(cornersA[3].x - cornersA[0].x,cornersA[3].y - cornersA[0].y);
        Vec2f axis2 = new Vec2f(cornersA[3].x - cornersA[2].x,cornersA[3].y - cornersA[2].y);
        Vec2f axis3 = new Vec2f(cornersB[0].x - cornersB[1].x,cornersB[0].y - cornersB[1].y);
        Vec2f axis4 = new Vec2f(cornersB[0].x - cornersB[3].x,cornersB[0].y - cornersB[3].y);

        return projectionOverlap(axis1, cornersA, cornersB)
            && projectionOverlap(axis2, cornersA, cornersB)
            && projectionOverlap(axis3, cornersA, cornersB)
            && projectionOverlap(axis4, cornersA, cornersB);
    }

    private static boolean projectionOverlap(Vec2f axis, Vec2f[] cornersA, Vec2f[] cornersB) {

        // (min, max)
        Vec2f extremasA = projectPoints(axis, cornersA);
        Vec2f extremasB = projectPoints(axis, cornersB);

        return !(extremasA.y < extremasB.x || extremasB.y < extremasA.x);
    }

    private static Vec2f projectPoints(Vec2f axis, Vec2f[] cornersA) {

        float v;
        Float min = null, max = null;

        for (Vec2f corner : cornersA) {

            v = corner.dot(axis);
            if (min == null) {
                min = v;
                max = v;
            } else {
                min = Math.min(min, v);
                max = Math.max(max, v);
            }

        }

        return new Vec2f(min, max);
    }


    @SuppressWarnings("DuplicatedCode")
    public static Vec2d rotatePointAboutCenter(Vec2d point, Vec2d center, float angle) {

        return new Vec2d(Math.cos(angle) * (point.x - center.x) - Math.sin(angle) * (point.y - center.y) + center.x,
                         Math.sin(angle) * (point.x - center.x) + Math.cos(angle) * (point.y - center.y) + center.y);
    }
    @SuppressWarnings("DuplicatedCode")
    public static Vec2d rotatePointAboutCenter(Vec2f point, Vec2f center, float angle) {

        return new Vec2d(Math.cos(angle) * (point.x - center.x) - Math.sin(angle) * (point.y - center.y) + center.x,
                         Math.sin(angle) * (point.x - center.x) + Math.cos(angle) * (point.y - center.y) + center.y);
    }

    public static double clamp(double value, double min, double max) {

        return Math.max(min, Math.min(max, value));
    }
    public static float clamp(float value, float min, float max) {

        return Math.max(min, Math.min(max, value));
    }

    public static Vec2d clampPointToRect(Vec2d point, Rect rect) {

        return new Vec2d(clamp(point.x, rect.getPos().x - rect.getSize().x * 0.5, rect.getPos().x + rect.getSize().x * 0.5),
                         clamp(point.y, rect.getPos().y - rect.getSize().y * 0.5, rect.getPos().y + rect.getSize().y * 0.5));
    }
    public static Vec2d clampPointToRect(double x, double y, Rect rect) {

        return new Vec2d(clamp(x, rect.getPos().x - rect.getSize().x * 0.5, rect.getPos().x + rect.getSize().x * 0.5),
                         clamp(y, rect.getPos().y - rect.getSize().y * 0.5, rect.getPos().y + rect.getSize().y * 0.5));
    }

    public static float sqrdDist(float x1, float y1, float x2, float y2) {

        return (float) (Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    public static double sqrdDist(double x1, double y1, double x2, double y2) {

        return Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2);
    }

    public static float dist(float x1, float y1, float x2, float y2) {

        return (float) Math.sqrt(sqrdDist(x1, y1, x2, y2));
    }
    public static float dist(double x1, double y1, double x2, double y2) {

        return (float) Math.sqrt(sqrdDist(x1, y1, x2, y2));
    }
}
