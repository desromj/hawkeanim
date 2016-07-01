package com.greenbatgames.hawkeanim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Quiv on 2016-05-18.
 */
public class GameScreen extends ScreenAdapter implements InputProcessor
{
    public static final GameScreen instance = new GameScreen();

    Viewport viewport;
    ShapeRenderer renderer;
    SpriteBatch batch;
    ChaseCam chaseCam;

    Array<Platform> platforms;
    Array<Box> boxes;
    Hawke hawke;

    World world;
    Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;

    private Vector2 spawnPoint;

    private GameScreen()
    {
        init();
    }

    public void init()
    {
        // Init physics world: with gravity
        world = new World(new Vector2(0, Constants.GRAVITY), true);
        debugRenderer = new Box2DDebugRenderer();

        // Base inits
        this.platforms = new Array<Platform>();
        this.boxes = new Array<Box>();
        this.viewport = new ExtendViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        this.renderer = new ShapeRenderer();
        this.batch = new SpriteBatch();
        this.spawnPoint = new Vector2(80.0f, 240.0f);

        // World dependent objects
        this.hawke = new Hawke(spawnPoint, world);
        this.chaseCam = new ChaseCam(viewport.getCamera(), this.hawke);

        // Init Platforms
        platforms.add(new Platform(20.0f, 20.0f, 1600.0f, 80.0f, world, false));
        platforms.add(new Platform(800.0f, 420.0f, 540.0f, 25.0f, world, true));

        // Init boxes
        boxes.add(new MediumBox(870.0f, 500.0f, world));
        boxes.add(new SmallBox(400.0f, 60.0f, world));

        // Finalize
        Gdx.input.setInputProcessor(this);

        world.setContactListener(new HawkeContactListener());
    }



    @Override
    public void render(float delta)
    {
        // Update the physics engine with all the bodies
        world.step(
                Constants.PHYSICS_STEP_FREQ,
                Constants.PHYSICS_VEL_ITERATIONS,
                Constants.PHYSICS_POS_ITERATIONS
        );

        // TODO: Updates go here
        hawke.update(delta);
        chaseCam.update(delta);

        // Render boxes
        for (Box box: boxes)
            box.update(delta);



        // TODO: Then rendering logic
        viewport.apply();
        renderer.setProjectionMatrix(viewport.getCamera().combined);

        // Scale the debug Matrix to box2d sizes
        debugMatrix = viewport.getCamera().combined.cpy().scale(
                Constants.PTM,
                Constants.PTM,
                0
        );

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.begin(ShapeRenderer.ShapeType.Filled);

        // Render game background
        renderer.setColor(Constants.BG_COLOR);
        renderer.rect(0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Render platforms
        for (Platform platform: platforms)
            platform.render(renderer);

        // Render boxes
        for (Box box: boxes)
            box.render(renderer);

        // Render Hawke
        hawke.renderShapes(renderer);

        renderer.end();

        // Render Sprites
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        hawke.renderSprites(batch);

        batch.end();

        // Render the debug physics engine settings
        debugRenderer.render(world, debugMatrix);
    }

    @Override
    public void resize(int width, int height)
    {
        viewport.update(width, height, true);
    }

    @Override
    public boolean keyDown(int keycode)
    {
        if (keycode == Input.Keys.Z)
            hawke.flap();

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    /*
    Getters and Setters
     */

    public Viewport getViewport()
    {
        return viewport;
    }
}
