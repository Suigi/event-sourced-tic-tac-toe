package ninja.ranner.tictactoe;

import java.util.List;

public class Game {
    public static Game create() {
        return new Game();
    }

    public static void from(List<Event> events) {


    }

    public List<Event> cullEvents() {
        return List.of(new GameCreated());
    }
}
