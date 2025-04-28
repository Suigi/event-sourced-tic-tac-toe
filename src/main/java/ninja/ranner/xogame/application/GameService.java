package ninja.ranner.xogame.application;

import ninja.ranner.xogame.application.port.GameIdGenerator;
import ninja.ranner.xogame.application.port.GameRepository;
import ninja.ranner.xogame.domain.Cell;
import ninja.ranner.xogame.domain.Game;
import ninja.ranner.xogame.domain.GameId;

import java.util.Optional;

public class GameService {
    private final GameRepository gameRepository;
    private final GameIdGenerator gameIdGenerator;

    public GameService(GameRepository gameRepository, GameIdGenerator gameIdGenerator) {
        this.gameRepository = gameRepository;
        this.gameIdGenerator = gameIdGenerator;
    }

    public static GameService createForTest(GameRepository gameRepository) {
        return new GameService(gameRepository, GameId::random);
    }

    public Game fill(Game game, Cell cell) {
        game.fillCell(cell);
        return gameRepository.save(game);
    }

    public Game create(String name) {
        Game game = Game.create(gameIdGenerator.generate(), name);
        return gameRepository.save(game);
    }

    public Optional<Game> find(GameId gameId) {
        return gameRepository.findById(gameId);
    }
}
