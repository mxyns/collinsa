package fr.insalyon.mxyns.collinsa.utils.geo;


import fr.insalyon.mxyns.collinsa.physics.entities.Circle;
import fr.insalyon.mxyns.collinsa.physics.entities.Polygon;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;

/**
 * Classe de calculs et méthodes géométriques
 */
public class Geometry {

    /**
     * Détermine la position des coins d'un rectangle orienté
     * @param rect rectangle dont il faut calculer la position des coins
     * @return Vec2f[] tableau de vecteurs position des coins
     */
    public static Vec2f[] getRectangleCorners(Rect rect) {

        Vec2f[] result;
        for (Vec2f corner : (result=getRectangeLocalCorners(rect)))
            corner.add(rect.getPos());

        return result;
    }
    public static Vec2f[] getRectangeLocalCorners(Rect rect) {

        Vec2f v1 = new Vec2f((float)Math.cos(rect.getRot()), (float)Math.sin(rect.getRot()));
        Vec2f v2 = new Vec2f(-v1.y, v1.x);

        v1.mult(rect.getSize().x * 0.5f);
        v2.mult(rect.getSize().y * 0.5f);

        return new Vec2f[] {
            Vec2f.zero().sub(v1).sub(v2),
            Vec2f.zero().sub(v1).add(v2),
            Vec2f.zero().add(v1).add(v2),
            Vec2f.zero().add(v1).sub(v2),
            };
    }

    /**
     * Renvoie un vecteur contenant la valeur minimale de x et la valeur minimale de y des vecteurs 'vecs'
     * @param vecs vecteurs à analyser
     * @return Vec2f( min(x_i), min(y_i) )
     */
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

    /**
     * Renvoie un vecteur contenant la valeur maximale de x et la valeur maximale de y des vecteurs 'vecs'
     * @param vecs vecteurs à analyser
     * @return Vec2f( max(x_i), max(y_i) )
     */
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

    /**
     * Détermine si un cercle et un rectangle orienté sont en collision par 'clamping'
     *
     * @param circle cercle avec lequel il faut vérifier l'intersection
     * @param rect rectangle avec lequel il faut vérifier l'intersection
     * @return true si le cercle et le rectangle sont en intersection
     */
    public static boolean circleIntersectRectByClamping(Circle circle, Rect rect) {

        // On tourne le centre du cercle de -angleDuRectangle.
        // Après ça c'est comme si essayait de déterminer une intersection en un cercle et un rectangle avec ses côtés alignés aux axes du repère
        Vec2d unrotatedCirclePos = rotatePointAboutCenter(circle.getPos(), rect.getPos(), -rect.getRot());

        // Position du centre du cercle après 'clamping' (on trouve la position sur les arêtes du rectangle la plus proche du centre du cercle, un peu comme une projection sur les arêtes)
        Vec2d clampedPosition = clampPointToRect(unrotatedCirclePos, rect);

        // Si ce vecteur est plus petit que le rayon du cercle, alors le cercle et le rectangle sont en intersection
        return unrotatedCirclePos.sqrdDist(clampedPosition) < circle.getR() * circle.getR();
    }

    /**
     * Separating Axis Theorem spécifique à des rectangles
     * @param rectA 1er rectangle
     * @param rectB 2eme rectangle
     * @return true si les rectangles sont en collision
     */
    public static boolean rectOnRectSAT(Rect rectA, Rect rectB) {

        Vec2f[] cornersA = rectA.getVertices();
        Vec2f[] cornersB = rectB.getVertices();

        Vec2f axis1 = new Vec2f(cornersA[3].x - cornersA[0].x,cornersA[3].y - cornersA[0].y);
        Vec2f axis2 = new Vec2f(cornersA[3].x - cornersA[2].x,cornersA[3].y - cornersA[2].y);
        Vec2f axis3 = new Vec2f(cornersB[0].x - cornersB[1].x,cornersB[0].y - cornersB[1].y);
        Vec2f axis4 = new Vec2f(cornersB[0].x - cornersB[3].x,cornersB[0].y - cornersB[3].y);

        return projectionOverlap(axis1, cornersA, cornersB)
            && projectionOverlap(axis2, cornersA, cornersB)
            && projectionOverlap(axis3, cornersA, cornersB)
            && projectionOverlap(axis4, cornersA, cornersB);
    }

    /**
     * Détermine si deux ensembles de points projetés sur un axe sont en intersection
     * Utilisé pour le rectOnRectSAT
     *
     * @param axis axe de projection
     * @param cornersA ensemble de points A
     * @param cornersB ensemble de points B
     * @return true si les intervalles des projections des ensembles de points sur l'axe sont en intersection
     */
    private static boolean projectionOverlap(Vec2f axis, Vec2f[] cornersA, Vec2f[] cornersB) {

        // Intervalle de l'ensemble A après projection (min, max)
        Vec2f extremasA = projectPoints(axis, cornersA);

        // Intervalle de l'ensemble B après projection (min, max)
        Vec2f extremasB = projectPoints(axis, cornersB);

        // Condition d'intersection de deux intervalles
        return !(extremasA.y < extremasB.x || extremasB.y < extremasA.x);
    }

