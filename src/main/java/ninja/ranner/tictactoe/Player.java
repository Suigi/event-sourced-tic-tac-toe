package ninja.ranner.tictactoe;

import java.util.List;

public class Player extends Aggregate {
  private PlayerId id;

  public static Player create(PlayerId playerId) {
    Player player = new Player();
    player.emit(new PlayerRegistered(playerId));
    return player;
  }

  public static Player from(List<Event> events) {
    return new Player();
  }

  @Override
  protected void apply(Event event) {
    if (event instanceof PlayerRegistered playerRegistered) {
      this.id = playerRegistered.playerId();
    }
  }

  public PlayerId id() {
    return id;
  }

}
