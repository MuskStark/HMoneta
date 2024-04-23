package fan.summer.hmoneta.util;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class EncryptionUtil {

    // 生成密钥对
    public static  Map<String,String> generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        Map<String,String> keys = new HashMap<>();
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        byte[] pubKeyBytes = keyPair.getPublic().getEncoded();
        String base64PublicKey = Base64.getEncoder().encodeToString(pubKeyBytes);

        byte[] priKeyBytes = keyPair.getPrivate().getEncoded();
        String base64PrivateKey = Base64.getEncoder().encodeToString(priKeyBytes);
        keys.put("publicKey", base64PublicKey);
        keys.put("privateKey", base64PrivateKey);
        return keys;
    }

    // 使用公钥加密数据
    public static String encrypt(PublicKey publicKey, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data));
    }

    public static String encrypt(String base64PublicKey, byte[] data) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data));
    }

    // 使用私钥解密数据
    public static byte[] decrypt(PrivateKey privateKey, String base64dData) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(Base64.getDecoder().decode(base64dData));
    }

    public static byte[] decrypt(String base64PrivateKey, String base64dData) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(Base64.getDecoder().decode(base64dData));
    }

}
