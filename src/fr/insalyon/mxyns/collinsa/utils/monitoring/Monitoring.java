package fr.insalyon.mxyns.collinsa.utils.monitoring;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

public class Monitoring {

    public EntityMonitoring entityMonitoring;

    public Monitoring() {

        entityMonitoring = new EntityMonitoring();
    }

    public void monitor(Entity entity) {

        entityMonitoring.toMonitor.add(entity);
    }
}