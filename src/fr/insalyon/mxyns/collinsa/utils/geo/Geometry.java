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

    /**
     * Les trois prochaines méthodes calculent les points supports, les axes de pénétration minimum et la face incidente tels que décrits par Erin Catto dans "SAT and Support Points"
     */
    public static Vec2f getSupport(Vec2f[] vertices, Vec2f axis) {

        Vec2f result = vertices[0];
        float v = result.dot(axis), tmp;

        for (int i = 1; i < vertices.length; ++i) {

            if ((tmp = vertices[i].dot(axis)) > v) {

                v = tmp;
                result = vertices[i];
            }

        }

        return result;
    }

    public static float findAxisOfLeastPenetration(int[] faceIndex, Polygon polygonA, Polygon polygonB) {

        float bestDistance = Float.NEGATIVE_INFINITY;
        int bestIndex = -1;

        for (int i = 0; i < polygonA.getVertices().length; ++i) {

            Vec2f n = polygonA.getNormals()[i].copy();
            Vec2f s = getSupport(polygonB.getVertices(), n.neg());
            Vec2f v = polygonA.getVertices()[i];

            float d = Vec2f.dot(n, s.copy().sub(v));

            // Store greatest distance
            if (d > bestDistance) {
                bestDistance = d;
                bestIndex = i;
            }
        }

        faceIndex[0] = bestIndex;
        return bestDistance;
    }

    public static void findIncidentFace(Vec2f[] v, Polygon reference, Polygon incident, int referenceIndex) {

        Vec2f referenceNormal = reference.getNormals()[referenceIndex];

        int incidentFace = 0;
        float minDot = Float.POSITIVE_INFINITY;
        for (int i = 0; i < incident.getVertices().length; ++i) {

            float dot = Vec2f.dot(referenceNormal, incident.getNormals()[i]);

            if (dot < minDot) {
                minDot = dot;
                incidentFace = i;
            }
        }

        v[0] = incident.getVertices()[incidentFace];
        incidentFace = (incidentFace + 1) % incident.getVertices().length;
        v[1] = incident.getVertices()[incidentFace];
    }

    /**
     * Renvoie un vecteur directeur du i-ème côté d'un polygone défini par les points 'vertices'
     * @param vertices points délimitant le polygone
     * @param i indice du côté à calculer
     * @return vecteur directeur du i-ème côté
     */
    public static Vec2f getEdge(Vec2f[] vertices, int i) {

        return vertices[(i + 1) % vertices.length].copy().sub(vertices[i]);
    }

    /**
     * Renvoie les vecteurs directeurs des côtés d'un polygone défini par les points 'vertices'
     * @param vertices points délimitant le polygone
     * @return tableau des vecteurs directeurs des côtés à remplir
     */
    public static Vec2f[] getEdgesVectors(Vec2f... vertices) {

        Vec2f[] result = new Vec2f[vertices.length];

        for (int i = 0; i < result.length; ++i)
            result[i] = vertices[(i + 1) % result.length].copy().sub(vertices[i]);

        return result;
    }

    /**
     * Renvoie les positions des centres des côtés d'un polygone défini par les points 'vertices'
     * @param vertices points délimitant le polygone
     * @param edges tableau des vecteurs directeurs des côtés à remplir
     * @return tableau des vecteurs position des centres des côtés du polygones
     */
    public static Vec2f[] getEdgesCenters(Vec2f[] vertices, Vec2f[] edges) {

        Vec2f[] edgesCenters = new Vec2f[vertices.length];
        for (int i = 0; i < vertices.length; ++i) {
            Vec2f edge = edges[i];
            Vec2f corner = vertices[i];
            edgesCenters[i] = corner.copy().add(edge.copy().mult(0.5f));
        }

        return edgesCenters;
    }

    /**
     * Renvoie les normales des côtés d'un polygone défini par les points 'vertices'
     * @param vertices points délimitant le polygone
     * @return un tableau de vecteurs Vec2f normaux aux côtés du polygone
     */
    public static Vec2f[] getNormals(Vec2f... vertices) {

        Vec2f[] result = new Vec2f[vertices.length];

        for (int i = 0; i < result.length; ++i)
            result[i] = cross(vertices[(i + 1) % result.length].copy().sub(vertices[i]), 1).normalize();

        return result;
    }

    /**
     * Calcule les vecteurs directeurs des côtés d'un polygone défini par les points 'vertices' ainsi que les normales à ces côtés
     * @param vertices points délimitant le polygone
     * @param edges tableau des vecteurs directeurs des côtés à remplir
     * @param normals tableau des normales à remplir
     */
    public static void getNormalsAndEdges(Vec2f[] vertices, Vec2f[] edges, Vec2f[] normals) {

        for (int i = 0; i < vertices.length; ++i) {
            edges[i] = vertices[(i + 1) % vertices.length].copy().sub(vertices[i]);
            normals[i] = new Vec2f(edges[i].y, -edges[i].x).normalize();
        }
    }

    /**
     * Renvoie le barycentre des points donnés s'ils ont tous la même pondération
     * @param vertices points dont il faut trouver le barycentre
     * @return barycentre des points
     */
    public static Vec2f getBarycenter(Vec2f[] vertices) {

        float x = 0, y = 0;

        for (Vec2f vertex : vertices) {
            x += vertex.x;
            y += vertex.y;
        }

        return new Vec2f(x, y).div(vertices.length);
    }

    /**
     * Produit vectoriel restreint à R². 
     * @see Vec2f#cross(Vec2f, float) 
     */
    public static Vec2f cross(Vec2f vec, float s) {

        return new Vec2f(s*vec.y, -s*vec.x);
    }
    /**
     * Produit vectoriel restreint à R². 
     * @see Vec2f#cross(float, Vec2f) 
     */
    public static Vec2f cross(float s, Vec2f vec) {

        return new Vec2f(-s*vec.y, s*vec.x);
    }
    /**
     * Produit vectoriel restreint à R². 
     * @see Vec2f#cross(Vec2f, Vec2f) 
     */
    public static float cross(Vec2f a, Vec2f b) {
        return a.x * b.y - a.y * b.x;
    }

    /**
     * Sutherland-Hodgman Clipping Algorithm
     * @param normal axe de clipping délimitant le semi-espace
     * @param c valeur donnée par le produit scalaire (ordonnée à l'origine de l'axe)
     * @param face les deux points à clipper 
     * @return nombre de points clippés
     */
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

    /**
     * Comme clampPointToRect mais quand l'objet est à l'intérieur du Rectangle
     * @param point point à clamp
     * @param rect rectangle sur lequel clamp le point
     * @return point clampé
     */
    public static Vec2d clampPointInsideRect(Vec2d point, Rect rect) {

        Vec2d[] possibilities = {
            new Vec2d(point.x, rect.getPos().y - rect.getSize().y * 0.5),
            new Vec2d(point.x, rect.getPos().y + rect.getSize().y * 0.5),
            new Vec2d(rect.getPos().x + rect.getSize().x * 0.5, point.y),
            new Vec2d(rect.getPos().x - rect.getSize().x * 0.5, point.y)
        };

        return nearestPoint(point, possibilities);
    }

    /**
     * Renvoie quel point est le plus proche d'un point cible parmis un ensemble de points
     * @param from point cible T
     * @param points ensemble E de de points
     * @return le point de E le plus proche de T
     */
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

    /**
     * Separating Axis Theorem : détermine si deux polygones convexes sont en intersection
     * @param entity premier polygone
     * @param target deuxieme polygone
     * @return true si il y a intersection
     */
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
