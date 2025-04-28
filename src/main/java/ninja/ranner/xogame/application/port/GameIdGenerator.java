package ninja.ranner.xogame.application.port;

import ninja.ranner.xogame.domain.GameId;

@FunctionalInterface
public interface GameIdGenerator {
    GameId generate();
}
