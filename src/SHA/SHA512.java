package SHA;

import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.security.PublicKey;

public class SHA512 {
    private static String ALGORITHM = "SHA-512";

    public static PublicKey[] encrypt(String message) throws NoSuchAlgorithmException, InvalidKeySpecException{
        MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
        byte[] messageByte = digest.digest(message.getBytes());
        int size = messageByte.length/2;
        byte[] K_AB1 = new byte[size];
        byte[] K_AB2 = new byte[messageByte.length - size];

        System.arraycopy(messageByte, 0, K_AB1, 0, size);
        System.arraycopy(messageByte, size, K_AB2, 0, K_AB2.length);

        PublicKey K_AB1Key = generatePublicKey(K_AB1);
        PublicKey K_AB2Key = generatePublicKey(K_AB2);
        PublicKey[] publicKeys =  new PublicKey[] {K_AB1Key, K_AB2Key};
        return publicKeys;
    }

    public static PublicKey generatePublicKey(byte[] bytes) throws InvalidKeySpecException, NoSuchAlgorithmException{
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
        return publicKey;
    }
}
