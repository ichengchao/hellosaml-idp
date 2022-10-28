package name.chengchao.hellosaml.idp.common;

public class CommonConstants {

    public static final String PUBLIC_KEY_PATH = "/Users/charles/config/saml.crt";
    public static final String PRIVATE_KEY_PATH = "/Users/charles/config/saml.pkcs8";

    // public static final String ROLE_SESSION_NAME = "admin@example.name";

    // IDP_ENTITY_ID 唯一ID,代表IDP
    public static final String IDP_ENTITY_ID = "https://chengchao.name/springrun/welcome/welcome.html";

    // ******************************************_aliyun_**************************************

    // role的list,格式如下注释
    // acs:ram::{uid}:role/{rolename},acs:ram::{uid}:saml-provider/{idp_provider_name}
    // public static final List<String> ROLE_LIST = Arrays.asList(
    // "acs:ram::1764263140474643:role/super3,acs:ram::1764263140474643:saml-provider/chengchaoIDP",
    // "acs:ram::1764263140474643:role/super2,acs:ram::1764263140474643:saml-provider/chengchaoIDP");

    // ******************************************_aws_*****************************************
    // public static final List<String> ROLE_LIST = Arrays.asList(
    // "arn:aws:iam::433312851566:role/myrole1,arn:aws:iam::433312851566:saml-provider/springrun");
    //
    // public static final String ALIYUN_IDENTIFIER = "urn:amazon:webservices";
    // public static final String ALIYUN_REPLY_URL = "https://signin.aws.amazon.com/saml";
    // public static final String ATTRIBUTE_KEY_ROLE_SESSION_NAME =
    // "https://aws.amazon.com/SAML/Attributes/RoleSessionName";
    // public static final String ATTRIBUTE_KEY_ROLE = "https://aws.amazon.com/SAML/Attributes/Role";

}
