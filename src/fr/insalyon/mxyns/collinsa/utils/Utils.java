package fr.insalyon.mxyns.collinsa.utils;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.utils.geo.Vec2f;

import java.awt.Color;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Classe contenant des méthodes utilitaires
 */
public class Utils {

    /**
     * Renvoie le minimum des valeurs données
     *
     * @param values valeurs v_i
     *
     * @return min(v_i)
     */
    public static float min(float... values) {

        float min = values[0];
        for (int i = 1; i < values.length; ++i)
            if (values[i] < min)
                min = values[i];

        return min;
    }

    /**
     * Renvoie le maximum des valeurs données
     *
     * @param values valeurs v_i
     *
     * @return max(v_i)
     */
    public static float max(float... values) {

        float max = values[0];
        for (int i = 1; i < values.length; ++i)
            if (values[i] > max)
                max = values[i];

        return max;
    }

    /**
     * Renvoie l'index d'un String dans un tableau de String
     * @param toLookFor String à chercher
     * @param array tableau de recherche
     * @return index de 'toLookFor' dans 'array', -1 si pas présent
     */
    public static int lookForString(String toLookFor, String... array) {

        for (int i = 0; i < array.length; ++i) {
            if (array[i].toLowerCase().equals(toLookFor.toLowerCase()))
                return i;
        }

        return -1;
    }

    /**
     * Renvoie la chaine de caractère suivant "-" + arg dans 'args'
     * @return null si il n'y a rien ou si le tableau est trop petit
     */
    public static String getArgValue(String arg, String[] args) {

        int argIndex;
        if ((argIndex = lookForString("-" + arg, args)) != -1 && argIndex + 1 < args.length)
            return args[argIndex + 1];

        return null;
    }

    /**
     * Récupère la valeur d'un paramètres dans un tableau, le cast et l'applique avec une fonction. Si il y a un problème lors d'une de ces opérations, c'est la valeur par défaut qui est appliquée
     * @param arg paramètre recherché
     * @param defaultValue valeur par défaut en cas de problème
     * @param args tableau des paramètres (passé dans main)
     * @param function fonction à appliquer
     * @param <T> type de variable
     * @return false si defaultValue est appliqué, true sinon
     */
    public static <T> boolean applyParameter(String arg, T defaultValue, String[] args, Consumer<T> function) {

        T value = getParameter(arg, defaultValue, args);
        function.accept(value);

        return (defaultValue == value);
    }

    /**
     * Récupère la valeur d'un paramètres dans un tableau, le cast et la renvoie. Si il y a un problème lors d'une de ces opérations, c'est la valeur par défaut qui est renvoyée
     * @param arg paramètre recherché
     * @param defaultValue valeur par défaut en cas de problème
     * @param args tableau des paramètres (passé dans main)
     * @param <T> type de variable
     * @return la valeur du paramètre 'arg' si elle est donnée, defaultValue sinon
     */
    public static <T> T getParameter(String arg, T defaultValue, String[] args) {

        if (lookForString(arg, args) == -1) return defaultValue;
        String value = getArgValue(arg.substring(1), args);
        T result = null;
        Exception caught = null;

        if (defaultValue instanceof Integer) {
            try {
                result = (T) Integer.valueOf(value);
            } catch (Exception e) {caught = e;}
        } else if (defaultValue instanceof Float) {
            try {
                result = (T) Float.valueOf(value);
            } catch (Exception e) {caught = e;}
        } else if (defaultValue instanceof Double) {
            try {
                result = (T) Double.valueOf(value);
            } catch (Exception e) {caught = e;}
        } else if (defaultValue instanceof Boolean) {
            try {
                result = (T) Boolean.valueOf(value);
            } catch (Exception e) {caught = e;}
        } else if (defaultValue instanceof Color) {

            try {
                result = (T) parseColor(value);

            } catch (Exception e) {
                caught = e;
            }
        } else if (defaultValue instanceof Vec2f) {

            try {
                result = (T) parseVector(value);

            } catch (Exception e) {
                caught = e;
            }
        } else {

            System.out.println("Unsupported type");
        }

        if (caught != null) {
            caught.printStackTrace();
            System.out.println("Wrong parameter given : " + arg + " " + value + ". Try format " + arg + " <" + defaultValue.getClass().getSimpleName() + "> ." +  " Applied default value: " + defaultValue);
        } else
            return result;

        return defaultValue;
    }

