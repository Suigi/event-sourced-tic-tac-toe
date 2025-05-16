package ninja.ranner.xogame.adapter.out.jdbc;

import ninja.ranner.xogame.domain.Event;
import ninja.ranner.xogame.domain.GameCreated;
import ninja.ranner.xogame.domain.GameId;
import ninja.ranner.xogame.domain.Identifier;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class JdbcEventStoreTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public record TestableId(UUID uuid) implements Identifier {
        public static TestableId random() {
            return new TestableId(UUID.randomUUID());
        }
    }

    public record TestableEvent(String stringValue) implements Event {}

    public record AnotherTestableEvent(String stringValue) implements Event {}

    @Test
    void storesSingleEvent() throws Exception {
        JdbcEventStore eventStore = createEventStore();
        TestableId id = TestableId.random();

        eventStore.append(id, List.of(new TestableEvent("Event Data")));

        var records = jdbcTemplate.queryForList("SELECT * FROM events");
        assertThat(records)
                .hasSize(1);
        Map<String, Object> record = records.getFirst();
        assertThat(record)
                .containsEntry("aggregate_type", "Testable")
                .containsEntry("aggregate_id", id.uuid().toString())
                .containsEntry("event_index", 1)
                .containsEntry("event_type", "TestableEvent")
                .containsEntry("event_payload",
                        createJsonObject("{\"stringValue\":\"Event Data\"}"))
        ;
    }

    @Test
    void storesMultipleEvents() throws SQLException {
        JdbcEventStore eventStore = createEventStore();
        TestableId id = TestableId.random();

        eventStore.append(id, List.of(
                new TestableEvent("First"),
                new AnotherTestableEvent("Second")));

        var records = jdbcTemplate.queryForList("SELECT * FROM events");
        assertThat(records)
                .hasSize(2);
        Map<String, Object> record = records.get(1);
        assertThat(record)
                .containsEntry("aggregate_type", "Testable")
                .containsEntry("aggregate_id", id.uuid().toString())
                .containsEntry("event_index", 2)
                .containsEntry("event_type", "AnotherTestableEvent")
                .containsEntry("event_payload",
                        createJsonObject("{\"stringValue\":\"Second\"}"));
    }

    @Test
    void appendsNewEventForExistingAggregate() throws SQLException {
        JdbcEventStore eventStore = createEventStore();
        TestableId id = TestableId.random();
        eventStore.append(id, List.of(new TestableEvent("First")));

        eventStore.append(id, List.of(new TestableEvent("Second")));

        var records = jdbcTemplate.queryForList("SELECT * FROM events");
        assertThat(records)
                .hasSize(2);
        Map<String, Object> record = records.get(1);
        assertThat(record)
                .containsEntry("event_index", 2)
                .containsEntry("event_payload",
                        createJsonObject("{\"stringValue\":\"Second\"}"));
    }

    @Test
    void storedEventsCanBeFound() {
        JdbcEventStore eventStore = createEventStore();
        TestableId id = TestableId.random();
        eventStore.append(id, List.of(
                new TestableEvent("My Event"),
                new AnotherTestableEvent("Another Event")));

        List<Event> events = eventStore.findAllForId(id).orElseThrow();

        assertThat(events)
                .containsExactly(
                        new TestableEvent("My Event"),
                        new AnotherTestableEvent("Another Event"));
    }

    private PGobject createJsonObject(String value) throws SQLException {
        PGobject payload = new PGobject();
        payload.setType("json");
        payload.setValue(value);
        return payload;
    }

    private JdbcEventStore createEventStore() {
        return new JdbcEventStore(jdbcTemplate,
                namedParameterJdbcTemplate,
                new TestableEventNameMapper());
    }

    @Test
    void storedEventCanBeRetrievedByAggregate() {
        JdbcEventStore eventStore = createEventStore();
        GameId firstGameId = GameId.random();
        GameId secondGameId = GameId.random();

        eventStore.append(firstGameId, List.of(
                new GameCreated(firstGameId, "First Game Name")
        ));
        eventStore.append(new TestableId(firstGameId.uuid()), List.of(
                new TestableEvent("Unrelated Event")
        ));
        eventStore.append(secondGameId, List.of(
                new GameCreated(secondGameId, "Second Game Name")
        ));

        List<Event> foundEvents = eventStore.findAllForId(firstGameId).orElseThrow();
        assertThat(foundEvents)
                .containsExactly(
                        new GameCreated(firstGameId, "First Game Name")
                );
    }

    @Test
    void storedEventCanBeRetrievedByType() {
        JdbcEventStore eventStore = createEventStore();
        GameId gameId = GameId.random();

        eventStore.append(gameId, List.of(
                new GameCreated(gameId, "New Game Name"),
                new TestableEvent("My String")
        ));

        List<Event> foundEvents = eventStore.findAllForTypes(List.of(GameCreated.class));
        assertThat(foundEvents)
                .containsExactly(
                        new GameCreated(gameId, "New Game Name")
                );
    }

    private static class TestableEventNameMapper implements JdbcEventStore.EventNameMapper {
        @Override
        public String aggregateNameFor(Class<? extends Identifier> identifierType) {
            if (identifierType.equals(TestableId.class)) {
                return "Testable";
            }
            if (identifierType.equals(GameId.class)) {
                return "Game";
            }
            throw new IllegalArgumentException("Unknown identifierType type " + identifierType);
        }

        @Override
        public String eventNameFor(Class<? extends Event> eventClass) {
            return eventClass.getSimpleName();
        }

        @Override
        public Class<?> identifierTypeFor(String aggregateName) {
            if (aggregateName.equals("Testable")) {
                return TestableId.class;
            }
            throw new IllegalArgumentException("Unknown aggregate name " + aggregateName);
        }

        @Override
        public Class<?> eventTypeFor(String eventName) {
            return switch (eventName) {
                case "TestableEvent" -> TestableEvent.class;
                case "AnotherTestableEvent" -> AnotherTestableEvent.class;
                case "GameCreated" -> GameCreated.class;
                default -> throw new IllegalArgumentException("Unknown event name " + eventName);
            };
        }
    }
}