    /**
     * Projette des points sur un axe et conserve les valeurs min et max du résultat de la projection
     * @param axis axe de projection
     * @param points points à projeter
     * @return Vec2f(min, max)
     */
    private static Vec2f projectPoints(Vec2f axis, Vec2f[] points) {

        float v;
        Float min = null, max = null;

        for (Vec2f corner : points) {

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

    private static Vec2f getSupport(Vec2f axis, Vec2f... points) {

        Vec2f result = points[0];
        float v = result.dot(axis), tmp;

        for (int i = 1; i < points.length; ++i) {

            if ((tmp = points[i].dot(axis)) > v) {

                v = tmp;
                result = points[i];
            }

        }

        return result;
    }

    public static float findAxisOfLeastPenetration(int[] faceIndex, Polygon polygonA, Polygon polygonB) {

        float bestDistance = Float.NEGATIVE_INFINITY;
        int bestIndex = -1;

        for (int i = 0; i < polygonA.getVertices().length; ++i) {

            Vec2f normal = polygonA.getNormals()[i];
            Vec2f support = getSupport(normal, polygonB.getVertices());
            Vec2f v = polygonA.getVertices()[i];
            float d = Vec2f.dot(normal.multOut(-1), support.copy().sub(v));

            if (d > bestDistance) {
                bestDistance = d;
                bestIndex = i;
            }
        }

        faceIndex[0] = bestIndex;

        return bestDistance;
    }

    public static void getNormalsAndEdges(Vec2f[] vertices, Vec2f[] edges, Vec2f[] normals) {

        for (int i = 0; i < vertices.length; ++i) {
            edges[i] = vertices[i].copy().sub(vertices[(i + (vertices.length - 1)) % vertices.length]).neg();
            normals[i] = cross(1, edges[i].multOut(-1));
        }
    }

    public static Vec2f[] getEdgesVectors(Vec2f... vecs) {

        Vec2f[] edges = new Vec2f[vecs.length];

        for (int i = 0; i < vecs.length; ++i)
            edges[i] = vecs[i].copy().sub(vecs[(i + (vecs.length - 1)) % vecs.length]).neg();

        return edges;
    }

    public static void findIncidentFace(Vec2f[] v, Polygon incident, Vec2f referenceNormal) {

        Vec2f normal = referenceNormal;

        // Find most anti-normal face on incident polygon

        float dot, bestDot = Float.MAX_VALUE;
        int incidentFace = -1;
        for (int i = 0; i < incident.getVertices().length; ++i) {

            dot = Vec2f.dot(normal, incident.getEdges()[i]);
            if (dot < bestDot) {

                bestDot = dot;
                incidentFace = i;
            }
        }

        v[0] = incident.getVertices()[incidentFace];
        incidentFace = (incidentFace + (incident.getVertices().length - 1)) % incident.getVertices().length;
        v[1] = incident.getVertices()[incidentFace];
    }

    public static Vec2f[] getNormals(Vec2f... vecs) {

        Vec2f[] normals = new Vec2f[vecs.length];

        for (int i = 0; i < vecs.length; ++i)
            normals[i] = cross(1, vecs[i].copy().sub(vecs[(i + (vecs.length - 1)) % vecs.length]));

        return normals;
    }

    public static Vec2f getNormal(Vec2f vec) {

        return cross(1, vec).normalize();
    }

    public static Vec2f cross(Vec2f vec, float s) {

        return new Vec2f(s*vec.y, -s*vec.x);
    }
    public static Vec2f cross(float s, Vec2f vec) {

        return new Vec2f(-s*vec.y, s*vec.x);
    }
    public static float cross(Vec2f a, Vec2f b) {
        return a.x * b.y - a.y * b.x;
    }

    public static int clip(Vec2f normal, float c, Vec2f[] face) {

        int sp = 0;
        Vec2f[] out = {
            face[0].copy(),
            face[1].copy()
        };

        float d1 = Vec2f.dot(normal, face[0]) - c;
        float d2 = Vec2f.dot(normal, face[1]) - c;

        if (d1 <= 0.0f) out[sp++].set(face[0]);
        if (d2 <= 0.0f) out[sp++].set(face[1]);

        if (d1 * d2 < 0.0f) {
            float alpha = d1 / (d1 - d2);

            out[sp++].set(face[1]).sub(face[0]).mult(alpha).add(face[0]);
        }

        face[0] = out[0];
        face[1] = out[1];

        return sp;
    }

    /**
     * Ces méthodes font tourner un point (P) autour d'un autre (O) selon la formule :
     *
     * p'x = cos(theta) * (px-ox) - sin(theta) * (py-oy) + ox
     * p'y = sin(theta) * (px-ox) + cos(theta) * (py-oy) + oy
     *
     * @param point point à tourner
     * @param center centre de rotation
     * @param angle angle de rotation
     * @return point tourné d'un angle 'angle' autour du point 'center'
     */
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
    @SuppressWarnings("DuplicatedCode")
    public static Vec2f rotatePointAboutCenterFloat(Vec2f point, Vec2f center, float angle) {

        return new Vec2f((float) (Math.cos(angle) * (point.x - center.x) - Math.sin(angle) * (point.y - center.y) + center.x),
                         (float) (Math.sin(angle) * (point.x - center.x) + Math.cos(angle) * (point.y - center.y) + center.y));
    }
    @SuppressWarnings("DuplicatedCode")
    public static Vec2f rotatePointAboutCenter(float x, float y, Vec2f center, float angle) {

        return new Vec2f((float)(Math.cos(angle) * (x - center.x) - Math.sin(angle) * (y - center.y) + center.x),
                         (float)(Math.sin(angle) * (x - center.x) + Math.cos(angle) * (y - center.y) + center.y));
    }

    /**
     * Limite une valeur dans un intervalle (équivalent de constrain en Arduino / Processing)
     * @param value valeur à contraindre
     * @param min minimum de l'intervalle
     * @param max maximum de l'intervalle
     * @return valeur contrainte à l'intervalle
     */
    public static double clamp(double value, double min, double max) {

        return Math.max(min, Math.min(max, value));
    }
    public static float clamp(float value, float min, float max) {

        return Math.max(min, Math.min(max, value));
    }

    /**
     * Clamp un point sur un rectangle
     * @param point point à 'clamper'
     * @param rect rectangle sur lequel 'clamper' le point
     * @return position du point 'clampé'
     */
    public static Vec2d clampPointToRect(Vec2d point, Rect rect) {

        return new Vec2d(clamp(point.x, rect.getPos().x - rect.getSize().x * 0.5, rect.getPos().x + rect.getSize().x * 0.5),
                         clamp(point.y, rect.getPos().y - rect.getSize().y * 0.5, rect.getPos().y + rect.getSize().y * 0.5));
    }
    public static Vec2d clampPointToRect(double x, double y, Rect rect) {

        return new Vec2d(clamp(x, rect.getPos().x - rect.getSize().x * 0.5, rect.getPos().x + rect.getSize().x * 0.5),
                         clamp(y, rect.getPos().y - rect.getSize().y * 0.5, rect.getPos().y + rect.getSize().y * 0.5));
    }

    public static Vec2d clampPointInsideRect(Vec2d point, Rect rect) {

        Vec2d[] possibilities = {
            new Vec2d(point.x, rect.getPos().y - rect.getSize().y * 0.5),
            new Vec2d(point.x, rect.getPos().y + rect.getSize().y * 0.5),
            new Vec2d(rect.getPos().x + rect.getSize().x * 0.5, point.y),
            new Vec2d(rect.getPos().x - rect.getSize().x * 0.5, point.y)
        };

        return nearestPoint(point, possibilities);
    }
    public static Vec2d nearestPoint(Vec2d from, Vec2d... points) {

        double dist = points[0].sqrdDist(from);
        int index = 0;
        double temp = 0;
        for (int i = 0; i < points.length; ++i)
            if ((temp = points[i].sqrdDist(from)) < dist) {
                index = i;
                dist = temp;
            }

        return points[index];
    }

    /**
     * Calcul d'une distance au carré entre les points (x1, y1) et (x2, y2)
     * @param x1 position x du premier point
     * @param y1 position y du premier point
     * @param x2 position x du deuxième point
     * @param y2 position y du deuxième point
     *
     * @return distance au carré entre les deux points
     */
    public static float sqrdDist(float x1, float y1, float x2, float y2) {

        return (x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1);
    }
    public static double sqrdDist(double x1, double y1, double x2, double y2) {

        return (x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1);
    }

    /**
     * Calcul d'une distance entre les points (x1, y1) et (x2, y2)
     * @param x1 position x du premier point
     * @param y1 position y du premier point
     * @param x2 position x du deuxième point
     * @param y2 position y du deuxième point
     *
     * @return distance entre les deux points
     */
    public static float dist(float x1, float y1, float x2, float y2) {

        return (float) Math.sqrt(sqrdDist(x1, y1, x2, y2));
    }
    public static float dist(double x1, double y1, double x2, double y2) {

        return (float) Math.sqrt(sqrdDist(x1, y1, x2, y2));
    }

    public static boolean SAT(Polygon entity, Polygon target) {

        Vec2f[] axes = new Vec2f[entity.getVertices().length + target.getVertices().length];
        Vec2f[] normals = Geometry.getNormals(entity.getVertices());

        System.arraycopy(normals, 0, axes, 0, normals.length);

        normals = Geometry.getNormals(target.getVertices());
        System.arraycopy(normals, 0, axes, entity.getVertices().length, normals.length);

        for (Vec2f axe : axes)
            if (!Geometry.projectionOverlap(axe, entity.getVertices(), target.getVertices()))
                return false;

        return true;
    }
}
