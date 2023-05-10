package ninja.ranner.tictactoe;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerTest {

  @Test
  public void creatingPlayerEmitsPlayerCreatedEvent() throws Exception {
    PlayerId playerId = PlayerId.create();

    Player player = Player.create(playerId);

    assertThat(player.cullEvents())
        .containsExactly(new PlayerRegistered(playerId));
  }

}
