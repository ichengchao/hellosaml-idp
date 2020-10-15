package name.chengchao.hellosaml.common;

import java.util.Arrays;
import java.util.List;

public class CommonConstants {
    // 是否自动提交表单,默认为false,方便调试
    public static final boolean SSO_FORM_AUTO_SUBMIT = false;

    public static final String PUBLIC_KEY_PATH = "/Users/charles/test/saml.crt";
    public static final String PRIVATE_KEY_PATH = "/Users/charles/test/saml.pkcs8";

    // 用户名: 一般是员工的邮箱
    public static final String ROLE_SESSION_NAME = "admin@example.name";

    // IDP_ENTITY_ID 唯一ID,代表IDP
    public static final String IDP_ENTITY_ID = "https://chengchao.name/b65d76ce4260/";

    // ******************************************_aliyun_**************************************

    // role的list,格式如下注释
    // acs:ram::{uid}:role/{rolename},acs:ram::{uid}:saml-provider/{idp_provider_name}
    public static final List<String> ROLE_LIST = Arrays.asList(
            "acs:ram::1764263140474643:role/super3,acs:ram::1764263140474643:saml-provider/superAD",
            "acs:ram::1764263140474643:role/super2,acs:ram::1764263140474643:saml-provider/superAD");

    /**
     * 以下内容不需要修改
     */
    public static final String ALIYUN_IDENTIFIER = "urn:alibaba:cloudcomputing";
    public static final String ALIYUN_REPLY_URL = "https://signin.aliyun.com/saml-role/sso";
    public static final String ATTRIBUTE_KEY_ROLE_SESSION_NAME = "https://www.aliyun.com/SAML-Role/Attributes/RoleSessionName";
    public static final String ATTRIBUTE_KEY_ROLE = "https://www.aliyun.com/SAML-Role/Attributes/Role";

    // ******************************************_aws_*****************************************
//    public static final List<String> ROLE_LIST = Arrays.asList(
//            "arn:aws:iam::433312851566:role/myrole1,arn:aws:iam::433312851566:saml-provider/springrun");
//
//    public static final String ALIYUN_IDENTIFIER = "urn:amazon:webservices";
//    public static final String ALIYUN_REPLY_URL = "https://signin.aws.amazon.com/saml";
//    public static final String ATTRIBUTE_KEY_ROLE_SESSION_NAME = "https://aws.amazon.com/SAML/Attributes/RoleSessionName";
//    public static final String ATTRIBUTE_KEY_ROLE = "https://aws.amazon.com/SAML/Attributes/Role";

}
