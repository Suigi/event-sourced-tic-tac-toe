package ninja.ranner.xogame.domain;

import java.util.List;
import java.util.Map;

public class Game extends EventSourcedAggregate {
    private final Board board = new Board();
    private GameResult gameResult = GameResult.GAME_IN_PROGRESS;
    private Player currentPlayer = Player.X;
    private String name;
    private GameId gameId;

    private Game() {
    }

    // Commands

    public static Game create(GameId gameId, String name) {
        Game game = new Game();
        game.emit(new GameCreated(gameId, name));
        return game;
    }

    public void fillCell(Cell cell) {
        ensureGameIsInProgress();
        ensureFreeCell(cell);

        emit(new CellFilled(currentPlayer, cell));

        determineGameFinished();
    }

    private void ensureGameIsInProgress() {
        if (gameResult != GameResult.GAME_IN_PROGRESS) {
            throw new GameAlreadyOver();
        }
    }

    private void ensureFreeCell(Cell cell) {
        board.filledBy(cell)
             .ifPresent(player -> {throw new CellAlreadyFilled(cell, player);});
    }

    private void determineGameFinished() {
        board.determineWinner()
             .ifPresentOrElse(
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

    public GameId id() {
        return gameId;
    }

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
            case GameCreated(GameId newGameId, String newGameName) -> {
                this.gameId = newGameId;
                this.name = newGameName;
            }
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
