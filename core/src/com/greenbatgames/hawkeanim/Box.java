package com.greenbatgames.hawkeanim;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Quiv on 31-05-2016.
 */
public class Box extends PhysicalObject
{
    public Box(float x, float y, float width, float height, World world)
    {
        super(x, y, width, height, world);
    }

    @Override
    protected void initPhysics(World world)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(
                (this.position.x + this.width / 2.0f) / Constants.PTM,
                (this.position.y + this.height / 2.0f) / Constants.PTM);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
                (this.width / 2.0f) / Constants.PTM,
                (this.height / 2.0f) / Constants.PTM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = Constants.BOX_DENSITY;
        fixtureDef.restitution = 0.2f;
        fixtureDef.friction = 1.0f;

        this.body = world.createBody(bodyDef);
        this.body.createFixture(fixtureDef);
        this.body.setUserData(this);

        shape.dispose();
    }

    @Override
    public void render(ShapeRenderer renderer)
    {
        renderer.setColor(Constants.BOX_COLOR);
        renderer.rect(
                this.position.x,
                this.position.y,
                this.width / 2.0f,
                this.height / 2.0f,
                this.width,
                this.height,
                1.0f,
                1.0f,
                this.body.getAngle() * MathUtils.radDeg
        );
    }
}
