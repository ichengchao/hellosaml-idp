package name.chengchao.hellosaml.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @RequestMapping("/login")
    public void login(HttpServletRequest request, HttpServletResponse response) {
        try {
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

}
