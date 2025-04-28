package ninja.ranner.xogame.adapter.in.web;

import ninja.ranner.xogame.application.AllGamesProjection;
import ninja.ranner.xogame.application.port.GameRepository;
import ninja.ranner.xogame.application.port.InMemoryEventStore;
import ninja.ranner.xogame.domain.Game;
import ninja.ranner.xogame.domain.GameId;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;

import static org.assertj.core.api.Assertions.assertThat;

class LobbyControllerTest {

    @Test
    void lobbyReturnsLobbyView() {
        LobbyController lobbyController = new LobbyController(new InMemoryEventStore());

        String viewName = lobbyController.lobby(new ConcurrentModel());

        assertThat(viewName)
                .isEqualTo("lobby");
    }

    @Test
    void existingGameIsDisplayedInLobby() {
        Game game = Game.create(GameId.random(), "Existing Game");
        InMemoryEventStore eventStore = new InMemoryEventStore();
        GameRepository gameRepository = new GameRepository(eventStore);
        gameRepository.save(game);
        LobbyController lobbyController = new LobbyController(eventStore);

        ConcurrentModel model = new ConcurrentModel();
        lobbyController.lobby(model);

        assertThat(model)
                .extractingByKey("games", InstanceOfAssertFactories.list(AllGamesProjection.GameSummary.class))
                .extracting(AllGamesProjection.GameSummary::name)
                .containsExactly("Existing Game");
    }

}