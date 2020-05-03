package fr.insalyon.mxyns.collinsa.physics.collisions;

import fr.insalyon.mxyns.collinsa.physics.Physics;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.utils.Utils;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.util.function.Function;

/**
 * Représente une collision entre deux entités (Entity)
 */
public class Collision {

    /**
     * Les deux entités impliquées dans la collision.
     * Les noms 'reference' et 'incident' ne représentent pas grand chose, ils permettent seulement de mieux voir la différence entre les deux entités que si on utilisait entity1/entityA et entity2/entityB
     * donc Collision(A, B) ≡ Collision(B, A)
     */
    Entity reference, incident;

    /**
     * La fonction à executer pour résoudre la collision.
     * Permet de ne pas avoir à refaire des comparaisons de types d'entités ou de créer plein de sous-classes Collision pour chaque type de collision
     */
    Function<Collision, Boolean> manifoldFunction;

    /**
     * Normale à la surface de la collision de l'incident vers la référence
     */
    public Vec2f normal;

    /**
     * Vecteurs allant du centre de l'entité vers le point de contact (pour chaque point de contact).
     */
    public Vec2f[] centerToContactReference, centerToContactIncident;

    /**
     * Tableau des pénétrations (pour chaque point de contact)
     */
    public float[] penetrations;

    /**
     * Type résultant de la collision, assigné au moment où resolve est appelée, utilisée dans les méthodes de résolutions de collisions
     */
    private CollisionType type;

    /**
     * Instancie une Collision entre deux entités 'reference' et 'incident' qui sera résolue via la méthode resolvingFunction
     * @param reference première entité impliquée dans la collision
     * @param incident deuxième entité impliquée dans la collision
     * @param manifoldFunction fonction utilisée pour la résolution de la collision
     */
    public Collision(Entity reference, Entity incident, Function<Collision, Boolean> manifoldFunction) {

        this.reference = reference;
        this.incident = incident;
        this.manifoldFunction = manifoldFunction;
    }

    /**
     * Execute la fonction de resolution
     */
    public void resolve() {

        // Une collision entre deux éléments cinématiques ne doit pas être résolue puisqu'ils ignorent les modifications de position/vitesse/accélération/... causées par les autres éléments
        // On évite donc des calculs inutiles puisque les résultats ne seront pas utilisés. Par contre, on peut réagir à la détection de la collision (utilisation d'objets comme trigger box par exemple)
        if (reference.isActivated() && incident.isActivated() && (type = CollisionType.resultingType(reference.getCollisionType(), incident.getCollisionType())) != CollisionType.IGNORE) {

            // Generate manifold. If collision detection & resolution fails, apply returns false and collision is skipped
            if (manifoldFunction.apply(this)) {

                // Notify listeners of collision detection
                for (CollisionListener listener : reference.getCollisionListeners())
                    listener.collisionDectected(reference, incident, this);

                for (CollisionListener listener : incident.getCollisionListeners())
                    listener.collisionDectected(incident, reference, this);

                // Facteur 2 si un des deux objets est cinématique puisque l'énergie qu'il ne récupère pas en ignorant les effets de la collision doit être transmise à l'autre objet
                int contactCount = centerToContactIncident.length;

                if (contactCount == 0) {
                    System.out.println("[Collisions.resolve] no penetration, skip");
                    return;
                }

                // Push entities away to prevent them from intersecting
                Physics.displace(reference, incident, normal, Utils.max(penetrations), getType() == CollisionType.KINEMATIC);

                // Foreach contact
                for (int i = 0; i < contactCount; ++i) {

                    // Apply bounce-off if non-kinematic
                    float i_n = Physics.bounceImpulseAmplitude(reference, incident, centerToContactReference[i], centerToContactIncident[i], normal) / contactCount;

                    if (!reference.isKinematic())
                        Physics.applyImpulse(reference, centerToContactReference[i], normal.multOut(i_n));

                    if (!incident.isKinematic())
                        Physics.applyImpulse(incident, centerToContactIncident[i], normal.multOut(-i_n));

                    // Apply friction
                    Vec2f frictionImpulse = Physics.frictionImpulseVector(reference, incident, centerToContactReference[i], centerToContactIncident[i], normal, i_n);
                    if (frictionImpulse != null) {

                        if (!reference.isKinematic())
                            Physics.applyImpulse(reference, centerToContactReference[i], frictionImpulse);

                        if (!incident.isKinematic())
                            Physics.applyImpulse(incident, centerToContactIncident[i], frictionImpulse.neg());
                    }

                    // Notify listeners of resolution
                    for (CollisionListener listener : reference.getCollisionListeners())
                        listener.collisionResolved(reference, incident, this);

                    for (CollisionListener listener : incident.getCollisionListeners())
                        listener.collisionResolved(incident, reference, this);
                }
            }
        } else {

            // Notify listeners of collision ignored
            for (CollisionListener listener : reference.getCollisionListeners())
                listener.collisionIgnored(reference, incident, this);

            for (CollisionListener listener : incident.getCollisionListeners())
                listener.collisionIgnored(incident, reference, this);
        }
    }

    /**
     * Renvoie la première entité impliquée dans la collision
     * @return reference
     */
    public Entity getReference() {

        return this.reference;
    }

    /**
     * Renvoie la deuxième entité impliquée dans la collision
     * @return incident
     */
    public Entity getIncident() {

        return this.incident;
    }

    /**
     * Renvoie le type de collision, s'il n'a pas encore été calculé on le fait avant de le renvoyer
     * @return type
     */
    public CollisionType getType() {

        return type != null ? type : (type = CollisionType.resultingType(reference.getCollisionType(), incident.getCollisionType()));
    }

    /**
     * Permet de déterminer si deux collisions sont égales
     * @param obj deuxième collision pour la comparaison
     * @return true si deux collisions représentent la même paire d'entités
     */
    @Override
    public boolean equals(Object obj) {

        return obj instanceof Collision && ((reference == ((Collision) obj).incident && incident == ((Collision) obj).reference) || (reference == ((Collision) obj).reference && incident == ((Collision) obj).incident) );
    }

    /**
     * Hashcode utilisé pour stocker les collisions dans un HashSet
     * Un HashSet ajoute un élément si il ne trouve pas d'élément avec un HashCode identique et qui renvoie true avec la comparaison equals
     * Tout le temps 1 pour que seulement equals soit pris en compte, je n'ai pas encore trouvé de bonne fonction de hashing pour une collision puisque les entités n'ont pas d'ID
     *
     * @link https://android.googlereference.com/platform/frameworks/base/+/master/core/java/android/util/Pair.java hashCode method
     * @return 1
     */
    @Override
    public int hashCode() {

        return (reference == null ? 0 : reference.hashCode()) ^ (incident == null ? 0 : incident.hashCode());
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