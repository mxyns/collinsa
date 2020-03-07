package fr.insalyon.mxyns.collinsa.utils.geo;


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

    public static float sqrdDist(float x1, float y1, float x2, float y2) {

        return (float) (Math.pow(x2 - x1, 2) + Math.pow(x2 - x1, 2));
    }
    public static float dist(float x1, float y1, float x2, float y2) {

        return (float) Math.sqrt(sqrdDist(x1, y1, x2, y2));
    }

}
