package name.chengchao.hellosaml.controller;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import name.chengchao.hellosaml.common.CommonConstants;
import name.chengchao.hellosaml.util.OpenSamlImplementation;
import name.chengchao.hellosaml.util.SAMLHelper;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.opensaml.saml.common.xml.SAMLConstants.SAML2_POST_BINDING_URI;

@Controller
@RequestMapping("/saml")
public class TestSamlController {

    @Value("127.0.0.1")
    private volatile String host;

    @Value("8888")
    private String port;

    private String sessionKey = "sessionkey";

    @Resource
    private OpenSamlImplementation openSamlImplementation;

    @Autowired
    private SAMLHelper samlHelper;

    @PostConstruct
    public void init() {
        if (!"80".equalsIgnoreCase(port)) {
            host = host + ":" + port;
        }
    }

    /**
     * 生成IDP元数据
     *
     * @return
     */
    @RequestMapping("/metadata")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> metadata()
            throws MarshallingException, SignatureException, SecurityException {

        return ResponseEntity.ok().headers(createHeader())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new ByteArrayResource(this.generateIDPMetadataXML().getBytes()));

    }

    private HttpHeaders createHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment; filename=" + System.currentTimeMillis() + ".xml");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf(System.currentTimeMillis()));
        return headers;
    }

    public String generateIDPMetadataXML() throws MarshallingException, SecurityException {
        EntityDescriptor entityDescriptor = openSamlImplementation.buildSAMLObject(EntityDescriptor.class);
        // EntityId是metadata地址
        String idpEntityId = CommonConstants.IDP_ENTITY_ID;
        entityDescriptor.setEntityID(idpEntityId);
        // IDP用于SSO的描述符
        IDPSSODescriptor idpssoDescriptor = openSamlImplementation.buildSAMLObject(IDPSSODescriptor.class);
        // 必须的
        idpssoDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);
        // 不加签
        idpssoDescriptor.setWantAuthnRequestsSigned(false);
        // 用于验断言的 Key 信息生成
        KeyDescriptor keyDescriptor = openSamlImplementation.buildSAMLObject(KeyDescriptor.class);
        KeyInfoGenerator keyInfoGenerator = openSamlImplementation
                .getKeyInfoGenerator(openSamlImplementation.getSelfCredential());
        keyDescriptor.setUse(UsageType.SIGNING);
        keyDescriptor.setKeyInfo(keyInfoGenerator.generate(openSamlImplementation.getSelfCredential()));
        idpssoDescriptor.getKeyDescriptors().add(keyDescriptor);
        // IDP返回的NameIDFormat
        NameIDFormat nameIDFormat = openSamlImplementation.buildSAMLObject(NameIDFormat.class);
        nameIDFormat.setFormat(NameIDType.UNSPECIFIED);
        idpssoDescriptor.getNameIDFormats().add(nameIDFormat);
        // SSO地址相关
        SingleSignOnService singleSignOnService = openSamlImplementation.buildSAMLObject(SingleSignOnService.class);
        singleSignOnService.setBinding(SAML2_POST_BINDING_URI);
        // 本次接入这个URL不需要使用
        singleSignOnService.setLocation(CommonConstants.Location);

        idpssoDescriptor.getSingleSignOnServices().add(singleSignOnService);
        entityDescriptor.getRoleDescriptors().add(idpssoDescriptor);

        return openSamlImplementation.transformSAMLObject2String(entityDescriptor);
    }

}
