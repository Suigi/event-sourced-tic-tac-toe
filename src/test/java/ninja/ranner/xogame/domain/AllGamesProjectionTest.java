package ninja.ranner.xogame.domain;

import ninja.ranner.xogame.application.AllGamesProjection;
import ninja.ranner.xogame.application.port.GameRepository;
import ninja.ranner.xogame.application.port.InMemoryEventStore;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AllGamesProjectionTest {

    @Test
    void containsCreatedGame() {
        Game game = Game.create(GameId.random(), "Some Game");
        InMemoryEventStore eventStore = new InMemoryEventStore();
        GameRepository gameRepository = new GameRepository(eventStore);
        gameRepository.save(game);

        AllGamesProjection allGamesProjection = AllGamesProjection.onDemand(eventStore);

        assertThat(allGamesProjection.games())
                .containsExactly(new AllGamesProjection.GameSummary(
                        game.id(),
                        "Some Game"
                ));
    }
}