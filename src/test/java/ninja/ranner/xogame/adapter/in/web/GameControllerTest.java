package ninja.ranner.xogame.adapter.in.web;

import ninja.ranner.xogame.application.port.GameRepository;
import ninja.ranner.xogame.application.port.InMemoryGameRepository;
import ninja.ranner.xogame.domain.Game;
import ninja.ranner.xogame.domain.GameId;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GameControllerTest {

    @Test
    @Disabled
    void loadsGameFromRepository() {
        String gameIdString = UUID.randomUUID().toString();
        GameController gameController = new GameController();
        ConcurrentModel model = new ConcurrentModel();
        Game game = Game.create(GameId.random(), "Game Name");
        GameRepository gameRepository = new InMemoryGameRepository();
        gameRepository.save(game);

        gameController.game(gameIdString, model);

        assertThat(model)
                .extracting("game", InstanceOfAssertFactories.type(GameController.GameView.class))
                .isEqualTo(new GameController.GameView("Game Name"));
    }
}