package ninja.ranner.xogame.adapter.out.jdbc;

import ninja.ranner.xogame.domain.*;

import java.util.Map;

public class GameEventNameMapper implements JdbcEventStore.EventNameMapper {
    private final Map<Class<? extends Event>, String> eventToName = Map.of(
            GameCreated.class, "GameCreated",
            CellFilled.class, "CellFilled",
            GameDrawn.class, "GameDrawn",
            GameWon.class, "GameWon"
    );

    @Override
    public String aggregateNameFor(Identifier identifier) {
        return "";
    }

    @Override
    public String eventNameFor(Class<? extends Event> eventClass) {
        String eventName = eventToName.get(eventClass);
        if (eventName == null) {
            throw new UnsupportedOperationException("Event class " + eventClass + " is not mapped");
        }
        return eventName;
    }

    @Override
    public Class<?> identifierTypeFor(String aggregateName) {
        return null;
    }

    @Override
    public Class<?> eventTypeFor(String eventName) {
        return null;
    }
}
