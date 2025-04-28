package ninja.ranner.xogame.application.port;

import ninja.ranner.xogame.domain.Event;
import ninja.ranner.xogame.domain.Identifier;

import java.util.List;
import java.util.Optional;

public interface EventStore {
    void append(Identifier id, List<Event> events);

    Optional<List<Event>> findAllForId(Identifier gameId);

    List<Event> findAllForTypes(List<Class<? extends Event>> gameCreatedClass);
}
