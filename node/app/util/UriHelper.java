package util;

public class UriHelper {

    public static String getUri(String ip, String port, String path) {
        return "http://" + ip + ":" + port +"/" + path;
    }

}
