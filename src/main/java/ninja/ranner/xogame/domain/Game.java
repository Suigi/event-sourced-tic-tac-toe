package ninja.ranner.xogame.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Game {
    private final List<Event> uncommittedEvents = new ArrayList<>();
    private String name;

    public static Game create(String name) {
        Game game = new Game();
        game.emit(new GameCreated(name));
        return game;
    }

    public static Game reconstitute(List<Event> events) {
        Game game = new Game();
        events.forEach(game::apply);
        return game;
    }

    private void apply(Event event) {
        switch (event) {
            case GameCreated(String newGameName) -> this.name = newGameName;
            default -> {}
        }
    }

    private void emit(Event event) {
        uncommittedEvents.add(event);
        apply(event);
    }

    public Stream<Event> uncommittedEvents() {
        return uncommittedEvents.stream();
    }

    public String name() {
        return name;
    }
}
