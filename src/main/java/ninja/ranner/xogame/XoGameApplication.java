package ninja.ranner.xogame;

import ninja.ranner.xogame.spring.XoGameConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
public class XoGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(XoGameApplication.class, args);
    }

}
