package ninja.ranner.xogame.application.port;

import ninja.ranner.xogame.domain.Game;
import ninja.ranner.xogame.domain.GameId;

import java.util.Optional;

public interface GameRepository {
    Game save(Game game);

    Optional<Game> findById(GameId gameId);
}
