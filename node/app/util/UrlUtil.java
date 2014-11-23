package util;

public class UrlUtil {

    public static String createHttpUrl(String ip, String port, String path) {
        return "http://" + ip + ":" + port +"/" + path;
    }

    public static String createHttpUrl(String ip, int port, String path) {
        return "http://" + ip + ":" + port +"/" + path;
    }
}
