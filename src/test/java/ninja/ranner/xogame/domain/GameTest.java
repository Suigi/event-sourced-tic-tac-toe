package ninja.ranner.xogame.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class GameTest {

    @Nested
    public class CommandsGenerateEvents {

        @Test
        void creatingGameEmitsGameCreated() {
            GameId gameId = GameId.of(UUID.fromString("019671ab-efcd-7991-899e-07694ad51bd5"));
            Game game = Game.create(gameId, "My game");

            assertThat(game.uncommittedEvents())
                    .containsExactly(new GameCreated(gameId, "My game"));
        }

        @Test
        void fillingFirstCellEmitsCellFilledByX() {
            Game game = Game.reconstitute(List.of(gameCreated()));

            game.fillCell(Cell.at(0, 0));

            assertThat(game.uncommittedEvents())
                    .containsExactly(new CellFilled(Player.X, Cell.at(0, 0)));
        }

        @Test
        void fillingSecondCellEmitsCellFilledByO() {
            Game game = Game.reconstitute(List.of(
                    gameCreated(),
                    new CellFilled(Player.X, Cell.at(0, 0))
            ));

            game.fillCell(Cell.at(2, 2));

            assertThat(game.uncommittedEvents())
                    .containsExactly(new CellFilled(Player.O, Cell.at(2, 2)));
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
                            new CellFilled(Player.X, Cell.at(2, 0)),
                            new GameWon(Player.X));
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
                            new CellFilled(Player.X, Cell.at(0, 2)),
                            new GameDrawn());
        }
    }

    @Nested
    public class CommandsProtectInvariants {
        @Test
        void fillingTheSameCellTwiceThrowsException() {
            Game game = Game.reconstitute(List.of(
                    gameCreated(),
                    xFilledCell(1, 2)
            ));

            assertThatThrownBy(() -> game.fillCell(Cell.at(1, 2)))
                    .isExactlyInstanceOf(CellAlreadyFilled.class)
                    .hasMessage("Cell(1,2) is already filled by X");
        }
    }


    @Nested
    public class EventsProjectState {

        @Test
        void gameCreatedContainsGameName() {
            Game game = Game.reconstitute(List.of(
                    new GameCreated(GameId.random(), "The Game")));

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
                    .isEqualTo(Player.X);
        }

        @Test
        void playerOHasTheSecondTurn() {
            Game game = Game.reconstitute(List.of(
                    gameCreated(),
                    new CellFilled(Player.X, Cell.at(1, 1))
            ));

            assertThat(game.currentPlayer())
                    .isEqualTo(Player.O);
        }

        @Test
        void newGameBoardIsAllEmpty() {
            Game game = Game.reconstitute(List.of(gameCreated()));

            assertThat(game.boardMap())
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

            assertThat(game.boardMap())
                    .containsExactlyInAnyOrderEntriesOf(Map.of(
                            Cell.at(0, 0), Player.X,
                            Cell.at(1, 1), Player.O,
                            Cell.at(2, 2), Player.X,
                            Cell.at(2, 0), Player.O
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
                    new GameWon(Player.X)
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
                    new GameWon(Player.O)
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
        return new GameCreated(GameId.random(), "IRRELEVANT GAME NAME");
    }

    private CellFilled xFilledCell(int x, int y) {
        return new CellFilled(Player.X, Cell.at(x, y));
    }

    private CellFilled oFilledCell(int x, int y) {
        return new CellFilled(Player.O, Cell.at(x, y));
    }

}