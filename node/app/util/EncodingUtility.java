package util;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Mihai Lepadat
 *         Date: 11/2/14
 */
public class EncodingUtility {

    public static String encodeMessage(String message) {
        byte[] bytesEncoded = Base64.encodeBase64(message.getBytes());
        return new String(bytesEncoded);
    }

    public static String decodeMessage(String message) {
        byte[] valueDecoded= Base64.decodeBase64(message.getBytes());
        return new String(valueDecoded);
    }
}
