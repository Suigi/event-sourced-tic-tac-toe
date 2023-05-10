package ninja.ranner.tictactoe.domain;

import java.util.UUID;

public record PlayerId(UUID value) {
  public static PlayerId of(UUID value) {
    return new PlayerId(value);
  }

  public static PlayerId create() {
    return new PlayerId(UUID.randomUUID());
  }
}
