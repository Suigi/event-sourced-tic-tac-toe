package ninja.ranner.xogame.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

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
            Game game = Game.reconstitute(List.of(new GameCreated("IRRELEVANT GAME NAME")));

            game.fillCell(Cell.at(0, 0));

            assertThat(game.uncommittedEvents())
                    .containsExactly(new CellFilled(Game.Player.X, Cell.at(0, 0)));
        }

        @Test
        void fillingSecondCellEmitsCellFilledByO() {

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
            Game game = Game.create("Some Game");

            assertThat(game.currentPlayer())
                    .isEqualTo(Game.Player.X);
        }

        @Test
        void playerOHasTheSecondTurn() {
            Game game = Game.reconstitute(List.of(
                    new GameCreated("Some Game"),
                    new CellFilled(Game.Player.X, Cell.at(1, 1))
            ));

            assertThat(game.currentPlayer())
                    .isEqualTo(Game.Player.O);
        }
    }

}