package ninja.ranner.tictactoe;

import org.junit.jupiter.api.Test;

class PlayerTest {

  @Test
  public void creatingPlayerEmitsPlayerCreatedEvent() throws Exception {
    PlayerId playerId = PlayerId.create();

    Player player = Player.create(playerId);

  }

}
