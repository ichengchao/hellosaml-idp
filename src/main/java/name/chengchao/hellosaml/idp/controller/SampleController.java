package name.chengchao.hellosaml.idp.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import name.chengchao.hellosaml.idp.util.SamlGenerator;
import name.chengchao.hellosaml.idp.util.XmlFormatter;

@Controller
public class SampleController {

    private Logger logger = LoggerFactory.getLogger(SampleController.class);

    // *************************************User SSO**********************************************
    public static final String ALIYUN_IDENTIFIER_USERSSO = "https://signin.aliyun.com/%s/saml/SSO";
    public static final String ALIYUN_REPLY_URL_USERSSO = "https://signin.aliyun.com/saml/SSO";
    // *******************************************************************************************
    // *************************************Role SSO**********************************************
    public static final String ALIYUN_IDENTIFIER_ROLESSO = "urn:alibaba:cloudcomputing";
    public static final String ALIYUN_REPLY_URL_ROLESSO = "https://signin.aliyun.com/saml-role/sso";

    public static final String ATTRIBUTE_KEY_ROLE_SESSION_NAME =
        "https://www.aliyun.com/SAML-Role/Attributes/RoleSessionName";
    public static final String ATTRIBUTE_KEY_ROLE = "https://www.aliyun.com/SAML-Role/Attributes/Role";
    // *******************************************************************************************

    @RequestMapping("/")
    public RedirectView root() {
        return new RedirectView("index.html");
    }

    /**
     * 测试sso
     * 
     * @param request
     * @param response
     */
    @RequestMapping("/login")
    public void login(HttpServletRequest request, HttpServletResponse response) {
        try {

            String samlResponse = "";
            String postURL = "";
            String type = request.getParameter("type");
            if ("aliyun_rolesso".equals(type)) {
                String uid = request.getParameter("uid");
                String roleName = request.getParameter("roleName");
                String sessionName = request.getParameter("sessionName");
                String provider = request.getParameter("provider");
                samlResponse = buildRoleSSOSamlResponse(uid, roleName, sessionName, provider);
                postURL = ALIYUN_REPLY_URL_ROLESSO;
            } else if ("aliyun_usersso".equals(type)) {
                String uid = request.getParameter("uid");
                String ramUsername = request.getParameter("ramUsername");
                samlResponse = buildUserSSOSamlResponse(uid, ramUsername);
                postURL = ALIYUN_REPLY_URL_USERSSO;
                //postURL = "http://localhost:8088/sp.do";
            } else {
                throw new RuntimeException("not support type:" + type);
            }

            // 返回跳转页面
            response.setContentType("text/html;charset=UTF-8");

            String body = """
                <!DOCTYPE html>
                <html>

                <head>
                    <meta charset="utf-8" />
                </head>

                <body onload="document.forms[0].submit()">
                    <h1>loading...</h1>
                    <form style="visibility: hidden;" action="%s" method="post">
                        <div><textarea name="SAMLResponse">%s</textarea></div>
                        <div><input type="submit" value="Continue" /></div>
                    </form>
                </body>
                </html>
                   """;

            response.getWriter().write(body.formatted(postURL, samlResponse));
            response.getWriter().flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    @RequestMapping("/getSamlResponse")
    public void getSamlResponse(HttpServletRequest request, HttpServletResponse response) {
        try {
            String samlResponse = "";
            String type = request.getParameter("type");
            if ("aliyun_rolesso".equals(type)) {
                String uid = request.getParameter("uid");
                String roleName = request.getParameter("roleName");
                String sessionName = request.getParameter("sessionName");
                String provider = request.getParameter("provider");
                samlResponse = buildRoleSSOSamlResponse(uid, roleName, sessionName, provider);
            } else if ("aliyun_usersso".equals(type)) {
                String uid = request.getParameter("uid");
                String ramUsername = request.getParameter("ramUsername");
                samlResponse = buildUserSSOSamlResponse(uid, ramUsername);
            } else {
                throw new RuntimeException("not support type:" + type);
            }
            String samlXML = new String(java.util.Base64.getDecoder().decode(samlResponse), StandardCharsets.UTF_8);

            samlXML = XmlFormatter.prettyPrintByDom4j(samlXML, 8, false);

            response.getWriter().write(samlXML);
            response.getWriter().flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    private String buildRoleSSOSamlResponse(String uid, String roleName, String sessionName, String provider)
        throws Exception {
        List<String> roleList = new ArrayList<String>();
        // acs:ram::1764263140474643:role/superadadmin,acs:ram::1764263140474643:saml-provider/superAD
        roleList.add("acs:ram::" + uid + ":role/" + roleName + ",acs:ram::" + uid + ":saml-provider/" + provider);

        // 构建saml的attributes
        HashMap<String, List<String>> attributes = new HashMap<>();
        attributes.put(ATTRIBUTE_KEY_ROLE, roleList);
        attributes.put(ATTRIBUTE_KEY_ROLE_SESSION_NAME, List.of(sessionName));

        String samlResponse = SamlGenerator.generateResponse(ALIYUN_IDENTIFIER_ROLESSO,
            ALIYUN_REPLY_URL_ROLESSO, sessionName, attributes);
        return samlResponse;
    }

    private String buildUserSSOSamlResponse(String uid, String ramUsername)
        throws Exception {
        String samlResponse = SamlGenerator.generateResponse(ALIYUN_IDENTIFIER_USERSSO.formatted(uid),
            ALIYUN_REPLY_URL_USERSSO, ramUsername, null);
        return samlResponse;
    }

    /**
     * 下载meta.xml
     * 
     * @param request
     * @param response
     */
    @RequestMapping("/metaxml")
    public void metaxml(HttpServletRequest request, HttpServletResponse response) {
        try {
            String metaxml = SamlGenerator.generateMetaXML();
            response.setContentType("application/xml;charset=UTF-8");
            response.getWriter().write(metaxml);
            response.getWriter().flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

}
