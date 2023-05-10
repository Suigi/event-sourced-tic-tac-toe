package ninja.ranner.tictactoe;

public record GameCreated(GameId gameId) implements Event {

  public GameCreated(GameId gameId) {
    this.gameId = gameId;
  }

}
