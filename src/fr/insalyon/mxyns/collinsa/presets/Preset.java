package fr.insalyon.mxyns.collinsa.presets;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.utils.Utils;

import java.awt.Color;

/**
 * Une classe pour définir des preset executables depuis la commande le temps qu'une IHM soit développée
 */
public abstract class Preset {

    /**
     * Préparation du monde, à faire avant que la simulation soit lancée
     * @param args arguments passés dans la commande de lancement
     * @param collinsa instance de collinsa sur laquelle appliquer le preset
     */
    public abstract void setup(String[] args, Collinsa collinsa);

    /**
     * Méthode lancée après le lancement de la simulation.
     * Généralement utilisée pour y mettre un while(true) {\/* do something *\/}, on peut aussi simplement y mettre une action à faire une seule fois.
     * Cette méthode ne boucle pas par elle même, elle s'appelle 'loop' puisqu'elle sera majoritairement utilisée pour accueillir des boucles
     * @param args arguments passés dans la commande de lancement
     * @param collinsa instance de collinsa sur laquelle appliquer le preset
     **/
    public void loop(String[] args, Collinsa collinsa) {}

    public enum EPreset {

        PRESET_1(new Preset_1()),
        PRESET_2(new Preset_2()),
        Friction(new Preset_Friction());

        private Preset presetInstance;

        EPreset(Preset presetInstance) {

            this.presetInstance = presetInstance;
        }

        public static void run(String name, String[] args, Collinsa collinsa) {

            for (EPreset preset : EPreset.values())
                if (preset.name().toLowerCase().equals(name.toLowerCase())) {
                    System.out.println("=====- RUNNING PRESET " + preset.name() + " -=====");
                    preset.presetInstance.setup(args, collinsa);

                    // apply other args
                    try {
                        if (Utils.lookForString("--showAABB", args) != -1)
                            collinsa.getRenderer().setRenderEntitiesAABB(Boolean.parseBoolean(Utils.getArgValue("-showAABB", args)));
                        if (Utils.lookForString("--aabbColor", args) != -1) {
                            Color color = null;
                            try {
                                color = (Color)Color.class.getField(Utils.getArgValue("-aabbColor", args).toUpperCase()).get(null);
                            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                                e.printStackTrace();
                            }
                            if (color != null)
                                collinsa.getRenderer().setAABBBoundsColor(color);
                        }
                        if (Utils.lookForString("--showAxes", args) != -1)
                            collinsa.getRenderer().setRenderCoordinateSystem(Boolean.parseBoolean(Utils.getArgValue("-showAxes", args)));
                        if (Utils.lookForString("--showChunks", args) != -1)
                            collinsa.getRenderer().setRenderChunksBounds(Boolean.parseBoolean(Utils.getArgValue("-showChunks", args)));
                        if (Utils.lookForString("--chunkColor", args) != -1) {
                            Color color = null;
                            try {
                                color = (Color)Color.class.getField(Utils.getArgValue("-chunkColor", args).toUpperCase()).get(null);
                            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                                e.printStackTrace();
                            }
                            if (color != null)
                                collinsa.getRenderer().setChunkBoundsColor(color);
                        }
                        if (Utils.lookForString("--showWorldBounds", args) != -1)
                            collinsa.getRenderer().setRenderWorldBounds(Boolean.parseBoolean(Utils.getArgValue("-showWorldBounds", args)));
                        if (Utils.lookForString("--scale", args) != -1)
                            collinsa.getRenderer().setRenderScale(Float.parseFloat(Utils.getArgValue("-scale", args)));
                        if (Utils.lookForString("--realtime", args) != -1)
                            collinsa.getPhysics().setRealtime(Boolean.parseBoolean(Utils.getArgValue("-realtime", args)));
                        if (Utils.lookForString("--dt", args) != -1)
                            collinsa.getPhysics().setFixedDeltaTime(Integer.parseInt(Utils.getArgValue("-dt", args)));
                        if (Utils.lookForString("--fpsp", args) != -1)
                            collinsa.getPhysics().getProcessingThread().setRefreshRate(Integer.parseInt(Utils.getArgValue("-fpsp", args)));
                        if (Utils.lookForString("--fpsr", args) != -1)
                            collinsa.getRenderer().getRenderingThread().setFramerate(Integer.parseInt(Utils.getArgValue("-fpsr", args)));
                        if (Utils.lookForString("--fpsd", args) != -1)
                            collinsa.getMainFrame().getSandboxPanel().getRefreshingThread().setRefreshRate(Integer.parseInt(Utils.getArgValue("-fpsd", args)));
                    } catch (NumberFormatException | NullPointerException e1) {
                        System.out.println("wrong parameters format");
                    }

                    collinsa.start();

                    preset.presetInstance.loop(args, collinsa);
                }

        }
    }
}

