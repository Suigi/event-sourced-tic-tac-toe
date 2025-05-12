package ninja.ranner.xogame.adapter.out.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.annotation.Nonnull;
import ninja.ranner.xogame.application.port.EventStore;
import ninja.ranner.xogame.domain.Event;
import ninja.ranner.xogame.domain.Identifier;
import org.springframework.jdbc.core.JdbcTemplate;
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

    public interface EventNameMapper {
        String aggregateNameFor(Identifier identifier);

        String eventNameFor(Event event);

        Class<?> identifierTypeFor(String aggregateName);

        Class<?> eventTypeFor(String eventName);
    }

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public JdbcEventStore(JdbcTemplate jdbcTemplate,
                          NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                          EventNameMapper eventNameMapper) {
        this.eventNameMapper = eventNameMapper;
        this.jdbcTemplate = jdbcTemplate;
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
                EventParameterSource.of(id, events, maxIndex));
    }

    @Override
    public Optional<List<Event>> findAllForId(Identifier gameId) {
        List<Event> events = namedParameterJdbcTemplate.query("""
                        SELECT *
                        FROM events
                        ORDER BY events.event_index
                        """,
                Map.of(),
                this::mapRow);
        return Optional.of(events);
    }

    @Override
    public List<Event> findAllForTypes(List<Class<? extends Event>> gameCreatedClass) {
        throw new UnsupportedOperationException("findAllForTypes is not implemented.");
    }

    Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        String eventType = rs.getString("event_type");
        String eventJson = rs.getString("event_payload");
        JsonMapper jsonMapper = JsonMapper.builder().build();
        try {
            return (Event) jsonMapper.readValue(eventJson, eventNameMapper.eventTypeFor(eventType));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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

        public static EventParameterSource[] of(Identifier id, List<Event> events, int startIndex) {
            AtomicInteger index = new AtomicInteger(startIndex);
            return events.stream()
                         .map(e -> new EventParameterSource(id, e, index.incrementAndGet()))
                         .toArray(EventParameterSource[]::new);
        }

        private final Identifier identifier;
        private final Event event;

        private EventParameterSource(Identifier identifier, Event event, int eventIndex) {
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
            JsonMapper jsonMapper = JsonMapper.builder().build();
            return switch (paramName) {
                case "aggregate_type" -> "Testable";
                case "aggregate_id" -> identifier.uuid().toString();
                case "event_index" -> eventIndex;
                case "event_type" -> event.getClass().getSimpleName();
                case "event_payload" -> {
                    try {
                        yield jsonMapper.writeValueAsString(event);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
                default -> null;
            };
        }
    }


}
