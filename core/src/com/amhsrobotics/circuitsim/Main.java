package com.amhsrobotics.circuitsim;

import com.amhsrobotics.circuitsim.screens.MenuScreen;
import com.amhsrobotics.circuitsim.utility.LinkTimer;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import java.io.*;

public class Main extends Game {

	private Game game;

	@Override
	public void create () {

		loadConstants();

		game = this;

		setScreen(new MenuScreen(game));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(46 / 255f, 52 / 255f, 64 / 255f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		super.render();
		LinkTimer.tick();
	}
	
	@Override
	public void dispose () {
		Gdx.app.log("Closing", "");
		saveConstants();
	}

	public void loadConstants() {
		try {
			FileReader fr = new FileReader(Gdx.files.internal("usersettings.txt").file());
			BufferedReader br = new BufferedReader(fr);

			Constants.ATLAS_STR = br.readLine();
			Constants.ATLAS_ALTERNATE_STR = br.readLine();

			Constants.ATLAS = new TextureAtlas(Gdx.files.internal(Constants.ATLAS_STR ));
			Constants.ATLAS_ALTERNATE = new TextureAtlas(Gdx.files.internal(Constants.ATLAS_ALTERNATE_STR));

			Constants.reloadAssets();

			Constants.CURRENT_COLOR = br.readLine();
			Constants.WORLD_DIM = new Vector2(Float.parseFloat(br.readLine()), Float.parseFloat(br.readLine()));
			Constants.GRID_SIZE = Integer.parseInt(br.readLine());

			br.close();

		} catch (Exception e) {
			Gdx.app.log("Error loading constants", "");
		}
	}

	public void saveConstants() {
		try {
			FileWriter fw = new FileWriter(Gdx.files.internal("usersettings.txt").file());
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(Constants.ATLAS_STR + "\n" +
					Constants.ATLAS_ALTERNATE_STR + "\n" +
					Constants.CURRENT_COLOR + "\n" +
					Constants.WORLD_DIM.x + "\n" +
					Constants.WORLD_DIM.y + "\n" +
					Constants.GRID_SIZE);

			bw.close();

		} catch (Exception e) {
			Gdx.app.log("Error saving constants", "");
		}
	}
}
