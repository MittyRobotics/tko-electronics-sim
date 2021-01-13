package com.amhsrobotics.circuitsim;

import com.amhsrobotics.circuitsim.screens.MenuScreen;
import com.amhsrobotics.circuitsim.utility.LinkTimer;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import net.spookygames.gdx.nativefilechooser.NativeFileChooser;

public class Main extends Game {

	private final NativeFileChooser chooser;
	public Main(NativeFileChooser fileChooser) {
		super();
		this.chooser = fileChooser;
	}

	@Override
	public void create () {
		setScreen(new MenuScreen(this, chooser));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(46 / 255f, 52 / 255f, 64 / 255f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);


		super.render();
		LinkTimer.tick();
	}
	
	@Override
	public void dispose () {
		Constants.disposeAll();
	}
}