    /**
     * Applique tous les paramètres par défaut (ou donnés) à une instance de Collinsa
     * @param collinsa instance sur laquelle appliquer les paramètres
     * @param args paramètres donnés dans (main)
     */
    public static void applyParameters(Collinsa collinsa, String[] args) {

        // https://github.com/mxyns/collinsa
        Utils.applyParameter("--showAABB", false, args, collinsa.getRenderer()::setRenderEntitiesAABB);
        Utils.applyParameter("--aabbColor", Color.yellow, args, collinsa.getRenderer()::setAABBBoundsColor);
        Utils.applyParameter("--showAxes", false, args, collinsa.getRenderer()::setRenderCoordinateSystem);
        Utils.applyParameter("--showChunks", false, args, collinsa.getRenderer()::setRenderChunksBounds);
        Utils.applyParameter("--chunkColor", Color.black, args, collinsa.getRenderer()::setChunkBoundsColor);
        Utils.applyParameter("--showWorldBounds", true, args, collinsa.getRenderer()::setRenderWorldBounds);
        Utils.applyParameter("--scale", 1f, args, collinsa.getRenderer()::setRenderScale);
        Utils.applyParameter("--realtime", false, args, collinsa.getPhysics()::setRealtime);
        Utils.applyParameter("--dt", 10, args, collinsa.getPhysics()::setFixedDeltaTime);
        Utils.applyParameter("--fpsp", 60, args, collinsa.getPhysics().getProcessingThread()::setRefreshRate);
        Utils.applyParameter("--fpsr", 60, args, collinsa.getRenderer().getRenderingThread()::setFramerate);
        Utils.applyParameter("--fpsd", 60, args, collinsa.getMainFrame().getSandboxPanel().getRefreshingThread()::setRefreshRate);
        Utils.applyParameter("--bgColor", Color.white, args, collinsa.getRenderer().getGraphicsBuffer()::setBackgroundColor);
        Utils.applyParameter("--worldBoundsColor", Color.black, args, collinsa.getRenderer()::setWorldBoundsColor);
        Utils.applyParameter("--useDebugColors", false, args, collinsa.getPhysics().getCollider()::setDisplayCollisionColor);

        Utils.applyParameter("--chunkCount", new Vec2f(3, 3), args, collinsa.getPhysics()::setChunkCount);
        Utils.applyParameter("--worldSize", new Vec2f(1440, 810), args, collinsa.getPhysics()::resize);

        float width = Utils.getParameter("--width", collinsa.getPhysics().getWidth(), args);
        float height = Utils.getParameter("--height", collinsa.getPhysics().getHeight(), args);
        collinsa.getPhysics().resize(new Vec2f(width, height));
    }

    /**
     * Parse une couleur si elle est définie dans java.awt.Color ou si elle est au format rgb(a,b,c) avec a b et c entre 0 et 255
     * @param str texte à parser
     * @return Color si pas d'erreur
     * @throws Exception une des multiples exception qui peuvent se produire durant le parsing
     */
    public static Color parseColor(String str) throws Exception {

        Exception toThrow = null;
        Color result = null;

        try {
            result = (Color) Color.class.getField(str).get(null);
        } catch (Exception e) {
            toThrow = e;

            if (Pattern.matches("(?:rgb)\\((?:[\\d]{1,3},?){3}\\)", str)) {
                str = str.substring(4, str.length() - 1);
                String[] values = str.split(",");
                Integer[] rgbValues = new Integer[3];
                for (int i = 0; i < 3; ++i) {
                    if (values[i] != null && !values[i].isEmpty()) {
                        try {
                            rgbValues[i] = constrain(Integer.parseInt(values[i]), 0, 255);
                        } catch (Exception e2) {
                            toThrow = e2;
                            --i;
                        }
                    } else --i;
                }

                result = new Color(rgbValues[0], rgbValues[1], rgbValues[2]);
            }
        }

        if (result != null)
            return result;
        else if (toThrow != null)
            toThrow.printStackTrace();

        throw new Exception("Couldn't cast '" + str + "' to Color. Use already existing field name in class java.awt.Color (case sensitive) or the following format: rgb(r,g,b). With r, g & b in range [0,255]. /!\\ No spaces ! /!\\");
    }

    private static Vec2f parseVector(String str) throws Exception {

        Exception toThrow = null;
        Vec2f result = null;

        if (Pattern.matches("(?:(?:Vec2f)|(?:vec2f))\\((?:[\\d]+,?){2}\\)", str)) {
            str = str.substring(6, str.length() - 1);
            String[] values = str.split(",");
            Float[] posValues = new Float[2];
            for (int i = 0; i < 2; ++i) {
                if (values[i] != null && !values[i].isEmpty()) {
                    try {
                        posValues[i] = Float.parseFloat(values[i]);
                    } catch (Exception e2) {
                        toThrow = e2;
                        --i;
                    }
                } else --i;
            }

            result = new Vec2f(posValues[0], posValues[1]);
        }

        if (result != null)
            return result;
        else if (toThrow != null)
            toThrow.printStackTrace();

        throw new Exception("Couldn't cast '" + str + "' to Vec2f. Use the following format: vec2f(a,b).");
    }

    /**
     * Limite une valeur à un intervalle. Identique à constrain en Processing
     * @param value valeur à constrain
     * @param min minimum de l'intervalle
     * @param max maximum de l'intervalle
     * @return valeur contrainte à l'intervalle
     */
    public static int constrain(int value, int min, int max) {

        if (min > max)
            return constrain(value, max, min);

        return Math.min(Math.max(min, value), max);
    }

    public static float mean(float[] penetrations) {

        if (penetrations.length == 0) return 0;

        float sum = 0;
        for (float f : penetrations)
            sum+=f;

        return sum/penetrations.length;
    }
}
