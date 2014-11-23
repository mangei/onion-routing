package util;

import play.Configuration;
import play.Logger;
import play.Play;

public class Config {
    private String ip;
    private int port;
    private DirectoryConfig directoryConfig = new DirectoryConfig();

    public Config() {
        Config config = new Config();
        Configuration configuration = Play.application().configuration();
        config.setIp(configuration.getString("http.address"));
        config.setPort(configuration.getInt("http.port"));
        config.getDirectoryConfig().setIp(configuration.getString("directory.http.port"));
        config.getDirectoryConfig().setPort(configuration.getInt("directory.http.port"));
        Logger.debug("Configuration: " + config);
    }

    public String getIp() {
        return ip;
    }

    private void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    private void setPort(int port) {
        this.port = port;
    }

    public DirectoryConfig getDirectoryConfig() {
        return directoryConfig;
    }

    private void setDirectoryConfig(DirectoryConfig directoryConfig) {
        this.directoryConfig = directoryConfig;
    }

    @Override
    public String toString() {
        return "Config{" +
                "ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", directoryConfig=" + directoryConfig +
                '}';
    }

    public static class DirectoryConfig {
        private String ip;
        private int port;

        public String getIp() {
            return ip;
        }

        private void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        private void setPort(int port) {
            this.port = port;
        }

        @Override
        public String toString() {
            return "DirectoryConfig{" +
                    "ip='" + ip + '\'' +
                    ", port='" + port + '\'' +
                    '}';
        }
    }
}
