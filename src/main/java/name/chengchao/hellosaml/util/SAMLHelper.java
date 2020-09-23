package name.chengchao.hellosaml.util;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import name.chengchao.hellosaml.common.Algo;
import name.chengchao.hellosaml.common.CommonConstants;
import name.chengchao.hellosaml.common.DigestMethod;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.joda.time.DateTime;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.impl.XSAnyBuilder;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPPostEncoder;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.springframework.stereotype.Component;

import static name.chengchao.hellosaml.common.CommonConstants.ALIYUN_IDENTIFIER;
import static org.springframework.util.StringUtils.hasText;

@Component
public class SAMLHelper {

    @Resource
    private OpenSamlImplementation openSamlImplementation;

    /**
     * 构建SAMLRepsonse
     */
    public Response buildResponse(String messageId) {
        Assertion assertion = openSamlImplementation.buildSAMLObject(Assertion.class);
        DateTime now = new DateTime();
        // 生成一个随机id
        assertion.setID(messageId);

        // subject 断言的对象主体,指定是什么对象nameid，有效期是什么时候 Recipient：阿里云通过检查该元素的值来确保阿里云是该断言的目标接收方
        Subject subject = openSamlImplementation.buildSAMLObject(Subject.class);
        NameID nameID = openSamlImplementation.buildSAMLObject(NameID.class);
        nameID.setValue(CommonConstants.ROLE_SESSION_NAME);
        nameID.setFormat(NameIDType.PERSISTENT);
        subject.setNameID(nameID);
        SubjectConfirmation subjectConfirmation = openSamlImplementation.buildSAMLObject(SubjectConfirmation.class);
        SubjectConfirmationData subjectConfirmationData = openSamlImplementation
                .buildSAMLObject(SubjectConfirmationData.class);
        subjectConfirmationData.setNotOnOrAfter(now.plusMinutes(5));
        subjectConfirmationData.setRecipient(CommonConstants.ALIYUN_REPLY_URL);
        subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);
        subject.getSubjectConfirmations().add(subjectConfirmation);
        assertion.setSubject(subject);
        assertion.getAuthnStatements().add(getAuthnStatement(messageId));
        assertion.setIssueInstant(now);
        // issuer的值与entityId一致 必须元素
        assertion.setIssuer(idpBuildIssuer());
        assertion.setIssueInstant(now);

        // Conditions中的AudienceRestriction中的Audience必须为urn:alibaba:cloudcomputing
        Conditions conditions = openSamlImplementation.buildSAMLObject(Conditions.class);
        conditions.setNotBefore(now);
        conditions.setNotOnOrAfter(now.plusSeconds(5));
        AudienceRestriction audienceRestriction = openSamlImplementation.buildSAMLObject(AudienceRestriction.class);
        Audience audience = openSamlImplementation.buildSAMLObject(Audience.class);
        audience.setAudienceURI(ALIYUN_IDENTIFIER);
        audienceRestriction.getAudiences().add(audience);
        conditions.getAudienceRestrictions().add(audienceRestriction);
        assertion.setConditions(conditions);
        // 在SAML断言的AttributeStatement元素中，必须包含以下阿里云要求的Attribute元素：必须指定角色,
        // 指定对应的rolesessionname
        AttributeStatement attributeStatement = openSamlImplementation.buildSAMLObject(AttributeStatement.class);

        Attribute aliRole = openSamlImplementation.buildSAMLObject(Attribute.class);
        aliRole.setName("https://www.aliyun.com/SAML-Role/Attributes/Role");
        XSAny aliyunRole = new XSAnyBuilder().buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
        aliyunRole.setTextContent(CommonConstants.ROLE_ARN);
        aliRole.getAttributeValues().add(aliyunRole);
        attributeStatement.getAttributes().add(aliRole);

