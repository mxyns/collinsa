package fr.insalyon.mxyns.collinsa.utils.monitoring;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class EntityMonitoring {

    public HashSet<Entity> toMonitor = new HashSet<>();
    public HashMap<Entity, LinkedHashMap<Double, Vec2f[]>> vectorialData = new HashMap<>();
    public HashMap<Entity, LinkedHashMap<Double, HashMap<String, Double>>> scalarData = new HashMap<>();

    public void logVectorialInfo(Entity entity, double time) {

        addVectorialInfo(entity, time, 0, entity.getPos().copy());
        addVectorialInfo(entity, time, 1, entity.getVel().copy());
        addVectorialInfo(entity, time, 2, entity.getAcc().copy());
    }
    public void logScalarInfo(Entity entity, double time) {

         addScalarInfo(entity, "lifespan", time, entity.lifespan);
         addScalarInfo(entity, "lived", time, entity.lived);
         addScalarInfo(entity, "rot", time, entity.getRot());
         addScalarInfo(entity, "angVel", time, entity.getAngVel());
         addScalarInfo(entity, "angAcc", time, entity.getAngAcc());
         addScalarInfo(entity, "fillColorRGB", time, entity.getFillColor().getRGB());
         addScalarInfo(entity, "outlineColorRGB", time, entity.getOutlineColor().getRGB());
         addScalarInfo(entity, "maximumSize", time, entity.getMaximumSize());
         addScalarInfo(entity, "volume", time, entity.getVolume());
         addScalarInfo(entity, "J", time, entity.getInertia().getJ());
         addScalarInfo(entity, "mass", time, entity.getInertia().getMass());
    }

    private void addScalarInfo(Entity entity, String dataType, double time, double value) {

        LinkedHashMap<Double, HashMap<String, Double>> dataOverTime;
        if ((dataOverTime = scalarData.get(entity)) == null)
            scalarData.put(entity, dataOverTime = new LinkedHashMap<>());

        HashMap<String, Double> data;
        if ((data = dataOverTime.get(time)) == null)
            scalarData.get(entity).put(time, data = new HashMap<>());

        data.put(dataType, value);
    }
    private void addVectorialInfo(Entity entity, double time, int index, Vec2f vector) {

        LinkedHashMap<Double, Vec2f[]> dataOverTime;
        if ((dataOverTime = vectorialData.get(entity)) == null)
            vectorialData.put(entity, dataOverTime = new LinkedHashMap<>());

        Vec2f[] data;
        if ((data = dataOverTime.get(time)) == null)
            dataOverTime.put(time, data = new Vec2f[3]);

        data[index] = vector;
    }

    public boolean isMonitored(Entity entity) {

        return toMonitor.contains(entity);
    }
    public boolean stopMonitoring(Entity entity) {

        return toMonitor.remove(entity);
    }
}