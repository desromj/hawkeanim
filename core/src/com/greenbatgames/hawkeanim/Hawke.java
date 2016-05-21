package com.greenbatgames.hawkeanim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    Vector2 spawnPosition;
    private Vector2 lastPosition;

    boolean grounded, flapping, gliding;
    float cannotFlapFor;

    SpriteBatch batch;
    BitmapFont font;

    public Hawke(Vector2 position)
    {
        this(position.x, position.y);
    }

    public Hawke(float x, float y)
    {
        this.spawnPosition = new Vector2(x, y);

        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.font.setColor(Constants.HAWKE_TEXT_COLOR);
        this.font.getData().setScale(Constants.HAWKE_TEXT_SCALE);

        init();
    }

    public void init()
    {
        this.position = new Vector2(this.spawnPosition.x, this.spawnPosition.y);
        this.lastPosition = new Vector2(this.spawnPosition.x, this.spawnPosition.y);
        this.velocity = new Vector2();
        this.animationState = AnimationState.FALLING;
        this.grounded = false;
        this.flapping = false;
        this.gliding = false;
        this.cannotFlapFor = 0.0f;
    }

    public void update(float delta, Array<Platform> platforms)
    {
        if (this.gliding)
            this.velocity.y = Constants.GRAVITY * 2.5f;
        else
            this.velocity.y += Constants.GRAVITY;

        this.position.mulAdd(this.velocity, delta);
        this.cannotFlapFor -= delta;

        if (this.position.y < Constants.KILL_PLANE)
            init();

        // Platform collision logic
        for (Platform platform: platforms)
        {
            if (this.hasCollided(platform))
            {
                this.grounded = true;
                this.flapping = false;
                this.gliding = false;
                this.cannotFlapFor = 0.0f;

                // Constrain player to the top of the platform
                this.position.y = platform.top + Constants.HAWKE_RADIUS;
                this.velocity.y = 0.0f;
                break;
            }
        }

        if (this.cannotFlapFor <= 0.0f && !this.grounded)
            this.flapping = false;

        this.gliding = !this.grounded && !this.flapping && Gdx.input.isKeyPressed(Input.Keys.Z);

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
            if (this.grounded)
                this.velocity.x = 0.0f;
            else {
                if (!this.gliding)
                    this.velocity.x *= Constants.HORIZONTAL_FALL_DAMPEN;
            }
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

        if (this.flapping) {
            return AnimationState.FLAPPING;
        }
        else {
            if (Gdx.input.isKeyPressed(Input.Keys.Z))
                return AnimationState.GLIDING;
            return AnimationState.FALLING;
        }
    }

    private boolean hasCollided(Platform platform)
    {
        boolean left = false, right = false, middle = false;

        if (this.lastPosition.y - Constants.HAWKE_RADIUS >= platform.top
                && this.position.y - Constants.HAWKE_RADIUS < platform.top)
        {
            float edgeLeeway = Constants.HAWKE_RADIUS / 2.0f;
            float leftFoot = this.position.x - edgeLeeway;
            float rightFoot = this.position.x + edgeLeeway;

            left = (platform.left < leftFoot && platform.right > leftFoot);
            right = (platform.left < rightFoot && platform.right > rightFoot);
            middle = (platform.left > leftFoot && platform.right < rightFoot);
        }

        return left || right || middle;
    }

    public void renderShapes(ShapeRenderer renderer)
    {
        renderer.setColor(Constants.HAWKE_COLOR);
        renderer.circle(this.position.x, this.position.y, Constants.HAWKE_RADIUS);
    }

    public void renderSprites(SpriteBatch batch)
    {
        font.setColor(Constants.HAWKE_TEXT_COLOR);
        font.draw(
                batch,
                this.animationState.getLabel(),
                this.position.x,
                this.position.y + Constants.HAWKE_RADIUS / 4.0f,
                0,
                Align.center,
                false
        );
    }

    public void flap()
    {
        if (this.cannotFlapFor <= 0.0f)
        {
            this.grounded = false;
            this.flapping = true;
            this.velocity.y = Constants.HAWKE_JUMP_IMPULSE;
            this.cannotFlapFor = Constants.HAWKE_DELAY_BETWEEN_FLAPS;
        }
    }
}
