package com.greenbatgames.hawkeanim;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HawkeGame extends Game
{
	@Override
	public void create ()
	{
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
		setScreen(GameScreen.instance);
	}
}
