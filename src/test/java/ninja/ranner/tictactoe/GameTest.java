package ninja.ranner.tictactoe;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GameTest {

    @Test
    public void creatingGameEmitsGameCreatedEvent() throws Exception {
        Game game = Game.create();

        assertThat(game.cullEvents())
                .containsExactly(new GameCreated());
    }

    @Test
    public void gameCanBeCreatedFromEvent() throws Exception {
        Game game = Game.from(List.of(new GameCreated()));
    }

}
