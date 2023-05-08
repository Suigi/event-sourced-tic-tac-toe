package ninja.ranner.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Game {
    private final List<Event> events = new ArrayList<>();

    public static Game create(UUID gameId) {
        Game game = new Game();
        game.events.add(new GameCreated(gameId));
        return game;
    }

    public static Game from(List<Event> events) {
        return new Game();
    }


    public List<Event> cullEvents() {
        return events;
    }
}
