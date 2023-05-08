package ninja.ranner.tictactoe;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GameTest {

    @Test
    public void creatingGameEmitsGameCreatedEvent() throws Exception {
        Game game = Game.create();

        assertThat(game.cullEvents())
                .hasSize(1);
    }

}
