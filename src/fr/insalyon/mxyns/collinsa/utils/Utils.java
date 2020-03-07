package fr.insalyon.mxyns.collinsa.utils;

public class Utils {

    public static float min(float... values) {

        float min = values[0];
        for (int i = 1; i < values.length; ++i)
            if (values[i] < min)
                min = values[i];

        return min;
    }

    public static float max(float... values) {

        float max = values[0];
        for (int i = 1; i < values.length; ++i)
            if (values[i] > max)
                max = values[i];

        return max;
    }
}
