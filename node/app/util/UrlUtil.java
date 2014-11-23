package util;

public class UrlUtil {

    public static String createHttpUrl(String ip, String port, String path) {
        return "http://" + ip + ":" + port +"/" + path;
    }
}
