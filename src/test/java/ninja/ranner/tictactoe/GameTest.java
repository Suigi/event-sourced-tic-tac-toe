package ninja.ranner.tictactoe;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GameTest {

  @Test
  public void newGameHasId() throws Exception {
    UUID gameId = UUID.randomUUID();

    Game game = Game.create(gameId);

    assertThat(game.id())
        .isEqualTo(gameId);
  }

  @Test
  public void creatingGameEmitsGameCreatedEvent() throws Exception {
    UUID gameId = UUID.randomUUID();
    Game game = Game.create(gameId);

    assertThat(game.cullEvents())
        .containsExactly(new GameCreated(gameId));
  }

  @Test
  public void culledEventsAreRemovedFromList() throws Exception {
    UUID gameId = UUID.randomUUID();
    Game game = Game.create(gameId);
    game.cullEvents();

    assertThat(game.cullEvents())
        .isEmpty();
  }

  @Test
  public void gameCreatedFromEventDoesNotEmitEvents() throws Exception {
    UUID gameId = UUID.randomUUID();
    Game game = Game.from(List.of(new GameCreated(gameId)));

    assertThat(game.cullEvents())
        .isEmpty();
  }

  @Test
  public void gameIsCreatedWithIdFromEvent() throws Exception {
    UUID gameId = UUID.randomUUID();

    Game game = Game.from(List.of(new GameCreated(gameId)));

    assertThat(game.id())
        .isEqualTo(gameId);
  }

}
