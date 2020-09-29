package name.chengchao.hellosaml.common;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.x509.BasicX509Credential;

/**
 * CertManager
 * 
 * @author charles
 * @date 2020-09-29
 */
public class CertManager {

//    #create the keypair
//    openssl req -new -x509 -days 3652 -nodes -out saml.crt -keyout saml.pem
//
//    #convert the private key to pkcs8 format
//    openssl pkcs8 -topk8 -inform PEM -outform DER -in saml.pem -out saml.pkcs8 -nocrypt

    public static Credential credential;

    public static X509Certificate credentialPublicKey;

    public static void initSigningCredential() throws Throwable {
        // create public key (cert) portion of credential
        String publicKeyLocation = CertManager.class.getClassLoader().getResource(CommonConstants.PUBLIC_KEY_PATH)
                .getFile();
        String privateKeyLocation = CertManager.class.getClassLoader().getResource(CommonConstants.PRIVATE_KEY_PATH)
                .getFile();

        InputStream inStream = new FileInputStream(publicKeyLocation);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate publicKey = (X509Certificate) cf.generateCertificate(inStream);
        inStream.close();

        // create private key
        RandomAccessFile raf = new RandomAccessFile(privateKeyLocation, "r");
        byte[] buf = new byte[(int) raf.length()];
        raf.readFully(buf);
        raf.close();

        PKCS8EncodedKeySpec kspec = new PKCS8EncodedKeySpec(buf);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(kspec);

        // create credential and initialize
        BasicX509Credential basicX509Credential = new BasicX509Credential();
        basicX509Credential.setEntityCertificate(publicKey);
        basicX509Credential.setPrivateKey(privateKey);

        credential = basicX509Credential;
        // TODO
        credentialPublicKey = publicKey;

    }

    public static Credential getCredential() {
        return credential;
    }

    public static X509Certificate getCredentialPublicKey() {
        return credentialPublicKey;
    }
}
