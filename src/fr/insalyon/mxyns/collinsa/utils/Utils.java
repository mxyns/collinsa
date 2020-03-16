package fr.insalyon.mxyns.collinsa.utils;

public class Utils {

    /**
     * Renvoie le minimum des valeurs données
     * @param values valeurs v_i
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
     * @param values valeurs v_i
     * @return max(v_i)
     */
    public static float max(float... values) {

        float max = values[0];
        for (int i = 1; i < values.length; ++i)
            if (values[i] > max)
                max = values[i];

        return max;
    }

    public static int lookForString(String toLookFor, String... array) {

        for (int i = 0; i < array.length; ++i) {
            if (array[i].toLowerCase().equals(toLookFor.toLowerCase()))
                return i;
        }

        return -1;
    }

    public static String getArgValue(String arg, String[] args) {

        int argIndex;
        if ((argIndex = lookForString("-"+arg, args)) != -1 && argIndex+1 < args.length)
            return args[argIndex+1];

        return null;
    }
}
