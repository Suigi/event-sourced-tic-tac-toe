package ninja.ranner.xogame.domain;

import java.util.*;

public class Game extends EventSourcedAggregate {
    private final Board board = new Board();
    private GameResult gameResult = GameResult.GAME_IN_PROGRESS;
    private Player currentPlayer = Player.X;
    private String name;

    private Game() {}

    // Commands

    public static Game create(String name) {
        Game game = new Game();
        game.emit(new GameCreated(name));
        return game;
    }

    public void fillCell(Cell cell) {
        // TODO guard against filling an already filled cell

        emit(new CellFilled(currentPlayer, cell));

        determineGameFinished();
    }

    private void determineGameFinished() {
        board.determineWinner().ifPresentOrElse(
                winner -> emit(new GameWon(winner)),
                this::determineDraw
        );
    }

    private void determineDraw() {
        if (board.isDraw()) {
            emit(new GameDrawn());
        }
    }

    // Queries

    public String name() {
        return name;
    }

    public Player currentPlayer() {
        return currentPlayer;
    }

    public Map<Cell, Player> boardMap() {
        return board.asMap();
    }

    public GameResult result() {
        return gameResult;
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
            case CellFilled(Player player, Cell cell) -> {
                this.currentPlayer = player == Player.X ? Player.O : Player.X;
                board.fill(player, cell);
            }
            case GameWon(Player winner) -> gameResult = winner == Player.X
                    ? GameResult.PLAYER_X_WINS
                    : GameResult.PLAYER_O_WINS;
            case GameDrawn() -> gameResult = GameResult.DRAW;
            default -> {}
        }
    }

}
