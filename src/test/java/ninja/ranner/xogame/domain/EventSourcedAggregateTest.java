package ninja.ranner.xogame.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EventSourcedAggregateTest {

    @Test
    void emittedEventsAreAppliedImmediately() {
        TestableAggregate testableAggregate = new TestableAggregate();
        Event event = new Event() {};

        testableAggregate.emit(event);

        assertThat(testableAggregate.appliedEvents)
                .containsExactly(event);
    }

    @Test
    void emittedEventsAreAddedToUncommittedEvents() {
        TestableAggregate testableAggregate = new TestableAggregate();
        Event event = new Event() {};

        testableAggregate.emit(event);

        assertThat(testableAggregate.uncommittedEvents())
                .containsExactly(event);
    }

    static class TestableAggregate extends EventSourcedAggregate {

        private final List<Event> appliedEvents = new ArrayList<>();

        @Override
        protected void apply(Event event) {
            this.appliedEvents.add(event);
        }

    }

}