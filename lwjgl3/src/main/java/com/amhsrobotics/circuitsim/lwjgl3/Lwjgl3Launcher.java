package com.amhsrobotics.circuitsim.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.amhsrobotics.circuitsim.Main;

public class Lwjgl3Launcher {
	public static void main(String[] args) {
		createApplication();
	}

	private static Lwjgl3Application createApplication() {
		return new Lwjgl3Application(new Main(), getDefaultConfiguration());
	}

	private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
		Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
		configuration.setTitle("TKO Circuit Simulator");
		configuration.setWindowedMode(1366, 768);
		configuration.setResizable(false);
		configuration.setWindowIcon("logo.png");
		configuration.setBackBufferConfig(8, 8, 8, 8, 16, 0, 3);
		return configuration;
	}
}
