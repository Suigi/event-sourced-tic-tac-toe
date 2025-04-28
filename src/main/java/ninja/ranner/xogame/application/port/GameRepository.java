package ninja.ranner.xogame.application.port;

import ninja.ranner.xogame.domain.Game;

public interface GameRepository {
    Game save(Game game);
}
