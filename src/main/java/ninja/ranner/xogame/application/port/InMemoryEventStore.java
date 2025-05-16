package ninja.ranner.xogame.application.port;

import ninja.ranner.xogame.adapter.out.jdbc.GameEventNameMapper;
import ninja.ranner.xogame.adapter.out.jdbc.JsonEventSerializer;
import ninja.ranner.xogame.domain.Event;
import ninja.ranner.xogame.domain.Identifier;

import java.util.*;

public class InMemoryEventStore implements EventStore {
    final Map<Identifier, List<EventDbo>> eventsById = new HashMap<>();
    final List<EventDbo> allEvents = new ArrayList<>();

    public InMemoryEventStore() {
    }

    @Override
    public void append(Identifier id, List<Event> events) {
        List<EventDbo> dbos = events.stream().map(EventDbo::of).toList();
        eventsById.computeIfAbsent(id, (_) -> new ArrayList<>())
                  .addAll(dbos);
        allEvents.addAll(dbos);
    }

    @Override
    public Optional<List<Event>> findAllForId(Identifier gameId) {
        List<EventDbo> eventDbos = eventsById.get(gameId);
        if (eventDbos == null) {
            return Optional.empty();
        }

        return Optional.of(eventDbos.stream()
                                    .map(EventDbo::toDomain)
                                    .toList());
    }

    @Override
    public final List<Event> findAllForTypes(List<Class<? extends Event>> eventClasses) {
        return allEvents.stream()
                        .map(EventDbo::toDomain)
                        .filter(event -> eventClasses.contains(event.getClass()))
                        .toList();
    }

    public void clear() {
        eventsById.clear();
    }

    record EventDbo(String eventType, String json) {
        public static EventDbo of(Event event) {
            String json = new JsonEventSerializer().serialize(event);
            GameEventNameMapper mapper = new GameEventNameMapper();
            return new EventDbo(mapper.eventNameFor(event), json);
        }

        public Event toDomain() {
            JsonEventSerializer jsonEventSerializer = new JsonEventSerializer();
            GameEventNameMapper mapper = new GameEventNameMapper();
            return jsonEventSerializer.deserialize(mapper.eventTypeFor(eventType), json);
        }
    }
}