package ninja.ranner.xogame.adapter.in.web;

import ninja.ranner.xogame.InMemoryStoresTestConfiguration;
import ninja.ranner.xogame.application.port.ConfigurableGameIdGenerator;
import ninja.ranner.xogame.application.port.GameRepository;
import ninja.ranner.xogame.application.port.InMemoryEventStore;
import ninja.ranner.xogame.domain.Game;
import ninja.ranner.xogame.domain.GameId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private GameRepository gameRepository;

    @Autowired
    private InMemoryEventStore eventStore;

    @BeforeEach
    void setUp() {
        eventStore.clear();
    }

    @Test
    void getGame_returnsGameView() {
        GameId gameId = GameId.random();
        gameRepository.save(Game.create(gameId, "IRRELEVANT GAME NAME"));

        MvcTestResult result = mvcTester.get()
                                        .uri("/games/{gameId}", gameId.uuid().toString())
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

    @Test
    void postFill_whenGameDoesNotExist_returns404NotFound() {
        String notFoundGameUuid = UUID.randomUUID().toString();

        MvcTestResult result = mvcTester.post()
                                        .uri("/games/{gameId}/fill", notFoundGameUuid)
                                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                        .param("x", "1")
                                        .param("y", "2")
                                        .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void postFill_returns200Ok() {
        GameId gameId = GameId.random();
        gameRepository.save(Game.create(gameId, "IRRELEVANT GAME NAME"));

        MvcTestResult result = mvcTester.post()
                                        .uri("/games/{gameId}/fill", gameId.uuid().toString())
                                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                        .param("x", "1")
                                        .param("y", "2")
                                        .header("hx-request", "true")
                                        .exchange();

        assertThat(result)
                .hasStatusOk();
    }

    @Test
    void postGame_redirectsToCreatedGame(@Autowired ConfigurableGameIdGenerator gameIdGenerator) {
        GameId newGameId = GameId.random();
        gameIdGenerator.configure(newGameId);
        MvcTestResult result = mvcTester.post()
                                        .uri("/games")
                                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                        .param("gameName", "New Game Name")
                                        .exchange();

        assertThat(result)
                .hasStatus3xxRedirection()
                .hasRedirectedUrl("/games/" + newGameId.uuid().toString());
    }
}