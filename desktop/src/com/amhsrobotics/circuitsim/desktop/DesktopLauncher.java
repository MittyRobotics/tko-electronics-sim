package com.amhsrobotics.circuitsim.desktop;

import com.amhsrobotics.circuitsim.Main;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.apache.commons.lang3.SystemUtils;

public class DesktopLauncher {
	public static void main (String[] arg) {

		if(SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) {
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

			config.title = "TKO 1351 Circuit Simulator";
			config.resizable = false;
			config.width = 1366;
			config.height = 768;

			new LwjglApplication(new Main(), config);
		} else {
			System.out.println("Incompatible Operating System. If you aren't using Windows, Linux, or Mac, wtf are you using?");
		}
	}
}