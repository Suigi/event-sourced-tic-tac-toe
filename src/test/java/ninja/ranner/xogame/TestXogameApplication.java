package ninja.ranner.xogame;

import org.springframework.boot.SpringApplication;

public class TestXogameApplication {

    public static void main(String[] args) {
        SpringApplication.from(XoGameApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
