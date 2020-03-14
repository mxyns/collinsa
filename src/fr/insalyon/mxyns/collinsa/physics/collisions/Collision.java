package fr.insalyon.mxyns.collinsa.physics.collisions;

import fr.insalyon.mxyns.collinsa.physics.entities.Entity;

import java.util.function.Consumer;

/**
 * Représente une collision entre deux entités (Entity)
 */
public class Collision {

    /**
     * Les deux entités impliquées dans la collision.
     * Les noms 'source' et 'target' ne représentent pas grand chose, ils permettent seulement de mieux voir la différence entre les deux entités que si on utilisait entity1/entityA et entity2/entityB
     * donc Collision(A, B) ≡ Collision(B, A)
     */
    Entity source, target;

    /**
     * La fonction à executer pour résoudre la collision.
     * Permet de ne pas avoir à refaire des comparaisons de types d'entités ou de créer plein de sous-classes Collision pour chaque type de collision
     */
    Consumer<Collision> resolvingFunction;

    /**
     * Instancie une Collision entre deux entités 'source' et 'target' qui sera résolue via la méthode resolvingFunction
     * @param source première entité impliquée dans la collision
     * @param target deuxième entité impliquée dans la collision
     * @param resolvingFunction fonction utilisée pour la résolution de la collision
     */
    public Collision(Entity source, Entity target, Consumer<Collision> resolvingFunction) {

        this.source = source;
        this.target = target;
        this.resolvingFunction = resolvingFunction;
    }

    /**
     * Execute la fonction de resolution
     */
    public void resolve() {

        resolvingFunction.accept(this);
    }

    /**
     * Renvoie la première entité impliquée dans la collision
     * @return source
     */
    public Entity getSource() {

        return this.source;
    }

    /**
     * Renvoie la deuxième entité impliquée dans la collision
     * @return target
     */
    public Entity getTarget() {

        return this.target;
    }

    /**
     * Permet de déterminer si deux collisions sont égales
     * @param obj deuxième collision pour la comparaison
     * @return true si deux collisions représentent la même paire d'entités
     */
    @Override
    public boolean equals(Object obj) {

        return obj instanceof Collision && ((source == ((Collision) obj).target && target == ((Collision) obj).source) || (source == ((Collision) obj).source && target == ((Collision) obj).target) );
    }

    /**
     * Hashcode utilisé pour stocker les collisions dans un HashSet
     * Un HashSet ajoute un élément si il ne trouve pas d'élément avec un HashCode identique et qui renvoie true avec la comparaison equals
     * Tout le temps 1 pour que seulement equals soit pris en compte, je n'ai pas encore trouvé de bonne fonction de hashing pour une collision puisque les entités n'ont pas d'ID
     *
     * @return 1
     */
    @Override
    public int hashCode() {

        return 1;
    }

    /**
     * Les différents types de collisions possibles, elastiques et non-elastiques
     */
    public enum CollisionType {

        /**
         * Différents types de collisions
         */
        ELASTIC, INELASTIC, /*KINEMATIC*/;

        /**
         * Détermine le type de méthode de calcul à employer pour résoudre la collision entre deux types de collisions différents
         * @param type1 type du premier objet impliqué dans la collision
         * @param type2 type du deuxième objet impliqué dans la collision
         * @return type de collision résultant
         */
        public CollisionType resultingType(CollisionType type1, CollisionType type2) {

            if (type1 == null) return type2;
            if (type2 == null) return type1;

            if (type1 == type2)
                return type1;
            else if ((type1 == ELASTIC && type2 == INELASTIC) || (type1 == INELASTIC && type2 == ELASTIC)) {
                return INELASTIC;
            }

            return ELASTIC;
        }
    }
}
