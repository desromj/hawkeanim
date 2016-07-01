package com.greenbatgames.hawkeanim;

import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Quiv on 07-06-2016.
 */
public class SmallBox extends Box
{
    public SmallBox(float x, float y, World world)
    {
        super(x, y, Constants.SMALL_BOX_WIDTH, Constants.SMALL_BOX_HEIGHT, world);
    }
}
