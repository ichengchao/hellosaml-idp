package name.chengchao.hellosaml.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import name.chengchao.hellosaml.util.OpenSamlImplementation;
import name.chengchao.hellosaml.util.SAMLHelper;
import org.opensaml.saml.saml2.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SampleController {

    private Logger logger = LoggerFactory.getLogger(SampleController.class);

    @Autowired
    private SAMLHelper samlHelper;

    @RequestMapping("/")
    @ResponseBody
    String root() {
        return "Hello World! @" + new Date();
    }

    @RequestMapping("/test")
    public void test(HttpServletRequest request, HttpServletResponse response) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(Thread.currentThread());
            response.getWriter().write(sb.toString());
            response.flushBuffer();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @RequestMapping("/login")
    public void login(HttpServletRequest request, HttpServletResponse response) {
        try {
            Response samlResponse = samlHelper.buildResponse(OpenSamlImplementation.generateSecureRandomId());
            samlHelper.httpPostBinding(null, response, "https://signin.aliyun.com/saml-role/sso", samlResponse);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

}
