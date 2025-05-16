package ninja.ranner.xogame.application;

import ninja.ranner.xogame.application.port.EventStore;
import ninja.ranner.xogame.domain.*;

import java.util.ArrayList;
import java.util.List;

public class OpenGamesProjection {
    private final List<GameSummary> games = new ArrayList<>();

    public record GameSummary(GameId id, String name) {}

    public static OpenGamesProjection onDemand(EventStore eventStore) {
        OpenGamesProjection projection = new OpenGamesProjection();
        eventStore.findAllForTypes(List.of(
                          GameCreated.class,
                          GameDrawn.class,
                          GameWon.class
                  ))
                  .forEach(projection::apply);
        return projection;
    }

    public List<GameSummary> games() {
        return games;
    }

    private void apply(Event event) {
        switch (event) {
            case GameCreated(GameId gameId, String newGameName) -> add(gameId, newGameName);
            case GameDrawn(GameId gameId) -> removeById(gameId);
            case GameWon(GameId gameId, _) -> removeById(gameId);
            default -> {}
        }
    }

    private void add(GameId gameId, String newGameName) {
        games.add(new GameSummary(gameId, newGameName));
    }

    private void removeById(GameId gameId) {
        games.removeIf(summary -> summary.id.equals(gameId));
    }
}
