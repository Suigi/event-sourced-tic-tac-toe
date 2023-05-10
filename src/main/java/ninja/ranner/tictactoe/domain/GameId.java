package ninja.ranner.tictactoe.domain;

import java.util.UUID;

public record GameId(UUID value) {
  public static GameId of(UUID value) {
    return new GameId(value);
  }

  public static GameId create() {
    return new GameId(UUID.randomUUID());
  }
}
