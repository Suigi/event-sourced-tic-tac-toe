package ninja.ranner.xogame.domain;

import java.util.List;

public class Game extends EventSourcedAggregate {
    private String name;

    private Game() {}

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

    @Override
    protected void apply(Event event) {
        switch (event) {
            case GameCreated(String newGameName) -> this.name = newGameName;
            default -> {}
        }
    }

    public String name() {
        return name;
    }
}
