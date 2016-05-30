package com.greenbatgames.hawkeanim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Quiv on 2016-05-18.
 */
public class Hawke
{
    AnimationState animationState;
    Vector2 position;
    Vector2 spawnPosition;
    private Vector2 lastPosition;

    boolean grounded, flapping, gliding;
    float cannotFlapFor, disableCollisionFor;

    SpriteBatch batch;
    BitmapFont font;

    Body body;

    public Hawke(Vector2 position, World world)
    {
        this(position.x, position.y, world);
    }

    public Hawke(float x, float y, World world)
    {
        this.spawnPosition = new Vector2(x, y);

        this.position = new Vector2(this.spawnPosition.x, this.spawnPosition.y);
        this.lastPosition = new Vector2(this.spawnPosition.x, this.spawnPosition.y);
        this.animationState = AnimationState.FALLING;
        this.grounded = false;
        this.flapping = false;
        this.gliding = false;
        this.cannotFlapFor = 0.0f;
        this.disableCollisionFor = 0.0f;

        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.font.setColor(Constants.HAWKE_TEXT_COLOR);
        this.font.getData().setScale(Constants.HAWKE_TEXT_SCALE);

        initPhysics(world);
        init();
    }

    public void init()
    {
        this.body.setTransform(
                this.spawnPosition.x / Constants.PTM,
                (this.spawnPosition.y) / Constants.PTM,
                0.0f
        );
        this.body.setLinearVelocity(0f, 0f);

        this.position.set(this.spawnPosition.x, this.spawnPosition.y);
        this.lastPosition.set(this.spawnPosition.x, this.spawnPosition.y);

        this.animationState = AnimationState.FALLING;
        this.grounded = false;
        this.flapping = false;
        this.gliding = false;
        this.cannotFlapFor = 0.0f;
        this.disableCollisionFor = 0.0f;
    }

