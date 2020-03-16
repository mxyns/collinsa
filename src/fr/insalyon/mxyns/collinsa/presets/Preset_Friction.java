package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.collisions.Collision;
import fr.insalyon.mxyns.collinsa.physics.entities.Rect;

public class Preset_Friction extends Preset {

    @Override
    public void run(String[] args, Collinsa collinsa) {

        //Création d'élements / entitées à ajouter à la simulation
        Physics physics = collinsa.getPhysics();
        Rect moulin = new Rect(physics.getWidth() / 2, physics.getHeight() / 2, physics.getChunkSize().x / 4, physics.getChunkSize().y / 16);
        moulin.setAngVel(-1f);
        moulin.setAngAcc(-0.3f);
        moulin.setCollisionType(Collision.CollisionType.KINEMATIC);

        // On ajoute les entités au moteur physique
        physics.addEntity(moulin);
    }
}
