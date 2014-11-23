package util;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.Logger;

import java.io.*;
import java.security.*;

/**
 * Created with IntelliJ IDEA.
 * User: thomasrieder
 * Date: 29.10.14
 * Time: 15:25
 */
public class EncryptionUtil {

    public static boolean isPublicKey(String publicKey) {

        Key key = stringToKey(publicKey);

        return key != null;
    }

    public static KeyPair getRSAKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Create the public and private keys
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        generator.initialize(2048, random);
        KeyPair pair = generator.generateKeyPair();

        return pair;
    }

    public static String keyToString(Key key) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        Base64 base64 = new Base64();

        os.writeObject(key);
        return base64.encodeAsString(out.toByteArray());
    }

    public static Key stringToKey(String keyString) {
        Base64 base64 = new Base64();
        ByteArrayInputStream bais = new ByteArrayInputStream(base64.decode(keyString));
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(bais);
            Object obj = in.readObject();

            if (obj instanceof Key) {
                return (Key) obj;
            } else {
                return null;
            }
        } catch (IOException e) {
            Logger.info(ExceptionUtils.getStackTrace(e));
            return null;

        } catch (ClassNotFoundException e) {
            Logger.info(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
}
