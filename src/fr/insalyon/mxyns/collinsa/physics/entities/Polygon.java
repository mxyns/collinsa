package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Geometry;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Graphics2D;

public abstract class Polygon extends Entity {

    /**
     * Position des points du polygone dans le repère local
     */
    public Vec2f[] local_vertices;

    /**
     * Liste des points, des vecteurs directeurs de chaque arête du polygone, et des normales à ces arêtes
     */
    protected Vec2f[] vertices, edges, normals;

    /**
     * Constructeur générique pour un polygone quelconque à n côtés
     * @param pos position du polygone dans le monde
     * @param n nombre de côtés / de points
     */
    protected Polygon(Vec2f pos, int n) {

        super(pos);
        this.vertices = new Vec2f[n];
        this.edges = new Vec2f[n];
        this.normals = new Vec2f[n];
        this.local_vertices = new Vec2f[n];
    }

    /**
     * Constructeur d'un polygone quelconque à partir d'un ensemble de points dans le repère local (relatif par rapport au centre)
     *
     * @param pos position du polygone dans le monde
     * @param local_vertices tableau contenant les points du polygone dans son repère local
     */
    public Polygon(Vec2f pos, Vec2f[] local_vertices) {

        this(pos, local_vertices.length);

        this.local_vertices = new Vec2f[local_vertices.length];

        Vec2f barycenter = Geometry.getBarycenter(local_vertices);
        for (int i = 0; i < local_vertices.length; ++i)
            this.local_vertices[i] = local_vertices[i].copy().sub(barycenter);

        updateVertices();
        getInertia().update();
    }

    @Override
    public void render(Renderer renderer, Graphics2D g) {

        renderer.renderPolygon(this, g);
    }

    @Override
    public float computeJ() {

        //TODO : use https://mathoverflow.net/questions/73556/calculating-moment-of-inertia-in-2d-planar-polygon
        if (local_vertices == null)
            return 0;

        return getInertia().getMass() * local_vertices[0].squaredMag();
    }

    @Override
    public float getVolume() {

        // Happens at first initialization (Entity constructor => setMaterial(DUMMY) => setMass => getVolume) but is changed right in Polygon constructor (inertia.update(this))
        if (local_vertices == null)
            return 0;

        double area = 0;
        int j;

        for (int i = 0 ; i < local_vertices.length; ++i) {
            j = (i + 1) % local_vertices.length;
            area += vertices[i].x * vertices[j].y;
            area -= vertices[i].y * vertices[j].x;
        }
        area = Math.abs(area) / 2;

        return (float) area;
    }

    /**
     * Met à jour les valeurs des points 'vertices' (dans le repère global) à partir des local_vertices.
     */
    public void updateVertices() {

        for (int i = 0; i < vertices.length; ++i)
            vertices[i] = local_vertices[i].rotateOut(getRot()).add(pos);

        Geometry.getNormalsAndEdges(vertices, edges, normals);
    }

    /**
     * Renvoie la liste des points du polygone dans le repère global
     * @return vertices
     */
    public Vec2f[] getVertices() {

        return vertices;
    }

    /**
     * Renvoie la liste des vecteurs directeurs de chaque côté du polygone dans le repère global
     * @return vertices
     */
    public Vec2f[] getEdges() {

        return edges;
    }

    /**
     * Renvoie la liste des normales aux côtés du polygone dans le repère global
     * @return vertices
     */
    public Vec2f[] getNormals() {

        return normals;
    }

    @Override
    public void updateAABB() {

        updateVertices();

        Vec2f minCorner = Geometry.getMinPos(vertices);
        Vec2f maxCorner = Geometry.getMaxPos(vertices);

        this.aabb.x = minCorner.x;
        this.aabb.y = minCorner.y;
        this.aabb.w = maxCorner.x - minCorner.x;
        this.aabb.h = maxCorner.y - minCorner.y;
    }

    @Override
    public double getMaximumSize() {

        double max = Double.NEGATIVE_INFINITY, tmp;
        for (Vec2f vertex : local_vertices)
            if ((tmp = vertex.squaredMag()) > max)
                max = tmp;

        return 2 * Math.sqrt(max);
    }

    @Override
    public short cardinal() {

        return 2;
    }
}