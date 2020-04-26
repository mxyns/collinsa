package fr.insalyon.mxyns.collinsa.physics.entities;

import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.utils.geo.Geometry;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Graphics2D;

public abstract class Polygon extends Entity {


    public Vec2f[] local_vertices;

    protected Vec2f[] vertices, edges, normals;

    protected Polygon(Vec2f pos, int n) {

        super(pos);
        this.vertices = new Vec2f[n];
        this.edges = new Vec2f[n];
        this.normals = new Vec2f[n];
        this.local_vertices = new Vec2f[n];
    }
    public Polygon(Vec2f pos, Vec2f[] local_vertices) {

        this(pos, local_vertices.length);

        this.local_vertices = new Vec2f[local_vertices.length];

        Vec2f barycenter = Geometry.getBarycenter(local_vertices);
        for (int i = 0; i < local_vertices.length; ++i)
            this.local_vertices[i] = local_vertices[i].copy().sub(barycenter);

        updateVertices();
        getInertia().update(this);
    }

    public Polygon(Vec2f pos, int n, float r) {

        this(pos, n);

        local_vertices = new Vec2f[n];

        float a = 360f / n;
        for (int i = 0; i < n; ++i)
            local_vertices[i] = new Vec2f(r, 0).rotate((float) Math.toRadians(-a * i));

        updateVertices();
        getInertia().update(this);
    }

    @Override
    public void render(Renderer renderer, Graphics2D g) {

        renderer.renderPolygon(this, g);
    }

    @Override
    public float computeJ() {

        if (local_vertices == null)
            return 0;

        return getInertia().getMass() * local_vertices[0].squaredMag();
    }
    //TODO : use https://mathoverflow.net/questions/73556/calculating-moment-of-inertia-in-2d-planar-polygon

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

    public void updateVertices() {

        for (int i = 0; i < vertices.length; ++i)
            vertices[i] = local_vertices[i].rotateOut(getRot()).add(pos);

        Geometry.getNormalsAndEdges(vertices, edges, normals);
    }

    public Vec2f[] getVertices() {

        return vertices;
    }

    public Vec2f[] getEdges() {

        return edges;
    }

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
