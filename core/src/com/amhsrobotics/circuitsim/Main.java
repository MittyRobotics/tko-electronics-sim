package com.amhsrobotics.circuitsim;

import com.amhsrobotics.circuitsim.screens.MenuScreen;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

public class Main extends Game {

	private Game game;

	@Override
	public void create () {

		game = this;

		setScreen(new MenuScreen(game));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(46 / 255f, 52 / 255f, 64 / 255f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		super.render();
	}
	
	@Override
	public void dispose () {

	}
}
