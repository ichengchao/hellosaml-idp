package name.chengchao.hellosaml.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import name.chengchao.hellosaml.common.CertManager;
import name.chengchao.hellosaml.common.CommonConstants;
import org.joda.time.DateTime;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.impl.ResponseMarshaller;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml2.metadata.NameIDFormat;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml2.metadata.impl.EntityDescriptorBuilder;
import org.opensaml.saml2.metadata.impl.EntityDescriptorMarshaller;
import org.opensaml.saml2.metadata.impl.IDPSSODescriptorBuilder;
import org.opensaml.saml2.metadata.impl.KeyDescriptorBuilder;
import org.opensaml.saml2.metadata.impl.NameIDFormatBuilder;
import org.opensaml.saml2.metadata.impl.SingleSignOnServiceBuilder;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.X509Certificate;
import org.opensaml.xml.signature.X509Data;
import org.opensaml.xml.signature.impl.KeyInfoBuilder;
import org.opensaml.xml.signature.impl.X509CertificateBuilder;
import org.opensaml.xml.signature.impl.X509DataBuilder;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

public class SamlGenerator {
    static {
        try {
            // 初始化证书
            CertManager.initSigningCredential();
            DefaultBootstrap.bootstrap();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        generateResponse();
        generateMetaXML();
    }

    /**
     * 生成base64的SAMLResponse
     * 
     * @return
     * @throws Throwable
     */
    public static String generateResponse() throws Exception {
        HashMap<String, List<String>> attributes = new HashMap<String, List<String>>();
        attributes.put(CommonConstants.ATTRIBUTE_KEY_ROLE, CommonConstants.ROLE_LIST);
        List<String> sessionNameList = new ArrayList<String>();
        sessionNameList.add(CommonConstants.ROLE_SESSION_NAME);
        attributes.put(CommonConstants.ATTRIBUTE_KEY_ROLE_SESSION_NAME, sessionNameList);
        Response responseInitial = SamlAssertionProducer.createSAMLResponse("subject", new DateTime(), attributes,
                CommonConstants.IDP_ENTITY_ID, 5);

        // output Response
        ResponseMarshaller marshaller = new ResponseMarshaller();
        Element element = marshaller.marshall(responseInitial);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLHelper.writeNode(element, baos);
        String responseStr = new String(baos.toByteArray());
        String base64Encode = java.util.Base64.getEncoder().encodeToString(responseStr.getBytes());

        System.out.println("===============Response XML================");
        System.out.println(responseStr);
        System.out.println("===============Response XML================");
        System.out.println("===============Response Base64================");
        System.out.println(base64Encode);
        System.out.println("===============Response Base64================");
        return base64Encode;
    }

    public static void generateMetaXML() throws Exception {

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

        x509Certificate.setValue(
                new String(java.util.Base64.getEncoder().encode(CertManager.getCredentialPublicKey().getEncoded())));

        X509DataBuilder x509DataBuilder = new X509DataBuilder();
        X509Data x509Data = x509DataBuilder.buildObject();
        x509Data.getX509Certificates().add(x509Certificate);
        keyInfo.getX509Datas().add(x509Data);

        KeyDescriptorBuilder keyDescriptorBuilder = new KeyDescriptorBuilder();
        KeyDescriptor keyDescriptor = keyDescriptorBuilder.buildObject();
        keyDescriptor.setUse(UsageType.SIGNING);
        keyDescriptor.setKeyInfo(keyInfo);
        idpssoDescriptor.getKeyDescriptors().add(keyDescriptor);

        NameIDFormatBuilder nameIDFormatBuilder = new NameIDFormatBuilder();
        NameIDFormat nameIDFormat = nameIDFormatBuilder.buildObject();
        nameIDFormat.setFormat(NameIDType.UNSPECIFIED);
        idpssoDescriptor.getNameIDFormats().add(nameIDFormat);

        SingleSignOnServiceBuilder singleSignOnServiceBuilder = new SingleSignOnServiceBuilder();
        SingleSignOnService singleSignOnService = singleSignOnServiceBuilder.buildObject();
        singleSignOnService.setBinding(SAMLConstants.SAML2_POST_BINDING_URI);
        // singleSignOnService.setLocation(CommonConstants.Location);
        idpssoDescriptor.getSingleSignOnServices().add(singleSignOnService);

        // output EntityDescriptor
        EntityDescriptorMarshaller marshaller = new EntityDescriptorMarshaller();
        Element element = marshaller.marshall(entityDescriptor);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLHelper.writeNode(element, baos);
        String metaXMLStr = new String(baos.toByteArray());

        System.out.println("===============MetaXML================");
        System.out.println(metaXMLStr);
        System.out.println("===============MetaXML================");

    }

}
