package ninja.ranner.xogame.application;

import ninja.ranner.xogame.application.port.EventStore;
import ninja.ranner.xogame.domain.Event;
import ninja.ranner.xogame.domain.GameCreated;
import ninja.ranner.xogame.domain.GameId;

import java.util.ArrayList;
import java.util.List;

public class AllGamesProjection {
    private final List<GameSummary> games = new ArrayList<>();

    public static AllGamesProjection onDemand(EventStore eventStore) {
        AllGamesProjection projection = new AllGamesProjection();
        eventStore.findAllForType(GameCreated.class).forEach(projection::apply);
        return projection;
    }

    public void apply(Event event) {
        if (event instanceof GameCreated(GameId gameId, String newGameName)) {
            games.add(new GameSummary(
                    gameId,
                    newGameName));
        }
    }

    public List<GameSummary> games() {
        return games;
    }

    public record GameSummary(GameId id, String name) {}
}
