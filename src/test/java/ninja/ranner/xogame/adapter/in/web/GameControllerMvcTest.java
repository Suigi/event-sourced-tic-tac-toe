package ninja.ranner.xogame.adapter.in.web;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("mvc")
@WebMvcTest(GameController.class)
class GameControllerMvcTest {

    @Autowired
    private MockMvcTester mvcTester;

    @Test
    void getGame_returnsViewName() {
        String gameUuidString = UUID.randomUUID().toString();

        MvcTestResult result = mvcTester.get()
                                        .uri("/games/{gameId}", gameUuidString)
                                        .exchange();

        assertThat(result)
                .hasStatusOk()
                .hasViewName("game")
                .model().containsKey("game");
    }

}