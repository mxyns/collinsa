package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;

import java.util.HashMap;

/**
 * Une classe pour définir des preset executables depuis la commande le temps qu'une IHM soit développée
 */
public abstract class Preset {

    private final static HashMap<String, Preset> registeredPresets;

    static {
        registeredPresets = new HashMap<>();

        registerPreset("mesh", new Preset_Mesh());
        registerPreset("1", new Preset_1());
        registerPreset("2", new Preset_2());
        registerPreset("friction", new Preset_Friction());
        registerPreset("angularvelocity", new Preset_AngularVelocity());
        registerPreset("polygons", new Preset_Polygons());
        registerPreset("gaztest", new Preset_GazTest());
        registerPreset("globalforces", new Preset_GlobalForces());
        registerPreset("huge", new Preset_Huge());
        registerPreset("tests", new Presets_Tests());
        registerPreset("force", new Preset_Force());
        registerPreset("temp", new Preset_Temp());
        registerPreset("boxdrag", new Preset_BoxDrag());
    }

    /**
     * Préparation du monde, à faire avant que la simulation soit lancée
     *
     * @param args     arguments passés dans la commande de lancement
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

    public static void registerPreset(String name, Preset instance) {

        registeredPresets.put(name, instance);
    }

    public static void runPreset(String name, String[] args, Collinsa collinsa) {

        if (registeredPresets.containsKey(name.toLowerCase())) {

            Preset presetInstance = registeredPresets.get(name);

            System.out.println("=====- RUNNING PRESET " + name + " -=====");

            presetInstance.setup(args, collinsa);

            collinsa.start();

            Thread thread = new Thread(() -> presetInstance.loop(args, collinsa));
            thread.setName("collinsa-preset-" + name.toLowerCase());
            thread.start();
        } else {
            System.err.println("Couldn't find any preset named '" + name + "'");
        }
    }
}
