package ninja.ranner.xogame.adapter.in.web;

import ninja.ranner.xogame.InMemoryStoresTestConfiguration;
import ninja.ranner.xogame.application.port.InMemoryGameRepository;
import ninja.ranner.xogame.domain.Game;
import ninja.ranner.xogame.domain.GameId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("mvc")
@WebMvcTest(GameController.class)
@Import(InMemoryStoresTestConfiguration.class)
class GameControllerMvcTest {

    @Autowired
    private MockMvcTester mvcTester;

    @Autowired
    private InMemoryGameRepository gameRepository;

    @BeforeEach
    void setUp() {
        gameRepository.clear();
    }

    @Test
    void getGame_returnsViewName() {
        GameId gameId = GameId.random();
        gameRepository.save(Game.create(gameId, "IRRELEVANT GAME NAME"));

        MvcTestResult result = mvcTester.get()
                                        .uri("/games/{gameId}", gameId.id().toString())
                                        .exchange();

        assertThat(result)
                .hasStatusOk()
                .hasViewName("game")
                .model().containsKey("game");
    }

    @Test
    void getGame_whenGameIdDoesNotExist_returns404NotFound() {
        String notFoundGameUuid = UUID.randomUUID().toString();

        MvcTestResult result = mvcTester.get()
                                        .uri("/games/{gameId}", notFoundGameUuid)
                                        .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND);
    }
}