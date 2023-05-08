package ninja.ranner.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Game {
    private final List<Event> events = new ArrayList<>();
    private UUID id = null;

    public static Game create(UUID gameId) {
        Game game = new Game();
        game.events.add(new GameCreated(gameId));
        game.id = gameId;
        return game;
    }

    public static Game from(List<Event> events) {
        Game game = new Game();
        game.id = ((GameCreated)events.get(0)).gameId();
        return game;
    }


    public List<Event> cullEvents() {
        return events;
    }

    public UUID id() {
        return id;
    }
}
