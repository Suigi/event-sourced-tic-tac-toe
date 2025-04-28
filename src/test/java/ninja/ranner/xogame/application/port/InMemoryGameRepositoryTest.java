package ninja.ranner.xogame.application.port;

import ninja.ranner.xogame.domain.Cell;
import ninja.ranner.xogame.domain.Game;
import ninja.ranner.xogame.domain.GameId;
import ninja.ranner.xogame.domain.Player;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryGameRepositoryTest {

    @Test
    void savedGameCanBeFound() {
        InMemoryGameRepository inMemoryGameRepository = new InMemoryGameRepository(new InMemoryEventStore());
        GameId gameId = GameId.random();
        Game game = Game.create(gameId, "Game to save");

        inMemoryGameRepository.save(game);

        assertThat(inMemoryGameRepository.findById(gameId))
                .get()
                .extracting(Game::name)
                .isEqualTo("Game to save");
    }

    @Test
    void retrievedGameCanSaveUpdates() {
        InMemoryGameRepository inMemoryGameRepository = new InMemoryGameRepository(new InMemoryEventStore());
        GameId gameId = GameId.random();
        Game game = Game.create(gameId, "Game to save");
        inMemoryGameRepository.save(game);

        Game foundGame = inMemoryGameRepository.findById(gameId).orElseThrow();
        foundGame.fillCell(Cell.at(1, 1));
        inMemoryGameRepository.save(foundGame);

        Game updatedAndFoundGame = inMemoryGameRepository.findById(gameId).orElseThrow();
        assertThat(updatedAndFoundGame.name())
                .isEqualTo("Game to save");
        assertThat(updatedAndFoundGame.boardMap().get(Cell.at(1, 1)))
                .isEqualTo(Player.X);
    }

}