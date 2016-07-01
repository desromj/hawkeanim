package com.greenbatgames.hawkeanim;

import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Quiv on 07-06-2016.
 */
public class MediumBox extends Box
{
    public MediumBox(float x, float y, World world)
    {
        super(x, y, Constants.MEDIUM_BOX_WIDTH, Constants.MEDIUM_BOX_HEIGHT, world);
    }
}
