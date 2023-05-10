package ninja.ranner.tictactoe;

public class Player extends Aggregate {
  public static Player create(PlayerId playerId) {
    Player player = new Player();
    player.emit(new PlayerRegistered(playerId));
    return player;
  }

  @Override
  protected void apply(Event event) {

  }
}
