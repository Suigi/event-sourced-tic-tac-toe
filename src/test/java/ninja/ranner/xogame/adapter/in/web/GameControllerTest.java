package ninja.ranner.xogame.adapter.in.web;

import ninja.ranner.xogame.application.port.GameRepository;
import ninja.ranner.xogame.application.port.InMemoryGameRepository;
import ninja.ranner.xogame.domain.Game;
import ninja.ranner.xogame.domain.GameId;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;

import static org.assertj.core.api.Assertions.assertThat;

class GameControllerTest {

    @Test
    void loadsGameFromRepository() {
        GameRepository gameRepository = new InMemoryGameRepository();
        GameController gameController = new GameController(gameRepository);
        ConcurrentModel model = new ConcurrentModel();
        GameId gameId = GameId.random();
        Game game = Game.create(gameId, "Game Name");
        gameRepository.save(game);

        gameController.game(gameId.id().toString(), model);

        assertThat(model)
                .extracting("game", InstanceOfAssertFactories.type(GameController.GameView.class))
                .isEqualTo(new GameController.GameView("Game Name"));
    }
}