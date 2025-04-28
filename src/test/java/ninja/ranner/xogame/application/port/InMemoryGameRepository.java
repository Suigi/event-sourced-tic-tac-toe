package ninja.ranner.xogame.application.port;

import ninja.ranner.xogame.domain.Event;
import ninja.ranner.xogame.domain.Game;
import ninja.ranner.xogame.domain.GameId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryGameRepository implements GameRepository {

    private final Map<GameId, List<Event>> store = new HashMap<>();

    @Override
    public Game save(Game game) {
        store.put(game.id(), game.uncommittedEvents().toList());
        return findById(game.id()).orElseThrow();
    }

    @Override
    public Optional<Game> findById(GameId gameId) {
        return Optional
                .ofNullable(store.get(gameId))
                .map(Game::reconstitute);
    }

    public void clear() {
        store.clear();
    }
}
