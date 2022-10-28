package name.chengchao.hellosaml.idp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan("name.chengchao.hellosaml.idp")
public class IdPDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdPDemoApplication.class, args);
    }

}
