package util;

import model.RegisterRequest;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: thomasrieder
 * Date: 29.10.14
 * Time: 15:18
 */
public class DirectoryHelper {

    private static SecureRandom random = new SecureRandom();

    private static final Set<String> sentSecrets;

    static {
        sentSecrets = new HashSet<String>();
    }


    public static boolean verifyRegisterRequest(RegisterRequest request) {

        String publicKey = request.getPublicKey();
        String ip = request.getIp();
        Integer port = request.getPort();

        if (publicKey == null || ip == null || port == null) {
            return false;
        }

        if (port <= 0 || port >= 65536) {
            return false;
        }

        if (!InetAddressValidator.getInstance().isValidInet4Address(ip)) {
            return false;
        }

        if (!EncryptionHelper.isPublicKey(publicKey)) {
            return false;
        }

        return true;
    }

    public static String nextSessionId() {
        String id = new BigInteger(130, random).toString(32);

        while (sentSecrets.contains(id)) {
            id = new BigInteger(130, random).toString(32);
        }

        sentSecrets.add(id);

        return id;
    }

}
