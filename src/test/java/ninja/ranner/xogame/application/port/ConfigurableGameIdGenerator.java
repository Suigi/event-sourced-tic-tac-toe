package ninja.ranner.xogame.application.port;

import ninja.ranner.xogame.domain.GameId;

public class ConfigurableGameIdGenerator implements GameIdGenerator {
    private GameId gameId;

    public void configure(GameId gameId) {
        this.gameId = gameId;
    }

    @Override
    public GameId generate() {
        return gameId;
    }
}
