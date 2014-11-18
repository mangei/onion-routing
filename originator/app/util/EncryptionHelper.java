package util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import play.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: thomasrieder
 * Date: 29.10.14
 * Time: 15:25
 */
public class EncryptionHelper {

    public static String encryptMessage(String msg, Key k) throws InvalidKeyException, IllegalBlockSizeException {
        int start, end;
        byte[] subarray,test;
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        Base64 base64=new Base64();
        try {
            byte[] msgbytes = msg.getBytes();
            Cipher encr = Cipher.getInstance("RSA/NONE/PKCS1Padding", "BC");
            encr.init(Cipher.ENCRYPT_MODE, k);
            for (int i = 0; i < msgbytes.length / 245.0; i++) {
                start = i * 245;
                end = ((i + 1) * 245) > msgbytes.length ? msgbytes.length : (i + 1) * 245;
                subarray = Arrays.copyOfRange(msgbytes, start, end);
                test = encr.doFinal(subarray);
                data.write(test);
            }
            return new String(base64.encode(data.toByteArray()));
        } catch (BadPaddingException e) {
            Logger.info(ExceptionUtils.getStackTrace(e));
            return null;
        } catch (NoSuchAlgorithmException e) {
            Logger.info(ExceptionUtils.getStackTrace(e));
            return null;
        } catch (NoSuchPaddingException e) {
            Logger.info(ExceptionUtils.getStackTrace(e));
            return null;
        } catch (NoSuchProviderException e) {
            Logger.info(ExceptionUtils.getStackTrace(e));
            return null;
        } catch (IOException e) {
            Logger.info(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static String decryptMessage(String msg, Key k) throws InvalidKeyException, IllegalBlockSizeException {
        Base64 base64=new Base64();
        byte[] decryptedData,encryptedBlock,msgbytes = base64.decode(msg);
        int start,end;
        StringBuilder ret = new StringBuilder();
        try {
            Cipher decr = Cipher.getInstance("RSA/NONE/PKCS1Padding", "BC");
            decr.init(Cipher.DECRYPT_MODE, k);
            for (int i = 0; i < msgbytes.length / 256.0; i++) {
                start = i * 256;
                end = ((i + 1) * 256) > msgbytes.length ? msgbytes.length : (i + 1) * 256;
                encryptedBlock = Arrays.copyOfRange(msgbytes, start, end);
                decryptedData = (decr.doFinal(encryptedBlock));
                ret.append(new String(decryptedData));
            }
            return ret.toString();
        } catch (NoSuchPaddingException e) {
            Logger.info(ExceptionUtils.getStackTrace(e));
            return null;
        } catch (NoSuchAlgorithmException e) {
            Logger.info(ExceptionUtils.getStackTrace(e));
            return null;
        } catch (BadPaddingException e) {
            Logger.info(ExceptionUtils.getStackTrace(e));
            return null;
        } catch (NoSuchProviderException e) {
            Logger.info(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static boolean isPublicKey(String publicKey) {

        Key key = stringToKey(publicKey);

        return key != null;
    }

    public static KeyPair getRSAKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {

        Security.addProvider(new BouncyCastleProvider());

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
