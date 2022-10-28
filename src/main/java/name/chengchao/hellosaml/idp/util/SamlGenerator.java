package name.chengchao.hellosaml.idp.util;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opensaml.core.config.InitializationService;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.impl.ResponseMarshaller;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml.saml2.metadata.impl.EntityDescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.EntityDescriptorMarshaller;
import org.opensaml.saml.saml2.metadata.impl.IDPSSODescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.KeyDescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.SingleSignOnServiceBuilder;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.X509Certificate;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.impl.KeyInfoBuilder;
import org.opensaml.xmlsec.signature.impl.X509CertificateBuilder;
import org.opensaml.xmlsec.signature.impl.X509DataBuilder;
import org.w3c.dom.Element;

import name.chengchao.hellosaml.idp.common.CertManager;
import name.chengchao.hellosaml.idp.common.CommonConstants;

public class SamlGenerator {
    static {
        try {
            // 初始化证书
            CertManager.initSigningCredential();
            InitializationService.initialize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        // generateResponse();
        generateMetaXML();
    }

//    private static Logger logger = LoggerFactory.getLogger(SamlGenerator.class);

    /**
     * 生成base64的SAMLResponse
     *
     * @return
     * @throws Throwable
     */
    public static String generateResponse(String identifier, String replyUrl, String nameID,
        HashMap<String, List<String>> attributes) throws Exception {
        Response responseInitial = SamlAssertionProducer.createSAMLResponse(identifier, replyUrl, nameID, attributes);
        // output Response
        ResponseMarshaller marshaller = new ResponseMarshaller();
        Element element = marshaller.marshall(responseInitial);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(element), new StreamResult(baos));
        // XMLHelper.writeNode(element, baos);
        String responseStr = new String(baos.toByteArray());
        String base64Encode = java.util.Base64.getEncoder().encodeToString(responseStr.getBytes());

        // logger.info("********************************SAML Response XML*******************************");
        // logger.info(responseStr);
        // logger.info("********************************************************************************");
        // logger.info("************************Response Base64*************************");
        // logger.info(base64Encode);
        // logger.info("****************************************************************");
        return base64Encode;
    }

    public static String generateMetaXML() throws Exception {

        EntityDescriptorBuilder entityDescriptorBuilder = new EntityDescriptorBuilder();
        EntityDescriptor entityDescriptor = entityDescriptorBuilder.buildObject();
        entityDescriptor.setEntityID(CommonConstants.IDP_ENTITY_ID);

        IDPSSODescriptorBuilder idpssoDescriptorBuilder = new IDPSSODescriptorBuilder();
        IDPSSODescriptor idpssoDescriptor = idpssoDescriptorBuilder.buildObject();
        idpssoDescriptor.setWantAuthnRequestsSigned(false);
        idpssoDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);
        entityDescriptor.getRoleDescriptors().add(idpssoDescriptor);

        KeyInfoBuilder keyInfoBuilder = new KeyInfoBuilder();
        KeyInfo keyInfo = keyInfoBuilder.buildObject();

        X509CertificateBuilder x509CertificateBuilder = new X509CertificateBuilder();
        X509Certificate x509Certificate = x509CertificateBuilder.buildObject();

        x509Certificate.setValue(new String(
            java.util.Base64.getEncoder().encode(CertManager.getCredential().getEntityCertificate().getEncoded())));

        X509DataBuilder x509DataBuilder = new X509DataBuilder();
        X509Data x509Data = x509DataBuilder.buildObject();
        x509Data.getX509Certificates().add(x509Certificate);
        keyInfo.getX509Datas().add(x509Data);

        KeyDescriptorBuilder keyDescriptorBuilder = new KeyDescriptorBuilder();
        KeyDescriptor keyDescriptor = keyDescriptorBuilder.buildObject();
        keyDescriptor.setUse(UsageType.SIGNING);
        keyDescriptor.setKeyInfo(keyInfo);
        idpssoDescriptor.getKeyDescriptors().add(keyDescriptor);

        SingleSignOnServiceBuilder singleSignOnServiceBuilder = new SingleSignOnServiceBuilder();
        SingleSignOnService singleSignOnService = singleSignOnServiceBuilder.buildObject();
        singleSignOnService.setBinding(SAMLConstants.SAML2_POST_BINDING_URI);
        // singleSignOnService.setLocation(CommonConstants.Location);
        idpssoDescriptor.getSingleSignOnServices().add(singleSignOnService);

        // output EntityDescriptor
        EntityDescriptorMarshaller marshaller = new EntityDescriptorMarshaller();
        Element element = marshaller.marshall(entityDescriptor);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(element), new StreamResult(baos));
        // XMLHelper.writeNode(element, baos);
        String metaXMLStr = new String(baos.toByteArray());

        System.out.println("===============MetaXML================");
        System.out.println(metaXMLStr);
        System.out.println("===============MetaXML================");

        return metaXMLStr;

    }

}
