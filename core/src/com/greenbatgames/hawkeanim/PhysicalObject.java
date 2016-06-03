package com.greenbatgames.hawkeanim;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Quiv on 03-06-2016.
 */
public abstract class PhysicalObject
{
    Vector2 position;
    float width, height;

    Body body;

    public PhysicalObject(float x, float y, float width, float height, World world)
    {
        this.position = new Vector2(x, y);
        this.width = width;
        this.height = height;

        initPhysics(world);
    }

    protected abstract void initPhysics(World world);
    public abstract void render(ShapeRenderer renderer);

    public void update(float delta)
    {
        // Cling this object's position to the physics body
        this.position.set(
                (this.body.getPosition().x * Constants.PTM) - this.width / 2.0f,
                (this.body.getPosition().y * Constants.PTM) - this.height / 2.0f
        );
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Body getBody() {
        return body;
    }
}
