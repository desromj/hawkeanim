package com.greenbatgames.hawkeanim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
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

    Array<Platform> platforms;
    Hawke hawke;

    private Vector2 spawnPoint;

    private GameScreen()
    {
        init();
    }

    public void init()
    {
        // Base inits
        this.platforms = new Array<Platform>();
        this.viewport = new ExtendViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        this.renderer = new ShapeRenderer();
        this.spawnPoint = new Vector2(80.0f, 160.0f);
        this.hawke = new Hawke(spawnPoint);

        // Init Platforms
        platforms.add(new Platform(10.0f, 10.0f, 1600.0f, 80.0f));
        platforms.add(new Platform(800.0f, 420.0f, 540.0f, 40.0f));

        // Finalize
        Gdx.input.setInputProcessor(this);
    }



    @Override
    public void render(float delta)
    {
        // TODO: Updates go here
        hawke.update(delta, platforms);


        // TODO: Then rendering logic
        viewport.apply();
        renderer.setProjectionMatrix(viewport.getCamera().combined);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.begin(ShapeRenderer.ShapeType.Filled);

        // Render game background
        renderer.setColor(Constants.BG_COLOR);
        renderer.rect(0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Render platforms
        for (Platform platform: platforms)
            platform.render(renderer);

        // Render Hawke
        hawke.render(renderer);

        renderer.end();
    }

    @Override
    public void resize(int width, int height)
    {
        viewport.update(width, height, true);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
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
