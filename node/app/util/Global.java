package util;

import handlers.KeyHandler;
import play.Application;
import play.GlobalSettings;
import play.Logger;

/**
 * @author Mihai Lepadat
 *         Date: 11/18/14
 */
public class Global extends GlobalSettings {

    private static KeyHandler keyHandler;

    @Override
    public void onStart(Application app) {
        Logger.info("Application has started");

        keyHandler = new KeyHandler();
    }

    @Override
    public void onStop(Application app) {
        Logger.info("Application shutdown");
    }

    public static KeyHandler getKeyHandler() {
        return keyHandler;
    }
}
