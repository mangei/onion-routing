package util;

import model.RegisterRequest;
import org.apache.commons.validator.routines.InetAddressValidator;

import play.Logger;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class DirectoryUtil {

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
            Logger.debug("publicKey/ip/port null");
            return false;
        }

        if (port <= 0 || port >= 65536) {
            Logger.debug("invalid port: " + port);
            return false;
        }

        if (!InetAddressValidator.getInstance().isValidInet4Address(ip)) {
            Logger.debug("invalid ipv4 address: " + ip);
            return false;
        }

        if (!EncryptionUtil.isPublicKey(publicKey)) {
            Logger.debug("invalid public key: " + publicKey);
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
