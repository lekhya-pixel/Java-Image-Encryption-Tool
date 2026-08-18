import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

public class CryptoUtils {

    // =========================================================
    // KEY GENERATION
    // =========================================================

    /*
     * SHA-256 converts the user's password into 32 bytes.
     * These bytes are then used to create keys of the
     * required size for each algorithm.
     */
    private static byte[] sha256(String password) throws Exception {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        return digest.digest(
                password.getBytes(StandardCharsets.UTF_8)
        );
    }


    // =========================================================
    // AES KEY
    // =========================================================

    private static byte[] getAESKey(String password, int keySize)
            throws Exception {

        byte[] hash = sha256(password);

        return Arrays.copyOf(hash, keySize / 8);
    }


    // =========================================================
    // DES KEY
    // =========================================================

    private static byte[] getDESKey(String password)
            throws Exception {

        byte[] hash = sha256(password);

        return Arrays.copyOf(hash, 8);
    }


    // =========================================================
    // 2DES KEYS
    // =========================================================

    private static SecretKeySpec[] get2DESKeys(String password)
            throws Exception {

        byte[] hash = sha256(password);

        byte[] keyMaterial = Arrays.copyOf(hash, 16);

        byte[] key1Bytes =
                Arrays.copyOfRange(keyMaterial, 0, 8);

        byte[] key2Bytes =
                Arrays.copyOfRange(keyMaterial, 8, 16);

        SecretKeySpec key1 =
                new SecretKeySpec(key1Bytes, "DES");

        SecretKeySpec key2 =
                new SecretKeySpec(key2Bytes, "DES");

        return new SecretKeySpec[]{key1, key2};
    }


    // =========================================================
    // 3DES KEY
    // =========================================================

    private static byte[] get3DESKey(String password)
            throws Exception {

        byte[] hash = sha256(password);

        return Arrays.copyOf(hash, 24);
    }


    // =========================================================
    // BLOWFISH KEY
    // =========================================================

    private static byte[] getBlowfishKey(String password)
            throws Exception {

        byte[] hash = sha256(password);

        return Arrays.copyOf(hash, 16);
    }


    // =========================================================
    // AES
    // =========================================================

    public static byte[] encryptAES(
            byte[] data,
            String password,
            int keySize) throws Exception {

        byte[] keyBytes =
                getAESKey(password, keySize);

        SecretKeySpec secretKey =
                new SecretKeySpec(keyBytes, "AES");

        Cipher cipher =
                Cipher.getInstance("AES/ECB/PKCS5Padding");

        cipher.init(
                Cipher.ENCRYPT_MODE,
                secretKey
        );

        return cipher.doFinal(data);
    }


    public static byte[] decryptAES(
            byte[] encryptedData,
            String password,
            int keySize) throws Exception {

        byte[] keyBytes =
                getAESKey(password, keySize);

        SecretKeySpec secretKey =
                new SecretKeySpec(keyBytes, "AES");

        Cipher cipher =
                Cipher.getInstance("AES/ECB/PKCS5Padding");

        cipher.init(
                Cipher.DECRYPT_MODE,
                secretKey
        );

        return cipher.doFinal(encryptedData);
    }


    // AES-128

    public static byte[] encryptAES128(
            byte[] data,
            String password) throws Exception {

        return encryptAES(data, password, 128);
    }


    public static byte[] decryptAES128(
            byte[] data,
            String password) throws Exception {

        return decryptAES(data, password, 128);
    }


    // AES-192

    public static byte[] encryptAES192(
            byte[] data,
            String password) throws Exception {

        return encryptAES(data, password, 192);
    }


    public static byte[] decryptAES192(
            byte[] data,
            String password) throws Exception {

        return decryptAES(data, password, 192);
    }


    // AES-256

    public static byte[] encryptAES256(
            byte[] data,
            String password) throws Exception {

        return encryptAES(data, password, 256);
    }


    public static byte[] decryptAES256(
            byte[] data,
            String password) throws Exception {

        return decryptAES(data, password, 256);
    }


    // =========================================================
    // DES
    // =========================================================

