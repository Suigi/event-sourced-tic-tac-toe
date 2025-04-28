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

    }

    @Nested
    public class EventsProjectState {

        @Test
        void gameCreatedContainsGameName() {
            Game game = Game.reconstitute(List.of(new GameCreated("The Game")));

            assertThat(game.name())
                   .isEqualTo("The Game");
        }

    }

}