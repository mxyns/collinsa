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
     * Type résultant de la collision, assigné au moment où resolve est appelée, utilisée dans les méthodes de résolutions de collisions
     */
    private CollisionType type;

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

        // Une collision entre deux éléments cinématiques ne doit pas être résolue puisqu'ils ignorent les modifications de position/vitesse/accélération/... causées par les autres éléments
        // On évite donc des calculs inutiles puisque les résultats ne seront pas utilisés. Par contre, on peut réagir à la détection de la collision (utilisation d'objets comme trigger box par exemple)
        if (source.isActivated() && target.isActivated() && (type = CollisionType.resultingType(source.getCollisionType(), target.getCollisionType())) != CollisionType.IGNORE)
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
     * Renvoie le type de collision, s'il n'a pas encore été calculé on le fait avant de le renvoyer
     * @return type
     */
    public CollisionType getType() {

        return type != null ? type : (type = CollisionType.resultingType(source.getCollisionType(), target.getCollisionType()));
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
     * @link https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/util/Pair.java hashCode method
     * @return 1
     */
    @Override
    public int hashCode() {

        return (source == null ? 0 : source.hashCode()) ^ (target == null ? 0 : target.hashCode());
    }

    /**
     * Les différents types de collisions possibles : cinématiques ou classiques
     */
    public enum CollisionType {

        /**
         * Différents types de collisions
         *  - CLASSIC : collision classique (elastique ou non-elastique selon les coefficients de restitution) avec tous les objets
         *  - KINEMATIC : objets cinématiques, dirigés uniquement par vitesse/acc/etc. résistent à la collision et n'interagissent pas avec les autres objets de type KINEMATIC
         *  - IGNORE : ignore totalement toutes collisions, et ne n'interagit avec aucune entité
         */
        CLASSIC, KINEMATIC, IGNORE;

        /**
         * Détermine le type de méthode de calcul à employer pour résoudre la collision entre deux types de collisions différents
         * IGNORE > KINEMATIC > CLASSIC
         * @param type1 type du premier objet impliqué dans la collision
         * @param type2 type du deuxième objet impliqué dans la collision
         * @return type de collision résultant
         */
        public static CollisionType resultingType(CollisionType type1, CollisionType type2) {

            if (type1 == null || type2 == null || type1 == IGNORE || type2 == IGNORE)
                return IGNORE;

            if (type1 == KINEMATIC || type2 == KINEMATIC)
                if (type1 == type2)
                    return IGNORE;
                else
                    return KINEMATIC;

            return CLASSIC;
        }
    }
}