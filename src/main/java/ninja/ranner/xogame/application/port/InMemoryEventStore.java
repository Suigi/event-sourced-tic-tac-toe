package ninja.ranner.xogame.application.port;

import ninja.ranner.xogame.domain.Event;
import ninja.ranner.xogame.domain.GameCreated;
import ninja.ranner.xogame.domain.Identifier;

import java.util.*;

public class InMemoryEventStore implements EventStore {
    final Map<Identifier, List<Event>> store = new HashMap<>();

    public InMemoryEventStore() {
    }

    @Override
    public void append(Identifier id, List<Event> events) {
        store.computeIfAbsent(id, (_) -> new ArrayList<>())
             .addAll(events);
    }

    @Override
    public Optional<List<Event>> findAllForId(Identifier gameId) {
        return Optional
                .ofNullable(store.get(gameId));
    }

    @Override
    public List<Event> findAllForType(Class<GameCreated> gameCreatedClass) {
        return store.values().stream()
                    .flatMap(Collection::stream)
                    .filter(event -> event.getClass().equals(gameCreatedClass))
                    .toList();
    }

    public void clear() {
        store.clear();
    }
}