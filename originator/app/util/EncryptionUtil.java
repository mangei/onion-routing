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

public class EncryptionUtil {

    public static String encryptMessage(String msg, Key k) throws InvalidKeyException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, BadPaddingException, IOException {
        Cipher encr = Cipher.getInstance("RSA/NONE/PKCS1Padding", "BC");
        encr.init(Cipher.ENCRYPT_MODE, k);

        byte[] encryptedData = encryptionHelper(msg.getBytes(),encr,220);
        return new String(Base64.encodeBase64(encryptedData));
    }

    public static String decryptMessage(String msg, Key k) throws InvalidKeyException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, BadPaddingException, IOException {
        Cipher decr = Cipher.getInstance("RSA/NONE/PKCS1Padding", "BC");
        decr.init(Cipher.DECRYPT_MODE, k);

        byte[] encryptedData = encryptionHelper(Base64.decodeBase64(msg.getBytes()),decr,256);
        return new String(encryptedData);
    }

    private static byte[] encryptionHelper(byte[] msgbytes,Cipher crypt,int blocksize) throws BadPaddingException, IllegalBlockSizeException, IOException {
        byte[] subarray, encryptedbytes;
        int start, end;
        double blockcount = Math.ceil(msgbytes.length / (blocksize*1.0));
        ByteArrayOutputStream data = new ByteArrayOutputStream();

        for (int i = 0; i < blockcount; i++) {
            start = i * blocksize;
            end = Math.min(msgbytes.length, (i + 1) * blocksize);
            subarray = Arrays.copyOfRange(msgbytes, start, end);
            encryptedbytes = crypt.doFinal(subarray);
            data.write(encryptedbytes);
        }
        return data.toByteArray();

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
