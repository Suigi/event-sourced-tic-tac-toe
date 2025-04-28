package ninja.ranner.xogame.domain;

import ninja.ranner.xogame.application.OpenGamesProjection;
import ninja.ranner.xogame.application.port.InMemoryEventStore;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OpenGamesProjectionTest {

    @Test
    void containsCreatedGame() {
        InMemoryEventStore eventStore = new InMemoryEventStore();
        GameId openGameId = GameId.random();
        eventStore.append(openGameId, List.of(
                new GameCreated(openGameId, "Open Game")
        ));

        OpenGamesProjection openGamesProjection = OpenGamesProjection.onDemand(eventStore);

        assertThat(openGamesProjection.games())
                .containsExactly(new OpenGamesProjection.GameSummary(
                        openGameId,
                        "Open Game"
                ));
    }

    @Test
    void doesNotContainFinishedGame() {
        InMemoryEventStore eventStore = new InMemoryEventStore();
        GameId openGameId = GameId.random();
        eventStore.append(openGameId, List.of(
                new GameCreated(openGameId, "Open Game")
        ));
        GameId drawnGameId = GameId.random();
        eventStore.append(drawnGameId, List.of(
                new GameCreated(drawnGameId, "Drawn Game"),
                new GameDrawn(drawnGameId)
        ));
        GameId gameWonByX = GameId.random();
        eventStore.append(gameWonByX, List.of(
                new GameCreated(gameWonByX, "Game Won by X"),
                new GameWon(gameWonByX, Player.X)
        ));

        OpenGamesProjection openGamesProjection = OpenGamesProjection.onDemand(eventStore);

        assertThat(openGamesProjection.games())
                .extracting(OpenGamesProjection.GameSummary::name)
                .containsExactly("Open Game");
    }
}