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
        this.local_vertices = local_vertices;

        getInertia().update(this);
        updateVertices();
    }
    /*public Polygon(Vec2f[] vertices) {

        this(Inertia.getBarycenter(vertices), vertices);
    }*/ // TODO : barycenter

    public Polygon(Vec2f pos, int n, float r) {

        this(pos, n);

        local_vertices = new Vec2f[n];

        float a = 360f / n;
        for (int i = 0; i < n; ++i)
            local_vertices[i] = new Vec2f(r, 0).rotate((float) Math.toRadians(-a * i));

        getInertia().update(this);
        updateVertices();
    }

    @Override
    public void render(Renderer renderer, Graphics2D g) {

        renderer.renderPolygon(this, g);
    }

    @Override
    public float computeJ() {

        return 1;
    }
    //TODO :

    @Override
    public float getVolume() { return 1; }
    // TODO :

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
    public short cardinal() {

        return 2;
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
}
