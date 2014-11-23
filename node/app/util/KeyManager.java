package util;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class KeyManager {

    private String publicKey;
    private String privateKey;

    public KeyManager() {
        KeyPair keyPair = generateKeyPair();
        publicKey = getPublicKey(keyPair);
        privateKey = getPrivateKey(keyPair);
    }

    private KeyPair generateKeyPair() {
        try {
            return EncryptionUtil.getRSAKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getPublicKey(KeyPair keyPair) {
        try {
            return EncryptionUtil.keyToString(keyPair.getPublic());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getPrivateKey(KeyPair keyPair) {
        try {
            return EncryptionUtil.keyToString(keyPair.getPrivate());
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
