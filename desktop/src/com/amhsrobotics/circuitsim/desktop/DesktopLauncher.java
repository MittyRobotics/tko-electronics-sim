package com.amhsrobotics.circuitsim.desktop;

import com.amhsrobotics.circuitsim.Main;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "TKO 1351 Circuit Simulator";
		config.resizable = false;
		config.width = 1366;
		config.height = 768;
		config.forceExit = false;
//		config.setTitle("TKO 1351 Circuit Simulator");
//		config.setResizable(false);
//		config.setHdpiMode(HdpiMode.Logical);
//		config.setWindowedMode(1366, 768);

		new LwjglApplication(new Main(), config);
	}
}