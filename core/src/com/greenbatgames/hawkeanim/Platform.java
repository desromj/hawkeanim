package com.greenbatgames.hawkeanim;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Quiv on 2016-05-18.
 */
public class Platform
{
    Vector2 position;
    float width, height;

    public Platform(Vector2 position, float width, float height)
    {
        this(position.x, position.y, width, height);
    }

    public Platform(float x, float y, float width, float height)
    {
        this.position = new Vector2(x + width / 2.0f, y + height / 2.0f);
        this.width = width;
        this.height = height;
    }

    public void render(ShapeRenderer renderer)
    {
        renderer.setColor(Constants.PLATFORM_COLOR);
        renderer.rect(
                this.position.x - this.width / 2.0f,
                this.position.y - this.height / 2.0f,
                this.width,
                this.height
        );
    }
}
