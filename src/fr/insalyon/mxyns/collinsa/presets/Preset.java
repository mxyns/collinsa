package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.utils.Utils;

/**
 * Une classe pour définir des preset executables depuis la commande le temps qu'une IHM soit développée
 */
public abstract class Preset {

    /**
     * Préparation du monde, à faire avant que la simulation soit lancée
     *
     * @param args arguments passés dans la commande de lancement
     * @param collinsa instance de collinsa sur laquelle appliquer le preset
     */
    public abstract void setup(String[] args, Collinsa collinsa);

    /**
     * Méthode lancée après le lancement de la simulation. Généralement utilisée pour y mettre un while(true) {\/* do
     * something *\/}, on peut aussi simplement y mettre une action à faire une seule fois. Cette méthode ne boucle pas
     * par elle même, elle s'appelle 'loop' puisqu'elle sera majoritairement utilisée pour accueillir des boucles
     *
     * @param args     arguments passés dans la commande de lancement
     * @param collinsa instance de collinsa sur laquelle appliquer le preset
     **/
    public void loop(String[] args, Collinsa collinsa) {}

    /**
     * Enum listant les presets et permettant de les executer à partir de leur nom
     */
    public enum EPreset {

        PRESET_1(new Preset_1()),
        PRESET_2(new Preset_2()),
        Friction(new Preset_Friction()),
        Angular_Velocity(new Preset_AngularVelocity()),
        Wheel(new Preset_AngularVelocity()),
        Poly(new Preset_Polygons()),
        GazTest(new Preset_GazTest()),
        GlobalForces(new Preset_GlobalForces()),
        Force(new Preset_Force());

        private final Preset presetInstance;

        EPreset(Preset presetInstance) {

            this.presetInstance = presetInstance;
        }

        public static void run(String name, String[] args, Collinsa collinsa) {

            for (EPreset preset : EPreset.values())
                if (preset.name().toLowerCase().equals(name.toLowerCase())) {

                    Utils.applyParameters(collinsa, args);

                    // Affichage des infos du programme
                    System.out.println("World: " + collinsa.getPhysics());
                    System.out.println("Renderer: " + collinsa.getRenderer());

                    System.out.println("=====- RUNNING PRESET " + preset.name() + " -=====");

                    preset.presetInstance.setup(args, collinsa);

                    collinsa.start();

                    preset.presetInstance.loop(args, collinsa);
                }
        }
    }
}

