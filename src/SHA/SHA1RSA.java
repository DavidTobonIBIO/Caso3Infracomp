package SHA;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class SHA1RSA {
    private static final String ALGORITHM = "SHA1WithRSA";

    public static byte[] sign(PrivateKey privateKey, String message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException{
        Signature firma = Signature.getInstance(ALGORITHM);
        firma.initSign(privateKey);
        byte[] datos = message.getBytes();
        firma.update(datos);
        byte[] digitalSignature = firma.sign();
        return digitalSignature;
    }

    public static boolean verify(String message, PublicKey publicKey, byte[] digitalSignature) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException{
        Signature firma = Signature.getInstance(ALGORITHM);
        firma.initVerify(publicKey);
        byte[] datos = message.getBytes();
        firma.update(datos);
        boolean valid = firma.verify(digitalSignature);
        return valid;
    }
}
