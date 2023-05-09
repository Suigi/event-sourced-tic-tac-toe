package ninja.ranner.tictactoe;

import java.util.ArrayList;
import java.util.List;

public class Game {
  private final List<Event> events = new ArrayList<>();
  private GameId id = null;

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

  private void emit(GameCreated event) {
    apply(event);
    events.add(event);
  }

  private void apply(Event event) {
    if (event instanceof GameCreated gameCreated) {
      id = gameCreated.gameId();
    }
  }

  public List<Event> cullEvents() {
    List<Event> culledEvents = List.copyOf(events);
    events.clear();
    return culledEvents;
  }

  public GameId id() {
    return id;
  }

  public void join(PlayerId playerId) {

  }
}
