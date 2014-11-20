package handlers;

import util.EncryptionHelper;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * @author Mihai Lepadat
 *         Date: 11/18/14
 */
public class KeyHandler {

    private String publicKey;
    private String privateKey;

    public KeyHandler() {
        KeyPair keyPair = generateKeyPair();
        publicKey = getPublicKey(keyPair);
        privateKey = getPrivateKey(keyPair);
    }

    private KeyPair generateKeyPair() {
        try {
            return EncryptionHelper.getRSAKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getPublicKey(KeyPair keyPair) {
        try {
            return EncryptionHelper.keyToString(keyPair.getPublic());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getPrivateKey(KeyPair keyPair) {
        try {
            return EncryptionHelper.keyToString(keyPair.getPrivate());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }
}
