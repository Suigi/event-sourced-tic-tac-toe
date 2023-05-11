package ninja.ranner.tictactoe.domain;

import java.util.ArrayList;
import java.util.List;

public abstract class Aggregate {
  private final List<Event> events = new ArrayList<>();

  protected final void emit(Event event) {
    apply(event);
    events.add(event);
  }

  public List<Event> cullEvents() {
    List<Event> culledEvents = List.copyOf(events);
    events.clear();
    return culledEvents;
  }

  protected abstract void apply(Event event);
}
