package ninja.ranner.xogame.application.port;

import ninja.ranner.xogame.domain.Event;
import ninja.ranner.xogame.domain.Identifier;

import java.util.*;

public class InMemoryEventStore implements EventStore {
    final Map<Identifier, List<Event>> eventsById = new HashMap<>();
    final List<Event> allEvents = new ArrayList<>();

    public InMemoryEventStore() {
    }

    @Override
    public void append(Identifier id, List<Event> events) {
        eventsById.computeIfAbsent(id, (_) -> new ArrayList<>())
                  .addAll(events);
        allEvents.addAll(events);
    }

    @Override
    public Optional<List<Event>> findAllForId(Identifier gameId) {
        return Optional
                .ofNullable(eventsById.get(gameId));
    }

    @Override
    public final List<Event> findAllForTypes(List<Class<? extends Event>> eventClasses) {
        return allEvents.stream()
                        .filter(event -> eventClasses.contains(event.getClass()))
                        .toList();
    }

    public void clear() {
        eventsById.clear();
    }
}