package ninja.ranner.xogame.application.port;

import ninja.ranner.xogame.domain.Event;
import ninja.ranner.xogame.domain.Game;
import ninja.ranner.xogame.domain.GameId;

import java.util.List;
import java.util.Optional;

public class InMemoryGameRepository implements GameRepository {

    private final EventStore eventStore;

    public InMemoryGameRepository(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public Game save(Game game) {
        List<Event> events = game.uncommittedEvents().toList();
        GameId id = game.id();
        eventStore.append(id, events);

        return findById(game.id()).orElseThrow();
    }

    @Override
    public Optional<Game> findById(GameId gameId) {
        return eventStore.findAllForId(gameId).map(Game::reconstitute);
    }

}
