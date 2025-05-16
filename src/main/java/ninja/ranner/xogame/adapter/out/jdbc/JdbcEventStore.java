package ninja.ranner.xogame.adapter.out.jdbc;

import jakarta.annotation.Nonnull;
import ninja.ranner.xogame.application.port.EventStore;
import ninja.ranner.xogame.domain.Event;
import ninja.ranner.xogame.domain.Identifier;
import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class JdbcEventStore implements EventStore {

    private final EventNameMapper eventNameMapper;
    private final JsonEventSerializer eventSerializer = new JsonEventSerializer();

    public interface EventNameMapper {
        String aggregateNameFor(Class<? extends Identifier> identifierType);

        default String aggregateNameFor(Identifier identifier) {
            return aggregateNameFor(identifier.getClass());
        }

        String eventNameFor(Class<? extends Event> eventClass);

        default String eventNameFor(Event event) {
            return eventNameFor(event.getClass());
        }

        Class<? extends Event> eventTypeFor(String eventName);
    }

    public interface EventSerializer {
        String serialize(Event event);
        Event deserialize(Class<? extends Event> eventType, String json);
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcEventStore(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                          EventNameMapper eventNameMapper) {
        this.eventNameMapper = eventNameMapper;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void append(Identifier id, List<Event> events) {
        SqlRowSet sqlRowSet = namedParameterJdbcTemplate.queryForRowSet("""
                        SELECT MAX(event_index) max_index
                        FROM events
                        WHERE aggregate_type = :aggregate_type
                          AND aggregate_id = :aggregate_id
                        """,
                Map.of(
                        "aggregate_type", eventNameMapper.aggregateNameFor(id),
                        "aggregate_id", id.uuid().toString()
                ));
        sqlRowSet.first();
        int maxIndex = sqlRowSet.getInt("max_index");

        namedParameterJdbcTemplate.batchUpdate("""
                        INSERT INTO events (aggregate_type,
                                            aggregate_id,
                                            event_index,
                                            event_type,
                                            event_payload)
                        VALUES (:aggregate_type,
                                :aggregate_id,
                                :event_index,
                                :event_type,
                                :event_payload::json)
                        """,
                EventParameterSource.of(eventNameMapper, eventSerializer, id, events, maxIndex));
    }

    @Override
    public Optional<List<Event>> findAllForId(Identifier identifier) {
        List<Event> events = namedParameterJdbcTemplate.query("""
                        SELECT *
                        FROM events
                        WHERE aggregate_type = :aggregate_type
                          AND aggregate_id = :aggregate_id
                        ORDER BY events.event_index
                        """,
                Map.of(
                        "aggregate_type", eventNameMapper.aggregateNameFor(identifier),
                        "aggregate_id", identifier.uuid().toString()
                ),
                this::mapRow);
        return Optional.of(events);
    }

    @Override
    public List<Event> findAllForTypes(List<Class<? extends Event>> gameCreatedClass) {
        return namedParameterJdbcTemplate.query("""
                        SELECT *
                        FROM events
                        WHERE event_type IN (:ids)
                        """,
                Map.of("ids", gameCreatedClass.stream()
                                              .map(eventNameMapper::eventNameFor)
                                              .toList()),
                this::mapRow);
    }

    Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        String eventType = rs.getString("event_type");
        String eventJson = rs.getString("event_payload");
        return eventSerializer.deserialize(eventNameMapper.eventTypeFor(eventType), eventJson);
    }

    public static class EventParameterSource extends AbstractSqlParameterSource {

        private static final List<String> PARAMETER_NAMES = List.of(
                "aggregate_type",
                "aggregate_id",
                "event_index",
                "event_type",
                "event_payload"
        );

        private final int eventIndex;

        public static EventParameterSource[] of(EventNameMapper eventNameMapper,
                                                EventSerializer eventSerializer,
                                                Identifier id,
                                                List<Event> events,
                                                int startIndex) {
            AtomicInteger index = new AtomicInteger(startIndex);
            return events.stream()
                         .map(e -> new EventParameterSource(eventNameMapper, eventSerializer, id, e, index.incrementAndGet()))
                         .toArray(EventParameterSource[]::new);
        }

        private final EventNameMapper eventNameMapper;
        private final EventSerializer eventSerializer;
        private final Identifier identifier;
        private final Event event;

        private EventParameterSource(EventNameMapper eventNameMapper,
                                     EventSerializer eventSerializer,
                                     Identifier identifier,
                                     Event event,
                                     int eventIndex) {
            this.eventNameMapper = eventNameMapper;
            this.eventSerializer = eventSerializer;
            this.identifier = identifier;
            this.event = event;
            this.eventIndex = eventIndex;
        }

        @Override
        public boolean hasValue(@Nonnull String paramName) {
            return PARAMETER_NAMES.contains(paramName);
        }

        @Override
        public Object getValue(@Nonnull String paramName) throws IllegalArgumentException {
            return switch (paramName) {
                case "aggregate_type" -> eventNameMapper.aggregateNameFor(identifier);
                case "aggregate_id" -> identifier.uuid().toString();
                case "event_index" -> eventIndex;
                case "event_type" -> eventNameMapper.eventNameFor(event);
                case "event_payload" -> eventSerializer.serialize(event);
                default -> null;
            };
        }

    }


}
