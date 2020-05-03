package fr.insalyon.mxyns.collinsa.physics;

import java.awt.Color;
import java.lang.reflect.Field;

/**
 * Classe représentant un matériau et ses propriétés
 */
public class Material {

    /**
     * Matériaux par défaut
     */
    public static Material DUMMY = new Material(.5f, 0.7f, 0.2f, 0.1f, Color.blue);
    public static Material STICKY = new Material(.5f, 0.5f, 1f, 0.9f, new Color(120, 0, 0));
    public static Material BOUNCY = new Material(.5f, 3f, 0.05f, 0.025f, new Color(0, 120, 0));
    public static Material SLIDY = new Material(.5f, 0.1f, 0.05f, 0.025f, Color.cyan);
    /**
     * Matériaux réels
     * @link https://fr.wikiversity.org/wiki/Frottement_et_adh%C3%A9rence/Coefficient_de_frottement_a_(ou_f)
     * @link https://owl-ge.ch/IMG/pdf/frottement.pdf
     * @link https://www.physlink.com/reference/frictioncoefficients.cfm
      */
    public static Material ROCK = new Material(0.6f, 0.1f, 0.38f, 0.2f, Color.gray, Color.darkGray);
    public static Material WOOD = new Material(0.3f, 0.2f, .5f, .3f, new Color(82, 59, 32), new Color(191, 143, 89));
    public static Material METAL = new Material(1.2f, 0.05f, .1f, .07f, Color.lightGray, Color.gray);
    public static Material COTTON = new Material(0.1f, 0.2f, .03f, .01f, Color.white, Color.gray);


    /**
     * Coefficient de restitution, représente l'élasticité ou la "bounciness" / rebond
     * e = 1 <=> collision élastique
     */
    float restitution;

    /**
     * Coefficient de friction statique, détermine quelle vitesse il faut pour que deux objets se mettent en mouvement
     */
    float staticFriction;

    /**
     * Coefficient de friction dynamique, représente les frottements / l'accroche d'un objet aux autres quand ils sont en mouvement
     */
    float dynamicFriction;

    /**
     * Couleurs par défaut d'un objet ayant ce matériau.
     * outlineColor => couleur des bordures
     * fillColor => couleur du fond
     */
    Color outlineColor, fillColor;

    /**
     * Densité utilisée pour calculer la masse
     */
    float density;

    public Material(float density, float restitution, float staticFriction, float dynamicFriction, Color outlineColor, Color fillColor) {

        this.restitution = restitution;
        this.staticFriction = staticFriction;
        this.dynamicFriction = dynamicFriction;
        this.density = density;
        this.outlineColor = outlineColor;
        this.fillColor = fillColor;
    }
    public Material(float density, float restitution, float staticFriction, float dynamicFriction, Color fillColor) {

        this.restitution = restitution;
        this.staticFriction = staticFriction;
        this.dynamicFriction = dynamicFriction;
        this.density = density;
        this.outlineColor = Color.black;
        this.fillColor = fillColor;
    }

    /**
     * Renvoie la couleur de bordures par défaut du matériau. Peut être modifiée ou remplacée manuellement avec Entity.setOutlineColor()
     * @return couleur par défaut du matériau
     */
    public Color getOutlineColor() {

        return outlineColor;
    }
    /**
     *   Renvoie la couleur de fond par défaut du matériau. Peut être modifiée ou remplacée manuellement avec Entity.setFillColor()
     * @return couleur par défaut du matériau
     */
    public Color getFillColor() {

        return fillColor;
    }

    /**
     * Renvoie la densité du matériau en kg/m^3
     * @return density
     */
    public float getDensity() {

        return density;
    }

    /**
     * Redéfinit la densité du matériau en kg/m^3.
     * Nécessite d'executer Entity.getInertia()::update pour que les changements soient appliqués à l'entité
     * @param density nouvelle densité en kg/m^3
     */
    public void setDensity(float density) {

        this.density = density;
    }

