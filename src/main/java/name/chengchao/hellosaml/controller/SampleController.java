package name.chengchao.hellosaml.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import name.chengchao.hellosaml.common.CommonConstants;
import name.chengchao.hellosaml.util.SamlGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SampleController {

    private Logger logger = LoggerFactory.getLogger(SampleController.class);

    @RequestMapping("/")
    @ResponseBody
    String root() {
        return "Hello World! @" + new Date();
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

            // 参考 templates/saml2-post-binding.vm
            String onloadSubmit = "";
            if (CommonConstants.SSO_FORM_AUTO_SUBMIT) {
                onloadSubmit = "onload=\"document.forms[0].submit()\"";
            }

            HashMap<String, List<String>> attributes = new HashMap<String, List<String>>();
            attributes.put(CommonConstants.ATTRIBUTE_KEY_ROLE, CommonConstants.ROLE_LIST);
            List<String> sessionNameList = new ArrayList<String>();
            sessionNameList.add(CommonConstants.ROLE_SESSION_NAME);
            attributes.put(CommonConstants.ATTRIBUTE_KEY_ROLE_SESSION_NAME, sessionNameList);

            String samlResponse = SamlGenerator.generateResponse(CommonConstants.ALIYUN_IDENTIFIER,
                    CommonConstants.ALIYUN_REPLY_URL, "wang@chengchao.name", attributes);
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("<!DOCTYPE html><html><head><meta charset=\"utf-8\" /></head><body "
                    + onloadSubmit + "><form action=\"" + CommonConstants.ALIYUN_REPLY_URL
                    + "\" method=\"post\"><div><textarea name=\"SAMLResponse\">" + samlResponse
                    + "</textarea></div><div><input type=\"submit\" value=\"Continue\" /></div></form></body></html>");
            response.getWriter().flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

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
