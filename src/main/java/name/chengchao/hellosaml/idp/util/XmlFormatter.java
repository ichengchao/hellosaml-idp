package name.chengchao.hellosaml.idp.util;

import java.io.StringWriter;

import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class XmlFormatter {

    public static String prettyPrintByDom4j(String xmlString, int indent, boolean skipDeclaration) {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setIndentSize(indent);
            format.setSuppressDeclaration(skipDeclaration);
            format.setEncoding("UTF-8");

            org.dom4j.Document document = DocumentHelper.parseText(xmlString);
            StringWriter sw = new StringWriter();
            XMLWriter writer = new XMLWriter(sw, format);
            writer.write(document);
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error occurs when pretty-printing xml:\n" + xmlString, e);
        }
    }

    public static void main(String[] args) {
        String xmlString =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><saml2p:Response xmlns:saml2p=\"urn:oasis:names:tc:SAML:2.0:protocol\" Destination=\"https://signin.aliyun.com/saml-role/sso\" ID=\"_1fb520d4-8a5b-49dc-a2b6-e8c6d69ccc50\" IssueInstant=\"2022-10-27T10:00:39.480Z\" Version=\"2.0\"><saml2:Issuer xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">https://chengchao.name/springrun/welcome/welcome.html</saml2:Issuer><saml2p:Status><saml2p:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\"/></saml2p:Status><saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ID=\"_40df3ede-44fe-4225-ad3e-a77d5a665b24\" IssueInstant=\"2022-10-27T10:00:39.480Z\" Version=\"2.0\"><saml2:Issuer>https://chengchao.name/springrun/welcome/welcome.html</saml2:Issuer><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">\n"
                + "<ds:SignedInfo>\n"
                + "<ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>\n"
                + "<ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/>\n"
                + "<ds:Reference URI=\"#_40df3ede-44fe-4225-ad3e-a77d5a665b24\">\n"
                + "<ds:Transforms>\n"
                + "<ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/>\n"
                + "<ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"><ec:InclusiveNamespaces xmlns:ec=\"http://www.w3.org/2001/10/xml-exc-c14n#\" PrefixList=\"xsd\"/></ds:Transform>\n"
                + "</ds:Transforms>\n"
                + "<ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/>\n"
                + "<ds:DigestValue>AYr6Q8FGKlE2CEGYVm2RviLfwEYXf9ZvirZcL8tPjnQ=</ds:DigestValue>\n"
                + "</ds:Reference>\n"
                + "</ds:SignedInfo>\n"
                + "<ds:SignatureValue>\n"
                + "cdsdrq/iJSVSiTFHtOObxPDflHTVgrfLWShLpWWkcCNj6bQ/i674w5XIGVE3FGbJ2lj8v4u6aBRf\n"
                + "C74kRZBABlFu233i46k1nVa3QUmtzEJIvy9E0g==\n"
                + "</ds:SignatureValue>\n"
                + "</ds:Signature><saml2:Subject><saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress\">admin@example.name</saml2:NameID><saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:bearer\"><saml2:SubjectConfirmationData NotOnOrAfter=\"2022-10-29T10:00:39.494Z\" Recipient=\"https://signin.aliyun.com/saml-role/sso\"/></saml2:SubjectConfirmation></saml2:Subject><saml2:Conditions NotOnOrAfter=\"2022-10-29T10:00:39.513Z\"><saml2:AudienceRestriction><saml2:Audience>urn:alibaba:cloudcomputing</saml2:Audience></saml2:AudienceRestriction></saml2:Conditions><saml2:AuthnStatement AuthnInstant=\"2022-10-27T10:00:39.480Z\"><saml2:AuthnContext><saml2:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport</saml2:AuthnContextClassRef></saml2:AuthnContext></saml2:AuthnStatement><saml2:AttributeStatement><saml2:Attribute Name=\"https://www.aliyun.com/SAML-Role/Attributes/RoleSessionName\"><saml2:AttributeValue xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xsd:string\">admin@example.name</saml2:AttributeValue></saml2:Attribute><saml2:Attribute Name=\"https://www.aliyun.com/SAML-Role/Attributes/Role\"><saml2:AttributeValue xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xsd:string\">acs:ram::1764263140474643:role/super3,acs:ram::1764263140474643:saml-provider/chengchaoIDP</saml2:AttributeValue><saml2:AttributeValue xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"xsd:string\">acs:ram::1764263140474643:role/super2,acs:ram::1764263140474643:saml-provider/chengchaoIDP</saml2:AttributeValue></saml2:Attribute></saml2:AttributeStatement></saml2:Assertion></saml2p:Response>\n"
                + "";
        System.out.println("Pretty printing by Transformer");
        System.out.println("=============================================");
        System.out.println(prettyPrintByDom4j(xmlString, 8, false));
    }
}