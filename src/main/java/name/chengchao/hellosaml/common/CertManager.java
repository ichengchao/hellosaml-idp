package name.chengchao.hellosaml.common;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

import org.opensaml.xml.security.x509.BasicX509Credential;

/**
 * CertManager
 * 
 * @author charles
 * @date 2020-09-29
 */
public class CertManager {

    public static BasicX509Credential credential;

    public static void initSigningCredential() throws Throwable {
//        String publicKeyLocation = CertManager.class.getClassLoader().getResource(CommonConstants.PUBLIC_KEY_PATH)
//                .getFile();
//        String privateKeyLocation = CertManager.class.getClassLoader().getResource(CommonConstants.PRIVATE_KEY_PATH)
//                .getFile();

        InputStream inStream = new FileInputStream(CommonConstants.PUBLIC_KEY_PATH);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate publicKey = (X509Certificate) cf.generateCertificate(inStream);
        inStream.close();

        // create private key
        RandomAccessFile raf = new RandomAccessFile(CommonConstants.PRIVATE_KEY_PATH, "r");
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
    }

    public static BasicX509Credential getCredential() {
        return credential;
    }

}