    public static byte[] encryptDES(
            byte[] data,
            String password) throws Exception {

        byte[] keyBytes =
                getDESKey(password);

        SecretKeySpec secretKey =
                new SecretKeySpec(keyBytes, "DES");

        Cipher cipher =
                Cipher.getInstance("DES/ECB/PKCS5Padding");

        cipher.init(
                Cipher.ENCRYPT_MODE,
                secretKey
        );

        return cipher.doFinal(data);
    }


    public static byte[] decryptDES(
            byte[] encryptedData,
            String password) throws Exception {

        byte[] keyBytes =
                getDESKey(password);

        SecretKeySpec secretKey =
                new SecretKeySpec(keyBytes, "DES");

        Cipher cipher =
                Cipher.getInstance("DES/ECB/PKCS5Padding");

        cipher.init(
                Cipher.DECRYPT_MODE,
                secretKey
        );

        return cipher.doFinal(encryptedData);
    }


    // =========================================================
    // 2DES
    // =========================================================

    public static byte[] encrypt2DES(
            byte[] data,
            String password) throws Exception {

        SecretKeySpec[] keys =
                get2DESKeys(password);

        Cipher cipher =
                Cipher.getInstance("DES/ECB/PKCS5Padding");

        // First DES encryption
        cipher.init(
                Cipher.ENCRYPT_MODE,
                keys[0]
        );

        byte[] firstEncryption =
                cipher.doFinal(data);

        // Second DES encryption
        cipher.init(
                Cipher.ENCRYPT_MODE,
                keys[1]
        );

        return cipher.doFinal(firstEncryption);
    }


    public static byte[] decrypt2DES(
            byte[] encryptedData,
            String password) throws Exception {

        SecretKeySpec[] keys =
                get2DESKeys(password);

        Cipher cipher =
                Cipher.getInstance("DES/ECB/PKCS5Padding");

        // Reverse order

        cipher.init(
                Cipher.DECRYPT_MODE,
                keys[1]
        );

        byte[] firstDecryption =
                cipher.doFinal(encryptedData);

        cipher.init(
                Cipher.DECRYPT_MODE,
                keys[0]
        );

        return cipher.doFinal(firstDecryption);
    }


    // =========================================================
    // 3DES
    // =========================================================

    public static byte[] encrypt3DES(
            byte[] data,
            String password) throws Exception {

        byte[] keyBytes =
                get3DESKey(password);

        SecretKeySpec secretKey =
                new SecretKeySpec(
                        keyBytes,
                        "DESede"
                );

        Cipher cipher =
                Cipher.getInstance(
                        "DESede/ECB/PKCS5Padding"
                );

        cipher.init(
                Cipher.ENCRYPT_MODE,
                secretKey
        );

        return cipher.doFinal(data);
    }


    public static byte[] decrypt3DES(
            byte[] encryptedData,
            String password) throws Exception {

        byte[] keyBytes =
                get3DESKey(password);

        SecretKeySpec secretKey =
                new SecretKeySpec(
                        keyBytes,
                        "DESede"
                );

        Cipher cipher =
                Cipher.getInstance(
                        "DESede/ECB/PKCS5Padding"
                );

        cipher.init(
                Cipher.DECRYPT_MODE,
                secretKey
        );

        return cipher.doFinal(encryptedData);
    }


    // =========================================================
    // BLOWFISH
    // =========================================================

    public static byte[] encryptBlowfish(
            byte[] data,
            String password) throws Exception {

        byte[] keyBytes =
                getBlowfishKey(password);

        SecretKeySpec secretKey =
                new SecretKeySpec(
                        keyBytes,
                        "Blowfish"
                );

        Cipher cipher =
                Cipher.getInstance(
                        "Blowfish/ECB/PKCS5Padding"
                );

        cipher.init(
                Cipher.ENCRYPT_MODE,
                secretKey
        );

        return cipher.doFinal(data);
    }


    public static byte[] decryptBlowfish(
            byte[] encryptedData,
            String password) throws Exception {

        byte[] keyBytes =
                getBlowfishKey(password);

        SecretKeySpec secretKey =
                new SecretKeySpec(
                        keyBytes,
                        "Blowfish"
                );

        Cipher cipher =
                Cipher.getInstance(
                        "Blowfish/ECB/PKCS5Padding"
                );

        cipher.init(
                Cipher.DECRYPT_MODE,
                secretKey
        );

        return cipher.doFinal(encryptedData);
    }
}
