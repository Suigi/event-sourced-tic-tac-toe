package ninja.ranner.xogame.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GameTest {

    @Nested
    public class CommandsGenerateEvents {

        @Test
        void creatingGameEmitsGameCreated() {
            Game game = Game.create("My game");

            assertThat(game.uncommittedEvents())
                    .containsExactly(new GameCreated("My game"));
        }

        @Test
        void fillingFirstCellEmitsCellFilledByX() {
            Game game = Game.reconstitute(List.of(gameCreated()));

            game.fillCell(Cell.at(0, 0));

            assertThat(game.uncommittedEvents())
                    .containsExactly(new CellFilled(Game.Player.X, Cell.at(0, 0)));
        }

        @Test
        void fillingSecondCellEmitsCellFilledByO() {
            Game game = Game.reconstitute(List.of(
                    gameCreated(),
                    new CellFilled(Game.Player.X, Cell.at(0, 0))
            ));

            game.fillCell(Cell.at(2, 2));

            assertThat(game.uncommittedEvents())
                    .containsExactly(new CellFilled(Game.Player.O, Cell.at(2, 2)));
        }

        @Test
        void winningMoveEmitsGameWon() {
            // Game board state:
            // +-------+
            // | X . O |
            // | X . O |
            // | . . . |
            // +-------+
            Game game = Game.reconstitute(List.of(
                    gameCreated(),
                    xFilledCell(0, 0),
                    oFilledCell(0, 2),
                    xFilledCell(1, 0),
                    oFilledCell(1, 2)
            ));

            game.fillCell(Cell.at(2, 0));

            assertThat(game.uncommittedEvents())
                    .containsExactly(
                            new CellFilled(Game.Player.X, Cell.at(2, 0)),
                            new GameWon(Game.Player.X));
        }

        @Test
        void finalDrawMoveEmitGameDrawn() {
            // Game board state:
            // +-------+
            // | X O . |
            // | O O X |
            // | X X O |
            // +-------+
            Game game = Game.reconstitute(List.of(
                    gameCreated(),
                    xFilledCell(0, 0),
                    oFilledCell(1, 1),
                    xFilledCell(2, 0),
                    oFilledCell(1, 0),
                    xFilledCell(1, 2),
                    oFilledCell(0, 1),
                    xFilledCell(2, 1),
                    oFilledCell(2, 2)
            ));

            game.fillCell(Cell.at(0, 2));

            assertThat(game.uncommittedEvents())
                    .containsExactly(
                            new CellFilled(Game.Player.X, Cell.at(0, 2)),
                            new GameDrawn());
        }
    }

    @Nested
    public class EventsProjectState {

        @Test
        void gameCreatedContainsGameName() {
            Game game = Game.reconstitute(List.of(new GameCreated("The Game")));

            assertThat(game.name())
                    .isEqualTo("The Game");
        }

        @Test
        void newGameIsInProgress() {
            Game game = Game.reconstitute(List.of(gameCreated()));

            assertThat(game.result())
                    .isEqualTo(GameResult.GAME_IN_PROGRESS);
        }

        @Test
        void playerXHasTheFirstTurn() {
            Game game = Game.reconstitute(List.of(gameCreated()));

            assertThat(game.currentPlayer())
                    .isEqualTo(Game.Player.X);
        }

        @Test
        void playerOHasTheSecondTurn() {
            Game game = Game.reconstitute(List.of(
                    gameCreated(),
                    new CellFilled(Game.Player.X, Cell.at(1, 1))
            ));

            assertThat(game.currentPlayer())
                    .isEqualTo(Game.Player.O);
        }

        @Test
        void newGameBoardIsAllEmpty() {
            Game game = Game.reconstitute(List.of(gameCreated()));

            assertThat(game.board())
                    .isEmpty();
        }

        @Test
        void gameBoardContainsFilledCells() {
            Game game = Game.reconstitute(List.of(
                    gameCreated(),
                    xFilledCell(0, 0),
                    oFilledCell(1, 1),
                    xFilledCell(2, 2),
                    oFilledCell(2, 0)
            ));

            assertThat(game.board())
                    .containsExactlyInAnyOrderEntriesOf(Map.of(
                            Cell.at(0, 0), Game.Player.X,
                            Cell.at(1, 1), Game.Player.O,
                            Cell.at(2, 2), Game.Player.X,
                            Cell.at(2, 0), Game.Player.O
                    ));
        }

        @Test
        void whenXWon_gameResultShowsPlayerXWon() {
            // Game board state:
            // +-------+
            // | X . O |
            // | X . O |
            // | X . . |
            // +-------+
            Game game = Game.reconstitute(List.of(
                    gameCreated(),
                    xFilledCell(0, 0),
                    oFilledCell(0, 2),
                    xFilledCell(1, 0),
                    oFilledCell(1, 2),
                    xFilledCell(2, 0),
                    new GameWon(Game.Player.X)
            ));

            assertThat(game.result())
                    .isEqualTo(GameResult.PLAYER_X_WINS);
        }

        @Test
        void whenOWon_gameResultShowsPlayerOWon() {
            // Game board state:
            // +-------+
            // | X . O |
            // | X X O |
            // | . . O |
            // +-------+
            Game game = Game.reconstitute(List.of(
                    gameCreated(),
                    xFilledCell(0, 0),
                    oFilledCell(0, 2),
                    xFilledCell(1, 0),
                    oFilledCell(1, 2),
                    xFilledCell(1, 1),
                    oFilledCell(2, 2),
                    new GameWon(Game.Player.O)
            ));

            assertThat(game.result())
                    .isEqualTo(GameResult.PLAYER_O_WINS);
        }

        @Test
        void whenGameEndsInDraw_gameResultShowsDraw() {
            // Game board state:
            // +-------+
            // | X O . |
            // | O O X |
            // | X X O |
            // +-------+
            Game game = Game.reconstitute(List.of(
                    gameCreated(),
                    xFilledCell(0, 0),
                    oFilledCell(1, 1),
                    xFilledCell(2, 0),
                    oFilledCell(1, 0),
                    xFilledCell(1, 2),
                    oFilledCell(0, 1),
                    xFilledCell(2, 1),
                    oFilledCell(2, 2),
                    xFilledCell(0, 2),
                    new GameDrawn()
            ));

            assertThat(game.result())
                    .isEqualTo(GameResult.DRAW);
        }
    }

    private GameCreated gameCreated() {
        return new GameCreated("IRRELEVANT GAME NAME");
    }

    private CellFilled xFilledCell(int x, int y) {
        return new CellFilled(Game.Player.X, Cell.at(x, y));
    }

    private CellFilled oFilledCell(int x, int y) {
        return new CellFilled(Game.Player.O, Cell.at(x, y));
    }

}