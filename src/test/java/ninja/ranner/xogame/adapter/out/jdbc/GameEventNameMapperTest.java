package ninja.ranner.xogame.adapter.out.jdbc;

import ninja.ranner.xogame.domain.*;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class GameEventNameMapperTest {

    public record EventCase(
            Event event,
            String eventName
    ) {}

    public record AggregateCase(
            Identifier identifier,
            String aggregateName
    ) {}

    public static Stream<Arguments> allEvents() {
        GameId gameId = GameId.of(UUID.fromString("0196d97f-024c-700f-b5d4-cda99209868b"));
        return Stream.of(
                argumentFrom(new EventCase(
                        new GameCreated(gameId, "Game Name"),
                        "GameCreated"
                )),
                argumentFrom(new EventCase(
                        new CellFilled(Player.X, Cell.at(1, 2)),
                        "CellFilled"
                )),
                argumentFrom(new EventCase(
                        new GameDrawn(gameId),
                        "GameDrawn"
                )),
                argumentFrom(new EventCase(
                        new GameWon(gameId, Player.O),
                        "GameWon"
                ))
        );
    }

    public static Stream<Arguments> allIdentifiers() {
        return Stream.of(
                argumentFrom(new AggregateCase(GameId.random(), "Game"))
        );
    }

    private static Arguments argumentFrom(AggregateCase aggregateCase) {
        return Arguments.of(Named.of(aggregateCase.aggregateName(), aggregateCase));
    }

    private static Arguments argumentFrom(EventCase payload) {
        return Arguments.of(Named.of(payload.eventName(), payload));
    }

    @ParameterizedTest
    @MethodSource("allEvents")
    void mapsEventsToEventNames(EventCase eventCase) {
        GameEventNameMapper mapper = new GameEventNameMapper();

        assertThat(mapper.eventNameFor(eventCase.event()))
                .isEqualTo(eventCase.eventName());
    }

    @Test
    void mappingUnknownEventToEventNameThrows() {
        GameEventNameMapper mapper = new GameEventNameMapper();

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> mapper.eventNameFor(new Event() {}))
                .withMessageContaining("Event class class ninja.ranner.xogame.adapter.out.jdbc.GameEventNameMapperTest$1 is not mapped");
    }

    @Test
    void allEventTypesAreMappedToNames() {
        GameEventNameMapper mapper = new GameEventNameMapper();
        Reflections reflections = new Reflections("ninja.ranner.xogame.domain");
        List<Class<? extends Event>> eventTypes = reflections
                .getSubTypesOf(Event.class).stream()
                .filter(this::isConcreteImplementation)
                .toList();

        for (Class<? extends Event> eventType : eventTypes) {
            assertThatNoException()
                    .as("Event type " + eventType + " should be mapped")
                    .isThrownBy(() -> mapper.eventNameFor(eventType));
        }
    }

    @ParameterizedTest
    @MethodSource("allEvents")
    void mapsEventNameToEventType(EventCase eventCase) {
        GameEventNameMapper mapper = new GameEventNameMapper();

        assertThat(mapper.eventTypeFor(eventCase.eventName()))
                .isEqualTo(eventCase.event().getClass());
    }

    @Test
    void mappingUnknownEventNameToEventTypeThrows() {
        GameEventNameMapper mapper = new GameEventNameMapper();

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> mapper.eventTypeFor("UnknownEvent"))
                .withMessageContaining("Unknown Event Type 'UnknownEvent'");
    }

    @ParameterizedTest
    @MethodSource("allIdentifiers")
    void mapsIdentifierTypesToAggregateName(AggregateCase aggregateCase) {
        GameEventNameMapper mapper = new GameEventNameMapper();

        assertThat(mapper.aggregateNameFor(aggregateCase.identifier()))
                .isEqualTo(aggregateCase.aggregateName());
    }

    private boolean isConcreteImplementation(Class<? extends Event> x) {
        return !x.isInterface() && !Modifier.isAbstract(x.getModifiers()) && !x.isAnonymousClass();
    }
}