    /**
     * Renvoie le coefficient de restitution du matériau.
     * < 1 inelastic
     * = 1 elastic
     * > unreal but can be useful
     * @return restitution
     */
    public float getRestitution() {

        return restitution;
    }

    /**
     * Redéfinit la restitution du matériau
     * @param restitution nouvelle restitution
     */
    public void setRestitution(float restitution) {

        this.restitution = restitution;
    }

    /**
     * Méthode de calcul du coefficient de frottement équivalent entre deux matériaux.
     * Pour deux matériaux A et B de friction respective fA et fB on obtient √(fA²+fB²)
     * N'a pas d'explication physique puisque il n'existe aucune formule pour déterminer les coefficients de frottements d'un matériau.
     * @param frictionA coefficient de friction du premier matériau
     * @param frictionB coefficient de friction du deuxième matériau
     * @return moyenne des deux coefficients
     */
    public static float frictionAverage(float frictionA, float frictionB) {

        return (float) Math.sqrt(frictionA*frictionA + frictionB*frictionB);
    }

    /**
     * Renvoie le coefficient de friction statique (~ énergie nécessaire pour qu'un objet se mette en mouvement)
     * @return staticFriction
     */
    public float getStaticFriction() {

        return staticFriction;
    }

    /**
     * Rédefinit le coefficient de friction statique (~ énergie nécessaire pour qu'un objet se mette en mouvement)
     * @param staticFriction nouveau coefficient de frottement statique
     */
    public void setStaticFriction(float staticFriction) {

        this.staticFriction = staticFriction;
    }

    /**
     * Renvoie le coefficient de friction dynamique (frottements lors de déplacements)
     * @return staticFriction
     */
    public float getDynamicFriction() {

        return dynamicFriction;
    }

    /**
     * Rédefinit le coefficient de friction dynamique (frottements lors de déplacements)
     * @param dynamicFriction nouveau coefficient de frottement dynamique
     */
    public void setDynamicFriction(float dynamicFriction) {

        this.dynamicFriction = dynamicFriction;
    }

    /**
     * Renvoie une copie du matériau, permet de ne pas avoir tous les éléments d'un même matériau liés
     * @return copy of the current material
     */
    public Material copy() {

        return new Material(density, restitution, staticFriction, dynamicFriction, outlineColor, fillColor);
    }

    /**
     * Donne le nom d'un matériau à partir du nom de son champs dans Material. Créer un attribut "name" serait plus pratique mais bon... ça permet de détecter les matériaux customisés
     * @param material matériau dont il faut récupérer le nom
     * @return soit le nom du matériau. soit Material@xxxxx s'il n'est pas un matériau par défaut
     */
    public static String getMaterialName(Material material) {

        if (material == null)
            return ""; // On voudrait pas causer des NullPointerException un peu partout par inadvertance

        for (Field field : Material.class.getFields()) {
            try {
                if (field.get(null).equals(material))
                    return field.getName();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return material.toString().substring(material.toString().lastIndexOf('.') + 1);
    }

    /**
     * Renvoie un matériau à partir de son nom. C'est l'inverse de Material.getMaterialName(...)
     * @param name nom du matériau
     * @return Materiau correpondant ou null
     */
    public static Material getMaterialFromName(String name) {

        for (Field field : Material.class.getFields()) {
            try {
                if (field.getName().equalsIgnoreCase(name) && field.getType().equals(Material.class))
                    return (Material) field.get(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Redéfinition du equals.
     * @param obj object to test
     * @return true if 'obj' has same fields values
     */
    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Material))
            return false;

        Material mat = (Material) obj;

        return mat.restitution == restitution && mat.density == density && mat.dynamicFriction == dynamicFriction && mat.staticFriction == staticFriction && mat.outlineColor.equals(outlineColor) && mat.fillColor.equals(fillColor);
    }
}
