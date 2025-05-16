package ninja.ranner.xogame.adapter.out.jdbc;

import ninja.ranner.xogame.domain.*;

import java.util.Map;
import java.util.stream.Collectors;

public class GameEventNameMapper implements JdbcEventStore.EventNameMapper {
    private final Map<Class<? extends Event>, String> eventToName = Map.of(
            GameCreated.class, "GameCreated",
            CellFilled.class, "CellFilled",
            GameDrawn.class, "GameDrawn",
            GameWon.class, "GameWon"
    );
    private final Map<String, Class<? extends Event>> eventNameToType = eventToName
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    private final Map<Class<? extends Identifier>, String> identifierToName = Map.of(
            GameId.class, "Game"
    );

    @Override
    public String eventNameFor(Class<? extends Event> eventClass) {
        String eventName = eventToName.get(eventClass);
        if (eventName == null) {
            throw new UnsupportedOperationException("Event class " + eventClass + " is not mapped");
        }
        return eventName;
    }

    @Override
    public Class<? extends Event> eventTypeFor(String eventName) {
        Class<? extends Event> eventType = eventNameToType.get(eventName);
        if (eventType == null) {
            throw new UnsupportedOperationException("Unknown Event Type '" + eventName + "'");
        }
        return eventType;
    }

    @Override
    public String aggregateNameFor(Class<? extends Identifier> identifierType) {
        String aggregateName = identifierToName.get(identifierType);
        if (aggregateName == null) {
            throw new UnsupportedOperationException("Unknown Identifier Type '" + identifierType + "'");
        }
        return aggregateName;
    }

    @Override
    public Class<?> identifierTypeFor(String aggregateName) {
        return null;
    }
}
