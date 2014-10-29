package util;


import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.security.*;

/**
 * Created with IntelliJ IDEA.
 * User: thomasrieder
 * Date: 29.10.14
 * Time: 15:25
 */
public class EncryptionHelper {

    public static boolean isPublicKey(String publicKey) {

        // TODO implement

        return true;
    }

    public static KeyPair getRSAKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Create the public and private keys
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        generator.initialize(1024, random);
        KeyPair pair = generator.generateKeyPair();

        return pair;
    }

    public static String keyToString(Key key) throws IOException {
        Base64 base64 = new Base64();
        return base64.encodeAsString(key.getEncoded());
    }

    public static Key StringToKey(String keyString) {
        Base64 base64 = new Base64();

        // TODO create key object from base64 string

        return null;
    }
}
