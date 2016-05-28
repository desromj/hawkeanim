package com.greenbatgames.hawkeanim;

/**
 * Created by Quiv on 28-05-2016.
 */
public class Utils
{
    public static boolean almostEqualTo(float first, float second, float variance)
    {
        return Math.abs(first - second) < variance;
    }
}
