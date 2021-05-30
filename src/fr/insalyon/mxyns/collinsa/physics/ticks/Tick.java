package fr.insalyon.mxyns.collinsa.physics.ticks;

import fr.insalyon.mxyns.collinsa.physics.Chunk;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.forces.Force;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

public class Tick {

    public final HashMap<UUID, Entity> entities = new HashMap<>();
    public final ArrayList<Chunk> chunks = new ArrayList<>();
    public final ArrayList<Force> forces = new ArrayList<>(), globalForces = new ArrayList<>();

    public CopyOnWriteArraySet<UUID> entitiesToRemove = new CopyOnWriteArraySet<>();
    public CopyOnWriteArraySet<Entity> entitiesToInsert = new CopyOnWriteArraySet<>();

    public final long creationTimeReal;
    public final double creationTimeSim;
    public long duration = -1;
    public final long id;

    public Tick(Physics physics, TickMachine tickMachine) {

        creationTimeReal = System.currentTimeMillis();
        creationTimeSim = physics.totalElapsedTime;

        if (tickMachine != null)
            this.id = tickMachine.tickCount++;
        else
            this.id = 0;

        int horizontalChunkCount = (int) physics.getChunkCount().x;
        int verticalChunkCount = (int) physics.getChunkCount().y;
        physics.buildChunks(this, horizontalChunkCount, verticalChunkCount, physics.getWidth() / horizontalChunkCount, physics.getHeight() / verticalChunkCount);
    }
    public Tick(Physics physics) {

        this(physics, physics.getTickMachine());
     }

    public String toString() {

        return String.valueOf(id);
    }
}