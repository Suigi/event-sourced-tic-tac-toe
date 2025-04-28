package ninja.ranner.xogame.application.port;

import ninja.ranner.xogame.domain.Cell;
import ninja.ranner.xogame.domain.Game;
import ninja.ranner.xogame.domain.GameId;
import ninja.ranner.xogame.domain.Player;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameRepositoryTest {

    @Test
    void savedGameCanBeFound() {
        GameRepository gameRepository = new GameRepository(new InMemoryEventStore());
        GameId gameId = GameId.random();
        Game game = Game.create(gameId, "Game to save");

        gameRepository.save(game);

        assertThat(gameRepository.findById(gameId))
                .get()
                .extracting(Game::name)
                .isEqualTo("Game to save");
    }

    @Test
    void retrievedGameCanSaveUpdates() {
        GameRepository gameRepository = new GameRepository(new InMemoryEventStore());
        GameId gameId = GameId.random();
        Game game = Game.create(gameId, "Game to save");
        gameRepository.save(game);

        Game foundGame = gameRepository.findById(gameId).orElseThrow();
        foundGame.fillCell(Cell.at(1, 1));
        gameRepository.save(foundGame);

        Game updatedAndFoundGame = gameRepository.findById(gameId).orElseThrow();
        assertThat(updatedAndFoundGame.name())
                .isEqualTo("Game to save");
        assertThat(updatedAndFoundGame.boardMap().get(Cell.at(1, 1)))
                .isEqualTo(Player.X);
    }

}