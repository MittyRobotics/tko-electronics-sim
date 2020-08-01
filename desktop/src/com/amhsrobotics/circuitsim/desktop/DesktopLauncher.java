package com.amhsrobotics.circuitsim.desktop;

import com.amhsrobotics.circuitsim.Main;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.HdpiMode;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		config.setTitle("TKO 1351 Circuit Simulator");
		config.setResizable(false);
		config.setHdpiMode(HdpiMode.Logical);
		config.setWindowedMode(1366, 768);

		new Lwjgl3Application(new Main(), config);
	}
}