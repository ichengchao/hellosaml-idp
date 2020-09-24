package name.chengchao.hellosaml.common;

/**
 * 常量
 * 
 * @author charles
 * @date 2020-09-23
 */
public class CommonConstants {

    public static final String ALIYUN_IDENTIFIER = "urn:alibaba:cloudcomputing";
    public static final String ALIYUN_REPLY_URL = "https://signin.aliyun.com/saml-role/sso";

    
    //用户名: 一般是员工的邮箱
    public static final String ROLE_SESSION_NAME = "test11@chengchao.name";
//    public static final String ROLE_ARN = "acs:ram::1646992549003859:role/719SSOtest,acs:ram::1646992549003859:saml-provider/ARAAD";

    //acs:ram::{uid}:role/{rolename},acs:ram::{uid}:saml-provider/{idp_provider_name}
    public static final String ROLE_ARN = "acs:ram::1764263140474643:role/superadadmin,acs:ram::1764263140474643:saml-provider/superAD";
    public static final String ROLE_ARN_2 = "acs:ram::1764263140474643:role/super2,acs:ram::1764263140474643:saml-provider/superAD";

    // IDP_ENTITY_ID 就是oppo的唯一随机ID
    public static final String IDP_ENTITY_ID = "https://sts.windows.net/fc8f6afa-b8a7-4acf-b16b-b65d76ce4260/";
    public static final String Location = "https://login.microsoftonline.com/fc8f6afa-b8a7-4acf-b16b-b65d76ce4260/saml2";

    /**
     * openssl 生成 私钥时所使用的密码
     */
    public static final String PASSWORD = "111111";
    
    
//    # (中间会提示输入密码(重复输入两次)，要记住这个密码)
//    openssl genrsa -des3 -out server.key 2048
//    openssl req -new -x509 -key server.key -out server.crt -days 3650
//    openssl pkcs12 -export -out server.pfx -inkey server.key -in server.crt
//    openssl pkcs12 -in selfsigncert.pfx -nokeys -out test.pem
    
    
    
    
    

}
