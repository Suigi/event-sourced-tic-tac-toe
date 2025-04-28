package ninja.ranner.xogame.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameTest {

    @Nested
    public class CommandsGenerateEvents {

        @Test
        void newGameHasEmptyListOfUncommittedEvents() {
            Game game = new Game();

            assertThat(game.uncommittedEvents())
                    .isEmpty();
        }
    }

}