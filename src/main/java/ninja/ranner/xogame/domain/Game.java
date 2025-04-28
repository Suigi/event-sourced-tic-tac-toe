package ninja.ranner.xogame.domain;

import java.util.List;

public class Game extends EventSourcedAggregate {
    private Player currentPlayer = Player.X;
    private String name;

    private Game() {}

    public enum Player {
        X,
        O;
    }

    // Commands

    public static Game create(String name) {
        Game game = new Game();
        game.emit(new GameCreated(name));
        return game;
    }

    public void fillCell(Cell cell) {
        emit(new CellFilled(Player.X, cell));
    }

    // Queries

    public String name() {
        return name;
    }

    public Player currentPlayer() {
        return currentPlayer;
    }

    // Internal projection

    public static Game reconstitute(List<Event> events) {
        Game game = new Game();
        events.forEach(game::apply);
        return game;
    }

    @Override
    protected void apply(Event event) {
        switch (event) {
            case GameCreated(String newGameName) -> this.name = newGameName;
            case CellFilled(Player player, Cell cell) -> this.currentPlayer = player == Player.X ? Player.O : Player.X;
            default -> {}
        }
    }

}