        Attribute roleSessionName = openSamlImplementation.buildSAMLObject(Attribute.class);
        roleSessionName.setName("https://www.aliyun.com/SAML-Role/Attributes/RoleSessionName");
        XSAny RoleSessionNameAttributeValue = new XSAnyBuilder().buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
        RoleSessionNameAttributeValue.setTextContent(CommonConstants.ROLE_SESSION_NAME);
        roleSessionName.getAttributeValues().add(RoleSessionNameAttributeValue);
        attributeStatement.getAttributes().add(roleSessionName);

        assertion.getAttributeStatements().add(attributeStatement);

        Response response = openSamlImplementation.buildSAMLObject(Response.class);
        response.setID(OpenSamlImplementation.generateSecureRandomId());
        Status status = openSamlImplementation.buildSAMLObject(Status.class);
        StatusCode statusCode = openSamlImplementation.buildSAMLObject(StatusCode.class);
        statusCode.setValue(StatusCode.SUCCESS);
        status.setStatusCode(statusCode);

        response.setStatus(status);
        response.setDestination(CommonConstants.ALIYUN_REPLY_URL);
        response.getAssertions().add(assertion);
        response.setIssueInstant(now);
        response.setIssuer(this.idpBuildIssuer());
        response.setVersion(SAMLVersion.VERSION_20);
        // 对断言加签
        openSamlImplementation.signObject(assertion, Algo.RSA_SHA256, DigestMethod.RIPEMD160);
        return response;
    }

    private AuthnStatement getAuthnStatement(String msgId) {
        AuthnStatement authnStatement = openSamlImplementation.buildSAMLObject(AuthnStatement.class);
        AuthnContext authnContext = openSamlImplementation.buildSAMLObject(AuthnContext.class);
        AuthnContextClassRef authnContextClassRef = openSamlImplementation.buildSAMLObject(AuthnContextClassRef.class);
        authnContextClassRef.setAuthnContextClassRef(AuthnContext.PASSWORD_AUTHN_CTX);
        authnContext.setAuthnContextClassRef(authnContextClassRef);
        authnStatement.setAuthnContext(authnContext);
        authnStatement.setAuthnInstant(new DateTime());
        // 当从 SP 登出时 需要通过 SessionIndex 来确定出会话
        authnStatement.setSessionIndex(msgId);

        return authnStatement;
    }

    // 合作伙伴的唯一标识
    public Issuer idpBuildIssuer() {
        Issuer issuer = openSamlImplementation.buildSAMLObject(Issuer.class);
        issuer.setValue(CommonConstants.IDP_ENTITY_ID);
        return issuer;
    }

    /**
     * HTTP POST BINDING 时用于编码返回结果并返回给浏览器 使用其他方式返回时可以使用
     */
    public void httpPostBinding(String relayState, HttpServletResponse res, String acsUrl, Response response)
            throws ComponentInitializationException, MessageEncodingException {
        // HTTP相关的类不放到 openSamlImplementation 中
        MessageContext messageContext = new MessageContext();
        messageContext.setMessage(response);
        if (hasText(relayState)) {
            messageContext.getSubcontext(SAMLBindingContext.class, true).setRelayState(relayState);
        }
        SAMLEndpointContext samlEndpointContext = messageContext.getSubcontext(SAMLPeerEntityContext.class, true)
                .getSubcontext(SAMLEndpointContext.class, true);
        Endpoint endpoint = openSamlImplementation.buildSAMLObject(AssertionConsumerService.class);
        endpoint.setLocation(acsUrl);
        samlEndpointContext.setEndpoint(endpoint);
        // openSamlImplementation.
        HTTPPostEncoder httpPostEncoder = new HTTPPostEncoder();
        httpPostEncoder.setMessageContext(messageContext);
        httpPostEncoder.setVelocityEngine(velocityEngine);
        httpPostEncoder.setHttpServletResponse(res);
        httpPostEncoder.initialize();
        httpPostEncoder.encode();
    }

    private VelocityEngine velocityEngine;

    public SAMLHelper() {
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.ENCODING_DEFAULT, "UTF-8");
        velocityEngine.setProperty(RuntimeConstants.OUTPUT_ENCODING, "UTF-8");
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.init();

    }

}
