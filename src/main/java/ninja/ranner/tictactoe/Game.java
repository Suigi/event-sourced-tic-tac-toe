package ninja.ranner.tictactoe;

import java.util.List;

public class Game {
    public static Game create() {
        return new Game();
    }

    public static Game from(List<Event> events) {
        return null;
    }

    public List<Event> cullEvents() {
        return List.of(new GameCreated());
    }
}
