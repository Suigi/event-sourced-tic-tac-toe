package ninja.ranner.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Game {
    private final List<Event> events = new ArrayList<>();
    private UUID id = null;

    public static Game create(UUID gameId) {
        Game game = new Game();
        GameCreated event = new GameCreated(gameId);
        game.apply(event);
        game.events.add(event);
        return game;
    }

    public static Game from(List<Event> events) {
        Game game = new Game();
        game.apply((events.get(0)));
        return game;
    }

    private void apply(Event event) {
        if (event instanceof GameCreated) {
            apply((GameCreated) event);
        }
    }

    private void apply(GameCreated event) {
        id = event.gameId();
    }

    public List<Event> cullEvents() {
        return events;
    }

    public UUID id() {
        return id;
    }
}
