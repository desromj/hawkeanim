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

/**
 * Hawke is the main player character of the game
 */
public class Hawke
{
    AnimationState animationState;
    Vector2 position;
    Vector2 spawnPosition;
    private Vector2 lastPosition;

    boolean grounded, flapping, gliding, carrying;
    float cannotFlapFor, disableCollisionFor;

    SpriteBatch batch;
    BitmapFont font;

    Body body;
    PhysicalObject carried;
    float contactDist;

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
        this.carrying = false;
        this.cannotFlapFor = 0.0f;
        this.disableCollisionFor = 0.0f;
        this.contactDist = 0f;

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
        this.carried = null;

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
        shape.set(Constants.HAWKE_VERTICIES);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = Constants.HAWKE_DENSITY;
        fixtureDef.restitution = 0f;
        fixtureDef.friction = 0f;

        body.createFixture(fixtureDef);
        body.setUserData(this);

        shape.dispose();
    }

    public void update(float delta)
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

        if (this.cannotFlapFor <= 0.0f && !this.grounded)
            this.flapping = false;

        this.gliding = !this.grounded && !this.flapping && Gdx.input.isKeyPressed(Input.Keys.Z);

        // Idle/Walking/Running movement controls
        this.move();

        // Cling horizontal velocity of grabbed objects if they are not at rest
        if (!Gdx.input.isKeyPressed(Input.Keys.X)) {
            if (this.carried != null
                    && Math.abs(this.body.getPosition().dst(this.carried.getBody().getPosition())) > this.contactDist + Constants.WOBBLE_ROOM)
            {
                this.dropCarriedObject();
            }
        }

        if (this.carrying && this.carried != null && !this.carried.isAtRest()) {
            carried.getBody().setLinearVelocity(
                    this.body.getLinearVelocity().x,
                    this.body.getLinearVelocity().y
            );
        }

        // Tweak the body while holding down so it does not come to rest
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            this.body.setAwake(true);

        // Set what the next animation state should be
        this.animationState = nextAnimationState();

        this.lastPosition.set(this.position.x, this.position.y);
    }

    private void move()
    {
        // Check left/right movement keys for X velocity
        boolean running = (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));

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
    }

    /**
     * Logic to determine what the next animation state should be set
     * to, based on velocity and boolean flags grounded and flapping
     *
     * @return The Animation State this object should be in, based off its movement boolean flags
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

    public void land()
    {
        this.grounded = true;
        this.flapping = false;
        this.gliding = false;
        this.cannotFlapFor = 0.0f;
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

            // Cancel out the effect of gravity
            this.body.setLinearVelocity(
                    this.body.getLinearVelocity().x,
                    0f
            );

            // apply the flapping force upward
            if (carrying && carried != null) {

                // Cancel gravity effect of carried object
                carried.getBody().setLinearVelocity(
                        carried.getBody().getLinearVelocity().x,
                        0f
                );

                float hawkeRatio = Utils.getMassRatio(this.body, carried.getBody(), true);
                float carriedRatio = Utils.getMassRatio(this.body, carried.getBody(), false);

                this.body.applyForceToCenter(0f, Constants.HAWKE_JUMP_IMPULSE * hawkeRatio, true);
                carried.getBody().applyForceToCenter(0f, Constants.HAWKE_JUMP_IMPULSE * carriedRatio, true);

            } else {
                this.body.applyForceToCenter(0f, Constants.HAWKE_JUMP_IMPULSE, true);
            }
        }
    }

    public void carryObject(PhysicalObject object)
    {
        this.carrying = true;
        this.carried = object;
        this.contactDist = Math.abs(this.body.getPosition().dst(object.getBody().getPosition()));
    }

    public void dropCarriedObject()
    {
        this.carrying = false;
        this.carried = null;
        this.contactDist = 0f;
    }

    public boolean isCarrying() { return this.carrying; }
    public Vector2 getPosition()
    {
        return this.position;
    }
    public float getFootYHeight() { return this.position.y - Constants.HAWKE_RADIUS * 2.0f; }
    public boolean collisionDisabled() { return this.disableCollisionFor > 0f; }
}
