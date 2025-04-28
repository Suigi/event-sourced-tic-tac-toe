package ninja.ranner.xogame.adapter.in.web;

import ninja.ranner.xogame.InMemoryStoresTestConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(LobbyController.class)
@Tag("mvc")
@Import(InMemoryStoresTestConfiguration.class)
class LobbyControllerMvcTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Test
    void getLobby_returns200Ok() {
        MvcTestResult response = mockMvcTester.get()
                                              .uri("/")
                                              .exchange();

        assertThat(response)
                .hasViewName("lobby");
    }

}