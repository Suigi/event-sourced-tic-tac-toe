package ninja.ranner.tictactoe;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerTest {

  @Test
  public void creatingPlayerEmitsPlayerCreatedEvent() throws Exception {
    PlayerId playerId = PlayerId.create();

    Player player = Player.create(playerId);

    assertThat(player.cullEvents())
        .containsExactly(new PlayerRegistered(playerId));
  }

  @Test
  public void newPlayerHasAnId() throws Exception {
    PlayerId playerId = PlayerId.create();

    Player player = Player.create(playerId);

    assertThat(player.id())
        .isEqualTo(playerId);
  }

  @Test
  public void playerCanBeCreatedFromEvents() throws Exception {
    PlayerId playerId = PlayerId.create();

    Player.from(List.of(new PlayerRegistered(playerId)));

  }

}
