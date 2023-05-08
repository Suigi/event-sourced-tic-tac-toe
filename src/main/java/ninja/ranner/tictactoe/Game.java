package ninja.ranner.tictactoe;

import java.util.List;

public class Game {
    public static Game create() {
        return new Game();
    }

    public List cullEvents() {
        return List.of("");
    }
}
