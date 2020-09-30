package name.chengchao.hellosaml.util;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import name.chengchao.hellosaml.common.CertManager;
import name.chengchao.hellosaml.common.CommonConstants;
import org.joda.time.DateTime;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.saml2.core.Audience;
import org.opensaml.saml2.core.AudienceRestriction;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml2.core.impl.AssertionBuilder;
import org.opensaml.saml2.core.impl.AttributeBuilder;
import org.opensaml.saml2.core.impl.AttributeStatementBuilder;
import org.opensaml.saml2.core.impl.AudienceBuilder;
import org.opensaml.saml2.core.impl.AudienceRestrictionBuilder;
import org.opensaml.saml2.core.impl.AuthnContextBuilder;
import org.opensaml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml2.core.impl.AuthnStatementBuilder;
import org.opensaml.saml2.core.impl.ConditionsBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.NameIDBuilder;
import org.opensaml.saml2.core.impl.ResponseBuilder;
import org.opensaml.saml2.core.impl.ResponseMarshaller;
import org.opensaml.saml2.core.impl.StatusBuilder;
import org.opensaml.saml2.core.impl.StatusCodeBuilder;
import org.opensaml.saml2.core.impl.SubjectBuilder;
import org.opensaml.saml2.core.impl.SubjectConfirmationBuilder;
import org.opensaml.saml2.core.impl.SubjectConfirmationDataBuilder;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.schema.impl.XSStringBuilder;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureConstants;
import org.opensaml.xml.signature.Signer;
import org.opensaml.xml.signature.impl.SignatureBuilder;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

public class SamlAssertionProducer {

    public static Response createSAMLResponse(final String subjectId, final DateTime authenticationTime,
            final HashMap<String, List<String>> attributes, String issuer, Integer samlAssertionDays) throws Exception {
        Signature signature = createSignature();
        Status status = createStatus();
        Issuer responseIssuer = null;
        Issuer assertionIssuer = null;
        Subject subject = null;
        AttributeStatement attributeStatement = null;

        if (issuer != null) {
            responseIssuer = createIssuer(issuer);
            assertionIssuer = createIssuer(issuer);
        }

        if (subjectId != null) {
            subject = createSubject(subjectId, samlAssertionDays);
        }

        if (attributes != null && attributes.size() != 0) {
            attributeStatement = createAttributeStatement(attributes);
        }

        AuthnStatement authnStatement = createAuthnStatement(authenticationTime);

        Assertion assertion = createAssertion(new DateTime(), subject, assertionIssuer, authnStatement,
                attributeStatement, samlAssertionDays);

        Response response = createResponse(new DateTime(), responseIssuer, status, assertion);
        response.setSignature(signature);

        ResponseMarshaller marshaller = new ResponseMarshaller();
        Element element = marshaller.marshall(response);

        if (signature != null) {
            Signer.signObject(signature);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLHelper.writeNode(element, baos);
        return response;
    }

    public static Conditions createConditions(final DateTime notOnOrAfter, final String audienceUri) {
        ConditionsBuilder conditionsBuilder = new ConditionsBuilder();
        final Conditions conditions = conditionsBuilder.buildObject();
        conditions.setNotOnOrAfter(notOnOrAfter);

        AudienceRestrictionBuilder audienceRestrictionBuilder = new AudienceRestrictionBuilder();
        final AudienceRestriction audienceRestriction = audienceRestrictionBuilder.buildObject();
        AudienceBuilder audienceBuilder = new AudienceBuilder();
        final Audience audience = audienceBuilder.buildObject();
        audience.setAudienceURI(audienceUri);
        audienceRestriction.getAudiences().add(audience);
        conditions.getAudienceRestrictions().add(audienceRestriction);
        return conditions;
    }

    private static Response createResponse(final DateTime issueDate, Issuer issuer, Status status,
            Assertion assertion) {

        ResponseBuilder responseBuilder = new ResponseBuilder();
        Response response = responseBuilder.buildObject();
        response.setID(UUID.randomUUID().toString());
        response.setIssueInstant(issueDate);
        response.setVersion(SAMLVersion.VERSION_20);
        response.setIssuer(issuer);
        response.setStatus(status);
        response.getAssertions().add(assertion);
        return response;
    }

    private static Assertion createAssertion(final DateTime issueDate, Subject subject, Issuer issuer,
            AuthnStatement authnStatement, AttributeStatement attributeStatement, final Integer samlAssertionDays) {
        AssertionBuilder assertionBuilder = new AssertionBuilder();
        Assertion assertion = assertionBuilder.buildObject();
        assertion.setID(UUID.randomUUID().toString());
        assertion.setIssueInstant(issueDate);
        assertion.setSubject(subject);
        assertion.setIssuer(issuer);

        DateTime currentDate = new DateTime();
        if (samlAssertionDays != null) {
            currentDate = currentDate.plusDays(samlAssertionDays);
        }
        Conditions conditions = createConditions(currentDate, CommonConstants.ALIYUN_IDENTIFIER);
        assertion.setConditions(conditions);

        if (authnStatement != null)
            assertion.getAuthnStatements().add(authnStatement);

        if (attributeStatement != null)
            assertion.getAttributeStatements().add(attributeStatement);

        return assertion;
    }

    private static Issuer createIssuer(final String issuerName) {
        // create Issuer object
        IssuerBuilder issuerBuilder = new IssuerBuilder();
        Issuer issuer = issuerBuilder.buildObject();
        issuer.setValue(issuerName);
        return issuer;
    }

    private static Subject createSubject(final String subjectId, final Integer samlAssertionDays) {
        DateTime currentDate = new DateTime();
        if (samlAssertionDays != null) {
            currentDate = currentDate.plusDays(samlAssertionDays);
        }

        // create name element
        NameIDBuilder nameIdBuilder = new NameIDBuilder();
        NameID nameId = nameIdBuilder.buildObject();
        nameId.setValue(subjectId);
        nameId.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");

        SubjectConfirmationDataBuilder dataBuilder = new SubjectConfirmationDataBuilder();
        SubjectConfirmationData subjectConfirmationData = dataBuilder.buildObject();
        subjectConfirmationData.setNotOnOrAfter(currentDate);
        subjectConfirmationData.setRecipient(CommonConstants.ALIYUN_REPLY_URL);

        SubjectConfirmationBuilder subjectConfirmationBuilder = new SubjectConfirmationBuilder();
        SubjectConfirmation subjectConfirmation = subjectConfirmationBuilder.buildObject();
        subjectConfirmation.setMethod("urn:oasis:names:tc:SAML:2.0:cm:bearer");
        subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);

        // create subject element
        SubjectBuilder subjectBuilder = new SubjectBuilder();
        Subject subject = subjectBuilder.buildObject();
        subject.setNameID(nameId);
        subject.getSubjectConfirmations().add(subjectConfirmation);

        return subject;
    }

