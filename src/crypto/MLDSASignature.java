package crypto;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.crystals.dilithium.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.io.*;

public class MLDSASignature {
    
    private DilithiumKeyPairGenerator keyGen;
    private DilithiumSigner signer;
    private SecureRandom random;
    
    public static class KeyPair implements Serializable {
        public transient DilithiumPublicKeyParameters publicKey;
        public transient DilithiumPrivateKeyParameters privateKey;
        public String publicKeyEncoded;
        public String privateKeyEncoded;
        
        public KeyPair(DilithiumPublicKeyParameters pub, DilithiumPrivateKeyParameters priv) {
            this.publicKey = pub;
            this.privateKey = priv;
            this.publicKeyEncoded = Base64.getEncoder().encodeToString(pub.getEncoded());
            this.privateKeyEncoded = Base64.getEncoder().encodeToString(priv.getEncoded());
        }
    }
    
    public MLDSASignature() {
        this.random = new SecureRandom();
        this.keyGen = new DilithiumKeyPairGenerator();
        this.keyGen.init(new DilithiumKeyGenerationParameters(random, DilithiumParameters.dilithium3));
        this.signer = new DilithiumSigner();
    }
    
    public KeyPair generateKeyPair() {
        AsymmetricCipherKeyPair pair = keyGen.generateKeyPair();
        DilithiumPublicKeyParameters pubKey = (DilithiumPublicKeyParameters) pair.getPublic();
        DilithiumPrivateKeyParameters privKey = (DilithiumPrivateKeyParameters) pair.getPrivate();
        return new KeyPair(pubKey, privKey);
    }
    
    public String sign(String message, KeyPair keyPair) {
        try {
            signer.init(true, new ParametersWithRandom(keyPair.privateKey, random));
            byte[] signature = signer.generateSignature(message.getBytes());
            return Base64.getEncoder().encodeToString(signature);
        } catch (Exception e) {
            return null;
        }
    }
    
    public boolean verify(String message, String signatureStr, KeyPair keyPair) {
        try {
            byte[] signature = Base64.getDecoder().decode(signatureStr);
            signer.init(false, keyPair.publicKey);
            return signer.verifySignature(message.getBytes(), signature);
        } catch (Exception e) {
            return false;
        }
    }
}
