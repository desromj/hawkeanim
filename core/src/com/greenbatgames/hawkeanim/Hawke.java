package com.greenbatgames.hawkeanim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Quiv on 2016-05-18.
 */
public class Hawke
{
    AnimationState animationState;
    Vector2 position, velocity;
    private Vector2 lastPosition;

    boolean grounded, flapping;
    float cannotFlapFor;

    SpriteBatch batch;
    BitmapFont font;

    public Hawke(Vector2 position)
    {
        this(position.x, position.y);
    }

    public Hawke(float x, float y)
    {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2();
        this.lastPosition = new Vector2();
        this.animationState = AnimationState.FALLING;
        this.grounded = false;
        this.flapping = false;
        this.cannotFlapFor = 0.0f;

        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.font.setColor(Constants.HAWKE_TEXT_COLOR);
        this.font.getData().setScale(Constants.HAWKE_TEXT_SCALE);
    }

    public void update(float delta, Array<Platform> platforms)
    {
        this.position.x += this.velocity.x * delta;
        this.position.y += this.velocity.y * delta;

        // Platform collision logic
        boolean onPlatform = false;

        for (Platform platform: platforms)
        {
            if (this.hasCollided(platform))
            {
                onPlatform = true;
                this.grounded = true;
                this.flapping = false;

                // Constrain player to the top of the platform
                this.position.y = platform.position.y + platform.height / 2.0f + Constants.HAWKE_RADIUS;
                break;
            }
        }

        if (!onPlatform)
            this.grounded = false;

        // Make the player fall if they are not already grounded
        if (this.grounded)
            this.velocity.y = 0.0f;
        else
            this.velocity.y += Constants.ACCEL_DUE_TO_GRAVITY;

        // Check left/right movement keys for X velocity
        boolean running = (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));

        // Idle/Walking/Running movement controls
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (running)
                this.velocity.x = Constants.HAWKE_RUN_SPEED;
            else
                this.velocity.x = Constants.HAWKE_WALK_SPEED;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (running)
                this.velocity.x = -Constants.HAWKE_RUN_SPEED;
            else
                this.velocity.x = -Constants.HAWKE_WALK_SPEED;
        } else {
            this.velocity.x = 0.0f;
        }

        // Set what the next animation state should be
        this.animationState = nextAnimationState();

        this.lastPosition.set(this.position.x, this.position.y);
    }

    /**
     * Logic to determine what the next animation state should be set
     * to, based on velocity and boolean flags grounded and flapping
     *
     * @return
     */
    private AnimationState nextAnimationState()
    {
        if (this.grounded)
        {
            if (Math.abs(this.velocity.x) > Constants.HAWKE_WALK_SPEED)
                return AnimationState.RUNNING;
            else if (Math.abs(this.velocity.x) > Constants.HAWKE_IDLE_SPEED_THRESHOLD)
                return AnimationState.WALKING;
            else
                return AnimationState.IDLE;
        }

        if (this.flapping)
            return AnimationState.FLAPPING;
        else
            return AnimationState.FALLING;
    }

    private boolean hasCollided(Platform platform)
    {
        // First off, check the Y values - that we JUST fell onto the platform
        float heightDiff = platform.height / 2.0f + Constants.HAWKE_RADIUS;

        // If we did NOT, return right away
        if (!this.grounded
            && !(this.lastPosition.y - platform.position.y > heightDiff
                && this.position.y - platform.position.y < heightDiff))
        {
            return false;
        }

        // Otherwise proceed to X checks

        // Check 1/2 of radius to the right of the platform
        if (this.position.x - (platform.position.x + platform.width / 2.0f) < Constants.HAWKE_RADIUS / 2.0f)
            return true;

        // Check on top of the platform
        if (this.position.x >= (platform.position.x - platform.width / 2.0f)
                && this.position.x <= (platform.position.x + platform.width / 2.0f))
            return true;

        // Check 1/2 of radius to the left of the platform
        if ((platform.position.x - platform.width / 2.0f) - this.position.x < Constants.HAWKE_RADIUS / 2.0f)
            return true;

        return false;
    }

    public void render(ShapeRenderer renderer)
    {
        renderer.setColor(Constants.HAWKE_COLOR);
        renderer.circle(this.position.x, this.position.y, Constants.HAWKE_RADIUS);

        batch.setProjectionMatrix(GameScreen.instance.getViewport().getCamera().combined);
        batch.begin();

        font.draw(
                batch,
                this.animationState.getLabel(),
                this.position.x,
                this.position.y,
                0,
                Align.center,
                false
        );

        batch.end();
    }

    public void setVelocity(float x, float y)
    {
        this.velocity.set(x, y);
    }
}
