package util;

import play.Configuration;
import play.Logger;
import play.Play;

public class Config {
    private String ip;
    private int port;
    private DirectoryConfig directoryConfig = new DirectoryConfig();
    private QuoteConfig quoteConfig = new QuoteConfig();

    public Config() {
        Configuration configuration = Play.application().configuration();
        setIp(configuration.getString("http.address"));
        setPort(configuration.getInt("http.port"));
        getDirectoryConfig().setIp(configuration.getString("directory.http.address"));
        getDirectoryConfig().setPort(configuration.getInt("directory.http.port"));
        getQuoteConfig().setIp(configuration.getString("quote.http.address"));
        getQuoteConfig().setPort(configuration.getInt("quote.http.port"));
        Logger.debug("Configuration: " + toString());
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

    public QuoteConfig getQuoteConfig() {
        return quoteConfig;
    }

    private void setQuoteConfig(QuoteConfig quoteConfig) {
        this.quoteConfig = quoteConfig;
    }

    @Override
    public String toString() {
        return "Config{" +
                "ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", directoryConfig=" + directoryConfig +
                ", quoteConfig=" + quoteConfig +
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

    public static class QuoteConfig {
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
            return "QuoteConfig{" +
                    "ip='" + ip + '\'' +
                    ", port='" + port + '\'' +
                    '}';
        }
    }
}
