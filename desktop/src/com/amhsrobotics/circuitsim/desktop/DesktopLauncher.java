package com.amhsrobotics.circuitsim.desktop;

import com.amhsrobotics.circuitsim.Main;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.spookygames.gdx.nativefilechooser.desktop.DesktopFileChooser;
import org.apache.commons.lang3.SystemUtils;

public class DesktopLauncher {
	public static void main (String[] arg) {

		if(SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_UNIX || SystemUtils.IS_OS_MAC) {
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

			config.title = "TKO 1351 Circuit Simulator";
			config.addIcon("img/logo/logo.png", Files.FileType.Internal);
			config.resizable = false;
//			config.fullscreen = true;
			config.width = 1366;
			config.height = 768;

			config.samples = 3;

			new LwjglApplication(new Main(new DesktopFileChooser()), config);
		} else {
			System.out.println("Incompatible Operating System. \nIf you aren't using Windows, Unix, or OSX, wtf are you using?");
		}
	}
}