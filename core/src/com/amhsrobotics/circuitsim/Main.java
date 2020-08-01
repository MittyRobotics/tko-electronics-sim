package com.amhsrobotics.circuitsim;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

public class Main extends ApplicationAdapter {

	ModifiedShapeRenderer renderer;

	@Override
	public void create () {

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(46 / 255f, 52 / 255f, 64 / 255f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	}
	
	@Override
	public void dispose () {

	}
}
