package util;

import play.Application;
import play.GlobalSettings;
import play.Logger;

public class Global extends GlobalSettings {

    private static Config config;

    @Override
    public void onStart(Application app) {
        Logger.info("Application has started");

        // init configuration
        config = new Config();
    }

    public static Config getConfig() {
        return config;
    }

}
