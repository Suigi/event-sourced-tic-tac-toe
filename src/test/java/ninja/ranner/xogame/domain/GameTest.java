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
                            Cell.at(0,0), Game.Player.X,
                            Cell.at(1,1), Game.Player.O,
                            Cell.at(2,2), Game.Player.X,
                            Cell.at(2,0), Game.Player.O
                    ));
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