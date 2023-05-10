package ninja.ranner.tictactoe;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GameTest {

  @Test
  public void newGameHasId() throws Exception {
    GameId gameId = GameId.of(UUID.randomUUID());

    Game game = Game.create(gameId);

    assertThat(game.id())
        .isEqualTo(gameId);
  }

  @Test
  public void creatingGameEmitsGameCreatedEvent() throws Exception {
    GameId gameId = GameId.of(UUID.randomUUID());

    Game game = Game.create(gameId);

    assertThat(game.cullEvents())
        .containsExactly(new GameCreated(gameId));
  }

  @Test
  public void eventsCanOnlyBeCulledOnce() throws Exception {
    Game game = Game.create(GameId.of(UUID.randomUUID()));
    game.cullEvents();

    assertThat(game.cullEvents())
        .isEmpty();
  }

  @Test
  public void gameCreatedFromEventDoesNotEmitEvents() throws Exception {
    GameId gameId = GameId.of(UUID.randomUUID());
    Game game = Game.from(List.of(new GameCreated(gameId)));

    assertThat(game.cullEvents())
        .isEmpty();
  }

  @Test
  public void gameIsCreatedWithIdFromEvent() throws Exception {
    GameId gameId = GameId.create();

    Game game = Game.from(List.of(new GameCreated(gameId)));

    assertThat(game.id())
        .isEqualTo(gameId);
  }

  @Test
  public void gameCanBeJoined() throws Exception {
    GameId gameId = GameId.create();
    PlayerId playerId = PlayerId.create();
    Game game = Game.create(gameId);
    game.cullEvents();

    game.join(playerId);

    assertThat(game.cullEvents())
       .containsExactly(new GameJoined(gameId, playerId));

  }

}
