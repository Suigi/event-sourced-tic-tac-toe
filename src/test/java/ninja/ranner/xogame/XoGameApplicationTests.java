package ninja.ranner.xogame;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import({
        TestcontainersConfiguration.class,
//        InMemoryStoresTestConfiguration.class
})
@SpringBootTest
@Tag("spring")
class XoGameApplicationTests {

    @Test
    void contextLoads() {
    }

}
