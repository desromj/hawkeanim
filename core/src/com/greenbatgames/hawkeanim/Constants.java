package com.greenbatgames.hawkeanim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Quiv on 2016-05-18.
 */
public class Constants
{
    // Pixel-to-metre ratio for Box2D physics engine
    public static final float PTM = 116.0f;
    public static final float PHYSICS_STEP_FREQ = 1f/60f;
    public static final int PHYSICS_VEL_ITERATIONS = 6;
    public static final int PHYSICS_POS_ITERATIONS = 2;

    // Density values, in kg/m^3 (human average is 985, water is 1020, wood about 650, but varies)
    public static final float HAWKE_DENSITY = 600.0f;
    public static final float BOX_DENSITY = 660.0f;

    // regular constants
    public static final float WORLD_WIDTH = 1920.0f;
    public static final float WORLD_HEIGHT = WORLD_WIDTH * 9.0f / 16.0f;

    public static final float CHASE_CAM_MOVE_SPEED = 1600.0f;
    public static final float CHASE_CAM_X_LEEWAY = WORLD_WIDTH / 9.0f;
    public static final float CHASE_CAM_Y_LEEWAY = WORLD_HEIGHT / 5.0f;

    public static final Color BG_COLOR = Color.SKY;

    public static final float GRAVITY = -20f;
    public static final float GLIDE_CONSTANT_GRAVITY = -20f;
    public static final Vector2 GLIDE_DRAG_FORCE = new Vector2(0f, 12f);
    public static final float KILL_PLANE = -400.0f;

    public static final float HORIZONTAL_WALK_DAMPEN = 0.8f;
    public static final float HORIZONTAL_FALL_DAMPEN = 0.95f;
    public static final float HORIZONTAL_GLIDE_DAMPEN = 0.98f;

    public static final float HAWKE_RADIUS = WORLD_WIDTH / 42.0f;
    public static final Color HAWKE_COLOR = Color.BROWN;
    public static final Color HAWKE_TEXT_COLOR = Color.WHITE;
    public static final float HAWKE_TEXT_SCALE = 2.0f;
    public static final float HAWKE_DELAY_BETWEEN_FLAPS = GRAVITY / -50.0f;

    public static final float HAWKE_IDLE_SPEED_THRESHOLD = 25.0f;
    public static final float HAWKE_WALK_SPEED = 400.0f;
    public static final float HAWKE_RUN_SPEED = 900.0f;
    public static final float HAWKE_GLIDE_WALK_SPEED = 15.0f;
    public static final float HAWKE_GLIDE_RUN_SPEED = 40.0f;
    public static final float HAWKE_MAX_GLIDE_SPEED = 600.0f;
    public static final float HAWKE_JUMP_IMPULSE = 500.0f * HAWKE_DENSITY;

    public static final float HAWKE_VERTEX_X_SCALE = HAWKE_RADIUS;
    public static final float HAWKE_VERTEX_Y_SCALE = HAWKE_RADIUS * 2.0f;

    public static final Vector2 [] HAWKE_VERTICIES = new Vector2[] {
            new Vector2(0.90f * HAWKE_VERTEX_X_SCALE / PTM, 0.67f * HAWKE_VERTEX_Y_SCALE / PTM),
            new Vector2(0.33f * HAWKE_VERTEX_X_SCALE / PTM, 1.00f * HAWKE_VERTEX_Y_SCALE / PTM),
            new Vector2(-0.33f * HAWKE_VERTEX_X_SCALE / PTM, 1.00f * HAWKE_VERTEX_Y_SCALE / PTM),
            new Vector2(-0.90f * HAWKE_VERTEX_X_SCALE / PTM, 0.67f * HAWKE_VERTEX_Y_SCALE / PTM),
            new Vector2(-0.90f * HAWKE_VERTEX_X_SCALE / PTM, -0.67f * HAWKE_VERTEX_Y_SCALE / PTM),
            new Vector2(-0.33f * HAWKE_VERTEX_X_SCALE / PTM, -1.00f * HAWKE_VERTEX_Y_SCALE / PTM),
            new Vector2(0.33f * HAWKE_VERTEX_X_SCALE / PTM, -1.00f * HAWKE_VERTEX_Y_SCALE / PTM),
            new Vector2(0.90f * HAWKE_VERTEX_X_SCALE / PTM, -0.67f * HAWKE_VERTEX_Y_SCALE / PTM)
    };

    public static final float WOBBLE_ROOM = WORLD_WIDTH / 16000f;

    public static final Color PLATFORM_COLOR = Color.BLUE;
    public static final float PLATFORM_COLLISION_LEEWAY = WORLD_WIDTH / 960.0f;
    public static final float DISABLE_COLLISION_FOR_PLATFORM = 0.25f;
    public static final float PLATFORM_EDGE_LEEWAY = HAWKE_RADIUS / 1.5f;

    public static final float SMALL_BOX_WIDTH = 80f;
    public static final float SMALL_BOX_HEIGHT = 50f;
    public static final float MEDIUM_BOX_WIDTH = 120f;
    public static final float MEDIUM_BOX_HEIGHT = 120f;

    public static final Color BOX_COLOR = Color.FIREBRICK;

}
