package ninja.ranner.tictactoe;

import java.util.UUID;

public record GameCreated(java.util.UUID gameId) implements Event {

  public GameCreated(GameId gameId) {
    this(gameId.value());
  }

  public GameCreated(UUID gameId) {
    this.gameId = gameId;
  }
}
