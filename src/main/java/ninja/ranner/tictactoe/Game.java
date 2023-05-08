package ninja.ranner.tictactoe;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final List<Event> events = new ArrayList<>();

    public static Game create() {
        Game game = new Game();
        game.events.add(new GameCreated());
        return game;
    }

    public static Game from(List<Event> events) {
        return new Game();
    }


    public List<Event> cullEvents() {
        return events;
    }
}
