package ninja.ranner.xogame.domain;

import java.util.*;

public class Game extends EventSourcedAggregate {
    private final HashMap<Cell, Player> board = new HashMap<>();
    private GameResult gameResult = GameResult.GAME_IN_PROGRESS;
    private Player currentPlayer = Player.X;
    private String name;

    private static final List<List<Cell>> winningArrangements = List.of(
            // Rows
            List.of(Cell.at(0, 0), Cell.at(0, 1), Cell.at(0, 2)),
            List.of(Cell.at(1, 0), Cell.at(1, 1), Cell.at(1, 2)),
            List.of(Cell.at(2, 0), Cell.at(2, 1), Cell.at(2, 2)),
            // Columns
            List.of(Cell.at(0, 0), Cell.at(1, 0), Cell.at(2, 0)),
            List.of(Cell.at(0, 1), Cell.at(1, 1), Cell.at(2, 1)),
            List.of(Cell.at(0, 2), Cell.at(1, 2), Cell.at(2, 2)),
            // Diagonals
            List.of(Cell.at(0, 0), Cell.at(1, 1), Cell.at(2, 2)),
            List.of(Cell.at(0, 2), Cell.at(1, 1), Cell.at(2, 0))
    );

    private Game() {}

    public enum Player {
        X,
        O,
    }

    // Commands

    public static Game create(String name) {
        Game game = new Game();
        game.emit(new GameCreated(name));
        return game;
    }

    public void fillCell(Cell cell) {
        if (board.containsKey(cell)) {
            throw new IllegalStateException();
        }
        emit(new CellFilled(currentPlayer, cell));

        determineGameFinished();
    }

    private void determineGameFinished() {
        Optional<Player> winner = winningArrangements.stream()
                .map(this::determineWinner)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
        winner.ifPresentOrElse(
                w -> emit(new GameWon(w)),
                this::determineDraw);
    }

    private void determineDraw() {
        if (board.size() == 9) {
            emit(new GameDrawn());
        }
    }

    private Optional<Player> determineWinner(List<Cell> cells) {
        List<Player> fills = cells.stream()
                .map(board::get)
                .filter(Objects::nonNull)
                .toList();
        if (fills.size() != 3) {
            return Optional.empty();
        }
        if (fills.stream().allMatch(Player.X::equals)) {
            return Optional.of(Player.X);
        }
        if (fills.stream().allMatch(Player.O::equals)) {
            return Optional.of(Player.O);
        }
        return Optional.empty();
    }

    // Queries

    public String name() {
        return name;
    }

    public Player currentPlayer() {
        return currentPlayer;
    }

    public Map<Cell, Player> board() {
        return board;
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
                this.board.put(cell, player);
            }
            case GameWon(Player winner) -> gameResult = winner == Player.X
                    ? GameResult.PLAYER_X_WINS
                    : GameResult.PLAYER_O_WINS;
            case GameDrawn() -> gameResult = GameResult.DRAW;
            default -> {}
        }
    }

}
