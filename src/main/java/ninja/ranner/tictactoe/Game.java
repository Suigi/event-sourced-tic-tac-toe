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
        apply(game, event);
        game.events.add(event);
        return game;
    }

    private static void apply(Game game, GameCreated event) {
        game.id = event.gameId();
    }

    public static Game from(List<Event> events) {
        Game game = new Game();
        apply(game, ((GameCreated) events.get(0)));
        return game;
    }


    public List<Event> cullEvents() {
        return events;
    }

    public UUID id() {
        return id;
    }
}
