package ninja.ranner.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Game extends Aggregate {
  private GameId id = null;
  private final List<PlayerId> players = new ArrayList<>();

  private Game() {
  }

  public static Game create(GameId gameId) {
    Game game = new Game();
    game.emit(new GameCreated(gameId));
    return game;
  }

  public static Game from(List<Event> events) {
    Game game = new Game();
    events.forEach(game::apply);
    return game;
  }

  @Override
  protected void apply(Event event) {
    if (event instanceof GameCreated gameCreated) {
      id = gameCreated.gameId();
    }
    if (event instanceof GameJoined gameJoined) {
      players.add(gameJoined.playerId());
    }
  }

  public GameId id() {
    return id;
  }

  public Stream<PlayerId> players() {
    return players.stream();
  }

  public void join(PlayerId playerId) {
    if (players.size() == 2) {
      throw new GameFull();
    }
    emit(new GameJoined(id, playerId));
  }

}
