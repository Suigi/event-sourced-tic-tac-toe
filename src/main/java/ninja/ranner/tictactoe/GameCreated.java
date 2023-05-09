package ninja.ranner.tictactoe;

import java.util.UUID;

public record GameCreated(GameId gameId) implements Event {

  public GameCreated(GameId gameId) {
    this.gameId = gameId;
  }

  public GameCreated(UUID gameId) {
    this(GameId.of(gameId));
  }
}
