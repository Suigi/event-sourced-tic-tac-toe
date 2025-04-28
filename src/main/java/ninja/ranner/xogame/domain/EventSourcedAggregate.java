package ninja.ranner.xogame.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public abstract class EventSourcedAggregate {
    private final List<Event> uncommittedEvents = new ArrayList<>();

    protected abstract void apply(Event event);

    protected void emit(Event event) {
        uncommittedEvents.add(event);
        apply(event);
    }

    public Stream<Event> uncommittedEvents() {
        return uncommittedEvents.stream();
    }
}
