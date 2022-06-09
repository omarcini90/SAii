package mx.org.fimpes.saii.ejb.security.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.ssl.PKCS8Key;
import org.apache.log4j.Logger;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
public class Crypto {

    private static final Logger log = Logger.getLogger(Crypto.class);
    public enum ALGORITHMS {
        SHA1, SHA224, SHA256, SHA384, SHA512
    }

    public static String byteToHex(byte[] digest) {
        /* METHOD 2:
        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            hex.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
        }
        return hex.toString();
         */
        return Hex.encodeHexString(digest);
    }

    public static byte[] hexToByte(String hex) throws DecoderException {
        /* METHOD 2:
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
         */
        return Hex.decodeHex(hex.toCharArray());
    }
    
    public static String encodeToBase64(String plaintext) {
        return Base64.getEncoder().encodeToString(plaintext.getBytes());
    }

    public static byte[] encodeToBase64(byte[] src) {
        return Base64.getEncoder().encode(src);
    }

    public static String decodeFromBase64(String base64) {
        return new String(Base64.getDecoder().decode(base64));
    }

    public static byte[] decodeFromBase64(byte[] base64) {
        return Base64.getDecoder().decode(base64);
    }

    public static String digestOf(ALGORITHMS hash, byte[] input) {
        Digest d;
        switch (hash) {
            case SHA1:
                d = new SHA1Digest();
                break;
            case SHA224:
                d = new SHA224Digest();
                break;
            case SHA256:
            default:
                d = new SHA256Digest();
                break;
            case SHA384:
                d = new SHA384Digest();
                break;
            case SHA512:
                d = new SHA512Digest();
                break;
        }
        d.update(input, 0, input.length);
        byte[] digestData = new byte[d.getDigestSize()];
        d.doFinal(digestData, 0);
        String hex = byteToHex(digestData);

        return hex;
    }
    
    public static String sha256digestOf(String text) {
        String hex = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes());
            byte digest[] = md.digest();
            hex = byteToHex(digest);
        } catch (NoSuchAlgorithmException ex) {
            log.error("Encryption error", ex);
        }

        return hex;
    }
    
    public static String sha512digestOf(String text) {
        String hex = null;
        try {
            var md = MessageDigest.getInstance("SHA-512");
            md.update(text.getBytes());
            byte digest[] = md.digest();
            hex = byteToHex(digest);
        } catch (NoSuchAlgorithmException ex) {
            log.error("Encryption error", ex);
        }

        return hex;
    }

    public static X509Certificate generateCertificate(InputStream stream) throws CertificateException, IOException {
        var factory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) factory.generateCertificate(stream);
    }

    public static PrivateKey generatePrivateKey(byte[] keyBytes, String password) throws GeneralSecurityException {
        /* METHOD 2:
        var encryptPKInfo = new EncryptedPrivateKeyInfo(keyBytes);
        var cipher = Cipher.getInstance(encryptPKInfo.getAlgName());
        var pbeKeySpec = new PBEKeySpec(password.toCharArray());
        var secFac = SecretKeyFactory.getInstance(encryptPKInfo.getAlgName());
        Key pbeKey = secFac.generateSecret(pbeKeySpec);
        AlgorithmParameters algParams = encryptPKInfo.getAlgParameters();
        cipher.init(Cipher.DECRYPT_MODE, pbeKey, algParams);
        var pkcs8KeySpec = encryptPKInfo.getKeySpec(cipher);
        var kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(pkcs8KeySpec);*/

        // Si el inputStream (puede ser DER-raw ASN.1 o PEM-base64) esta encriptado, el password es requerido;
        // de lo contrario, es ignorado y puede ser NULL.
        PrivateKey pk = null;
        try {
            var pkcs8 = new PKCS8Key(keyBytes, password.toCharArray());
            // Si se provee una clave PKCS8 desencriptada, se obtiene lo que se pasó sin cambio.
            // Si se provee una clave OpenSSL, se formatea como PKCS #8, y con esto, los bytes ya no serán OpenSSL.
            byte[] decrypted = pkcs8.getDecryptedBytes();
            var spec = new PKCS8EncodedKeySpec(decrypted);
            // Se obtiene la clave privada: pk = pkcs8.getPrivateKey();
            if (pkcs8.isDSA()) {
                pk = KeyFactory.getInstance("DSA").generatePrivate(spec);
            } else if (pkcs8.isRSA()) {
                pk = KeyFactory.getInstance("RSA").generatePrivate(spec);
            }
        } catch (GeneralSecurityException e) {
            throw new GeneralSecurityException("CONTRASEÑA DE LA CLAVE PRIVADA INCORRECTA", e);
        }

        return pk;
    }

    public static void verifyKeys(PublicKey publicKey, PrivateKey privateKey) throws KeyException {
        if (!(publicKey instanceof RSAPublicKey)) {
            throw new KeyException("EL CERTIFICADO NO CONTIENE UNA LLAVE PUBLICA RSA, SINO " + publicKey.getClass().getName());
        }
        if (!(privateKey instanceof RSAPrivateKey)) {
            throw new KeyException("EL ARCHIVO .key NO CONTIENE UNA LLAVE PRIVADA RSA");
        }

        final var rsaPublicKey = (RSAPublicKey) publicKey;
        final byte[] certModulusData = rsaPublicKey.getModulus().toByteArray();
        final var certIDinHex = digestOf(ALGORITHMS.SHA1, certModulusData);

        final var rsaPrivateKey = (RSAPrivateKey) privateKey;
        final byte[] keyModulusData = rsaPrivateKey.getModulus().toByteArray();
        final var keyIDinHex = digestOf(ALGORITHMS.SHA1, keyModulusData);

        if (!certIDinHex.equalsIgnoreCase(keyIDinHex)) {
            throw new KeyException("LA LLAVE PRIVADA NO CORRESPONDE CON LA LLAVE PUBLICA DEL CERTIFICADO");
        }
    }

    public static byte[] signDigest(String digest, PrivateKey rsaKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        byte[] sign;
        try {
            log.info("SIGNING METHOD No.1: AsymmetricBlockCipher.processBlock(digest.getBytes(), 0, digest.length())");
            var privKey = (RSAPrivateCrtKey) rsaKey;
            var pkParams = new RSAPrivateCrtKeyParameters(privKey.getModulus(), privKey.getPublicExponent(), privKey.getPrivateExponent(),
                    privKey.getPrimeP(), privKey.getPrimeQ(), privKey.getPrimeExponentP(), privKey.getPrimeExponentQ(), privKey.getCrtCoefficient());
            AsymmetricBlockCipher rsa = new PKCS1Encoding(new RSAEngine());
            rsa.init(true, pkParams);
            sign = rsa.processBlock(digest.getBytes(), 0, digest.length());
        } catch (RuntimeException | InvalidCipherTextException e) {
            log.error("FAILED: " + e.getMessage());
            log.info("SIGNING METHOD No.2: Signature.sign()");
            /* METHOD 2: */
            try {
                var signer = Signature.getInstance("SHA256withRSA"); //SHA1withRSA
                signer.initSign(rsaKey);
                signer.update(digest.getBytes());
                sign = signer.sign();
            } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
                log.error("FAILED: " + ex.getMessage());
                throw ex;
            }
        }
        log.info("SUCCESSFUL!");

        return sign;
    }

    public static boolean verifySignature(byte[] data, byte[] signature, Certificate certificate, String algorithm) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        var signer = Signature.getInstance(algorithm); //SHA256withRSA, SHA1withRSA
        signer.initVerify(certificate);
        signer.update(data);

        return signer.verify(signature);
    }
}
