package com.greenbatgames.hawkeanim;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by Quiv on 2016-05-18.
 */
public class Constants
{
    public static final float WORLD_WIDTH = 1920.0f;
    public static final float WORLD_HEIGHT = WORLD_WIDTH * 9.0f / 16.0f;

    public static final Color BG_COLOR = Color.SKY;
    public static final Color PLATFORM_COLOR = Color.BLUE;

    public static final float GRAVITY = -20.0f;
    public static final float KILL_PLANE = -400.0f;

    public static final float HAWKE_RADIUS = WORLD_WIDTH / 48.0f;
    public static final Color HAWKE_COLOR = Color.BROWN;
    public static final Color HAWKE_TEXT_COLOR = Color.WHITE;
    public static final float HAWKE_TEXT_SCALE = 2.0f;
    public static final float HAWKE_DELAY_BETWEEN_FLAPS = 1.0f;
    public static final float HAWKE_IDLE_SPEED_THRESHOLD = 0.1f;
    public static final float HAWKE_WALK_SPEED = 400.0f;
    public static final float HAWKE_RUN_SPEED = 900.0f;
    public static final float HAWKE_JUMP_IMPULSE = 640.0f;
}
