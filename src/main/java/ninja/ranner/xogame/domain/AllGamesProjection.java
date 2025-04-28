package ninja.ranner.xogame.domain;

import java.util.ArrayList;
import java.util.List;

public class AllGamesProjection {
    private final List<GameSummary> games = new ArrayList<>();

    public void apply(Event event) {
        if (event instanceof GameCreated(GameId gameId, String newGameName)) {
            games.add(new GameSummary(
                    gameId.uuid().toString(),
                    newGameName));
        }
    }

    public List<GameSummary> games() {
        return games;
    }

    public record GameSummary(String gameId, String name) {}
}
