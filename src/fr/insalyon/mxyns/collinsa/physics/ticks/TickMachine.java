package fr.insalyon.mxyns.collinsa.physics.ticks;

import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

public class TickMachine {

    public long tickCount = 0;
    private final Tick current;
    private Tick prev, queue, last;

    // public ArrayList<Force> globalForcesToRemove = new ArrayList<>();

    public TickMachine(Physics physics) {

        this.current = new Tick(physics, this);
        this.prev = finished(physics, -1);
        this.last = this.current;
        this.queue = this.current;
    }

    public Tick finished(Physics physics, long duration) {

        //System.out.println("finished : " + this.current + " -> " + this.next );
        this.current.duration = duration;

        final Tick previous = new Tick(physics);

        // add copies of the current tick's entities to the previous tick they will be used to in read-only for the changes
        copyTickTo(physics, this.current, previous);

        this.prev = previous;
        this.queue = this.prev;

        return this.prev;
    }

    public void rendered() {

        if (this.queue != null) {
            this.last = this.queue;
            this.queue = null;
        }
    }

    public Tick last() {

        if (this.queue != null) {
            this.last = this.queue;
            this.queue = null;
        }

        return last;
    }

    public Tick current() {

        return current;
    }

    public Tick getPrev() {

        return this.prev;
    }

    public Tick queue() {

        return this.queue;
    }

    private void copyTickTo(Physics physics, Tick origin, Tick target) {

        origin.entities.forEach((uuid, entity) -> target.entities.put(uuid, entity.copy()));
        target.entitiesToInsert.addAll(origin.entitiesToInsert);
        origin.entitiesToInsert.clear();
        target.entitiesToRemove.addAll(origin.entitiesToRemove);
        origin.entitiesToRemove.clear();

        physics.spatialHashing(target);

        origin.forces.forEach(force -> {

            Entity newSource = force.getSource() == null ? null : target.entities.get(force.getSource().uuid);
            Entity newTarget = force.getTarget() == null ? null : target.entities.get(force.getTarget().uuid);
            if ((force.getSource() != null && newSource == null) || (force.getTarget() != null && newTarget == null))
                return;
            target.forces.add(force.copy(newSource, newTarget));
        });
        origin.globalForces.forEach(force -> {
            Entity sourceEntity = force.getSource() != null ? target.entities.get(force.getSource().uuid) : null;
            Entity targetEntity = force.getTarget() != null ? target.entities.get(force.getTarget().uuid) : null;
            target.globalForces.add(force.copy(sourceEntity, targetEntity));
        });

        // target.forces.forEach(force -> System.out.println(force.lastValue));
    }
}