    private static AuthnStatement createAuthnStatement(final DateTime issueDate) {
        // create authcontextclassref object
        AuthnContextClassRefBuilder classRefBuilder = new AuthnContextClassRefBuilder();
        AuthnContextClassRef classRef = classRefBuilder.buildObject();
        classRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");

        // create authcontext object
        AuthnContextBuilder authContextBuilder = new AuthnContextBuilder();
        AuthnContext authnContext = authContextBuilder.buildObject();
        authnContext.setAuthnContextClassRef(classRef);

        // create authenticationstatement object
        AuthnStatementBuilder authStatementBuilder = new AuthnStatementBuilder();
        AuthnStatement authnStatement = authStatementBuilder.buildObject();
        authnStatement.setAuthnInstant(issueDate);
        authnStatement.setAuthnContext(authnContext);

        return authnStatement;
    }

    private static AttributeStatement createAttributeStatement(HashMap<String, List<String>> attributes) {
        // create authenticationstatement object
        AttributeStatementBuilder attributeStatementBuilder = new AttributeStatementBuilder();
        AttributeStatement attributeStatement = attributeStatementBuilder.buildObject();

        AttributeBuilder attributeBuilder = new AttributeBuilder();
        if (attributes != null) {
            for (Map.Entry<String, List<String>> entry : attributes.entrySet()) {
                Attribute attribute = attributeBuilder.buildObject();
                attribute.setName(entry.getKey());

                for (String value : entry.getValue()) {
                    XSStringBuilder stringBuilder = new XSStringBuilder();
                    XSString attributeValue = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME,
                            XSString.TYPE_NAME);
                    attributeValue.setValue(value);
                    attribute.getAttributeValues().add(attributeValue);
                }

                attributeStatement.getAttributes().add(attribute);
            }
        }

        return attributeStatement;
    }

    private static Status createStatus() {
        StatusCodeBuilder statusCodeBuilder = new StatusCodeBuilder();
        StatusCode statusCode = statusCodeBuilder.buildObject();
        statusCode.setValue(StatusCode.SUCCESS_URI);
        StatusBuilder statusBuilder = new StatusBuilder();
        Status status = statusBuilder.buildObject();
        status.setStatusCode(statusCode);
        return status;
    }

    private static Signature createSignature() throws Exception {
        SignatureBuilder builder = new SignatureBuilder();
        Signature signature = builder.buildObject();
        signature.setSigningCredential(CertManager.getCredential());
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        return signature;
    }
}