    private void initPhysics(World world)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(
                this.position.x / Constants.PTM,
                (this.position.y + Constants.HAWKE_RADIUS) / Constants.PTM
        );
        bodyDef.fixedRotation = true;

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
                Constants.PLATFORM_EDGE_LEEWAY / Constants.PTM,
                (Constants.HAWKE_RADIUS * 2.0f) / Constants.PTM
        );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0f;
        fixtureDef.friction = 0f;

        fixtureDef.filter.groupIndex = Constants.PLAYER_GROUP;

        body.createFixture(fixtureDef);
        body.setUserData(this);

        shape.dispose();
    }

    public void update(float delta, Array<Platform> platforms)
    {
        // First cling position to the physics body
        this.position.set(
                this.body.getPosition().x * Constants.PTM,
                this.body.getPosition().y * Constants.PTM
        );

        // Proceed to rest of 'regular' updates
        if (this.gliding)
            this.body.applyForceToCenter(Constants.GLIDE_DRAG_FORCE, true);

        this.cannotFlapFor -= delta;
        this.disableCollisionFor -= delta;

        if (this.position.y < Constants.KILL_PLANE)
            init();

        boolean onPlatform = false;

        // Platform collision logic
        for (Platform platform: platforms)
        {
            if (this.hasLanded(platform))
            {
                onPlatform = true;

                this.grounded = true;
                this.flapping = false;
                this.gliding = false;
                this.cannotFlapFor = 0.0f;

                break;
            }
        }

        if (!onPlatform)
            this.grounded = false;

        if (this.cannotFlapFor <= 0.0f && !this.grounded)
            this.flapping = false;

        this.gliding = !this.grounded && !this.flapping && Gdx.input.isKeyPressed(Input.Keys.Z);

        // Check left/right movement keys for X velocity
        boolean running = (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));

        // Idle/Walking/Running movement controls
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (gliding)
            {
                float vel = this.body.getLinearVelocity().x * Constants.PTM;

                if (running) {
                    vel += Constants.HAWKE_GLIDE_RUN_SPEED;
                } else {
                    vel += Constants.HAWKE_GLIDE_WALK_SPEED;
                }

                vel = MathUtils.clamp(
                        vel,
                        -Constants.HAWKE_MAX_GLIDE_SPEED,
                        Constants.HAWKE_MAX_GLIDE_SPEED);

                this.body.setLinearVelocity(
                        vel / Constants.PTM,
                        Constants.GLIDE_CONSTANT_GRAVITY / Constants.PTM
                );
            } else {
                if (running) {
                    this.body.setLinearVelocity(
                            Constants.HAWKE_RUN_SPEED / Constants.PTM,
                            this.body.getLinearVelocity().y
                    );
                } else {
                    this.body.setLinearVelocity(
                            Constants.HAWKE_WALK_SPEED / Constants.PTM,
                            this.body.getLinearVelocity().y
                    );
                }
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (gliding)
            {
                float vel = this.body.getLinearVelocity().x * Constants.PTM;

                if (running) {
                    vel -= Constants.HAWKE_GLIDE_RUN_SPEED;
                } else {
                    vel -= Constants.HAWKE_GLIDE_WALK_SPEED;
                }

                vel = MathUtils.clamp(
                        vel,
                        -Constants.HAWKE_MAX_GLIDE_SPEED,
                        Constants.HAWKE_MAX_GLIDE_SPEED);

                this.body.setLinearVelocity(
                        vel / Constants.PTM,
                        Constants.GLIDE_CONSTANT_GRAVITY / Constants.PTM
                );

            } else {
                if (running) {
                    this.body.setLinearVelocity(
                            -Constants.HAWKE_RUN_SPEED / Constants.PTM,
                            this.body.getLinearVelocity().y
                    );
                } else {
                    this.body.setLinearVelocity(
                            -Constants.HAWKE_WALK_SPEED / Constants.PTM,
                            this.body.getLinearVelocity().y
                    );
                }
            }
        } else {
            if (!this.grounded)
            {
                if (this.gliding) {
                    this.body.setLinearVelocity(
                            this.body.getLinearVelocity().x * Constants.HORIZONTAL_GLIDE_DAMPEN,
                            Constants.GLIDE_CONSTANT_GRAVITY / Constants.PTM
                    );
                } else {
                    this.body.setLinearVelocity(
                            this.body.getLinearVelocity().x * Constants.HORIZONTAL_FALL_DAMPEN,
                            this.body.getLinearVelocity().y
                    );
                }
            } else {
                this.body.setLinearVelocity(
                        this.body.getLinearVelocity().x * Constants.HORIZONTAL_WALK_DAMPEN,
                        this.body.getLinearVelocity().y
                );
            }
        }

        // Tweak the body while holding down so it does not come to rest
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            this.body.setAwake(true);

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
            if (Math.abs(this.body.getLinearVelocity().x * Constants.PTM) > Constants.HAWKE_WALK_SPEED)
                return AnimationState.RUNNING;
            else if (Math.abs(this.body.getLinearVelocity().x * Constants.PTM) > Constants.HAWKE_IDLE_SPEED_THRESHOLD)
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

    private boolean hasLanded(Platform platform)
    {
        if (flapping)
            return false;

        boolean left = false, right = false, middle = false;

        if (Utils.almostEqualTo(
                this.body.getPosition().y * Constants.PTM - Constants.HAWKE_RADIUS * 2.0f,
                platform.top,
                Constants.PLATFORM_COLLISION_LEEWAY))
        {
            float leftFoot = this.position.x - Constants.PLATFORM_EDGE_LEEWAY;
            float rightFoot = this.position.x + Constants.PLATFORM_EDGE_LEEWAY;

            left = (platform.left < leftFoot && platform.right > leftFoot);
            right = (platform.left < rightFoot && platform.right > rightFoot);
            middle = (platform.left > leftFoot && platform.right < rightFoot);
        }

        return left || right || middle;
    }

    public void renderShapes(ShapeRenderer renderer)
    {
        renderer.setColor(Constants.HAWKE_COLOR);

        // bottom circle of the capsule
        renderer.circle(
                this.position.x,
                this.position.y - Constants.HAWKE_RADIUS,
                Constants.HAWKE_RADIUS);

        // top circle of the capsule
        renderer.circle(
                this.position.x,
                this.position.y + Constants.HAWKE_RADIUS,
                Constants.HAWKE_RADIUS);

        // rectangle connecting the two circles into a capsule
        renderer.rect(
                this.position.x - Constants.HAWKE_RADIUS,
                this.position.y - Constants.HAWKE_RADIUS,
                2.0f * Constants.HAWKE_RADIUS,
                2.0f * Constants.HAWKE_RADIUS
        );
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
        if (this.grounded && Gdx.input.isKeyPressed(Input.Keys.DOWN))
        {
            this.disableCollisionFor = Constants.DISABLE_COLLISION_FOR_PLATFORM;
            return;
        }

        if (this.cannotFlapFor <= 0.0f)
        {
            this.grounded = false;
            this.flapping = true;
            this.cannotFlapFor = Constants.HAWKE_DELAY_BETWEEN_FLAPS;

            this.body.setLinearVelocity(
                    this.body.getLinearVelocity().x,
                    0f
            );
            this.body.applyForceToCenter(0f, Constants.HAWKE_JUMP_IMPULSE, true);
        }
    }

    public Vector2 getPosition()
    {
        return this.position;
    }
    public float getFootYPosition() { return this.position.y - Constants.HAWKE_RADIUS * 2.0f; }
    public boolean collisionDisabled() { return this.disableCollisionFor > 0f; }
}
