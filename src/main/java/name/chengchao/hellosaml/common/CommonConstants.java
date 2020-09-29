package name.chengchao.hellosaml.common;

import java.util.Arrays;
import java.util.List;

/**
 * 生成自签名证书的步骤<br>
 * #create the keypair <br>
 * openssl req -new -x509 -days 3652 -nodes -out saml.crt -keyout saml.pem <br>
 * #convert the private key to pkcs8 format <br>
 * openssl pkcs8 -topk8 -inform PEM -outform DER -in saml.pem -out saml.pkcs8
 * -nocrypt <br>
 */
public class CommonConstants {

    public static final String PUBLIC_KEY_FILE_PATH = "/Users/charles/test/saml.crt";
    public static final String PRIVATE_KEY_FILE_PATH = "/Users/charles/test/saml.pkcs8";

    public static final String ALIYUN_IDENTIFIER = "urn:alibaba:cloudcomputing";
    public static final String ALIYUN_REPLY_URL = "https://signin.aliyun.com/saml-role/sso";

    public static final String ATTRIBUTE_KEY_ROLE_SESSION_NAME = "https://www.aliyun.com/SAML-Role/Attributes/RoleSessionName";
    public static final String ATTRIBUTE_KEY_ROLE = "https://www.aliyun.com/SAML-Role/Attributes/Role";

    // 用户名: 一般是员工的邮箱
    public static final String ROLE_SESSION_NAME = "admin@chengchao.name";

    // acs:ram::{uid}:role/{rolename},acs:ram::{uid}:saml-provider/{idp_provider_name}
    public static final List<String> ROLE_LIST = Arrays.asList(
            "acs:ram::1764263140474643:role/superadadmin,acs:ram::1764263140474643:saml-provider/superAD",
            "acs:ram::1764263140474643:role/super3,acs:ram::1764263140474643:saml-provider/superAD",
            "acs:ram::1764263140474643:role/super2,acs:ram::1764263140474643:saml-provider/superAD");

    // IDP_ENTITY_ID 就是oppo的唯一随机ID
    public static final String IDP_ENTITY_ID = "https://chengchao.name/b65d76ce4260/";

}
