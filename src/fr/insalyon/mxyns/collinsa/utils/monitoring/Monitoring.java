package fr.insalyon.mxyns.collinsa.utils.monitoring;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

/**
 * Class that contains gives access to all of the monitoring tools
 */
public class Monitoring {

    public EntityMonitoring entityMonitoring;

    public Monitoring() {

        entityMonitoring = new EntityMonitoring();
    }

    public void monitor(Entity entity) {

        entityMonitoring.toMonitor.add(entity.uuid);
    }
}