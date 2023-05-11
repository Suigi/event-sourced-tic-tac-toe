# Event-Sourced Tic Tac Toe

I'm using this code base for learning about and experimenting with Event Sourcing.

## Domain

Some ideas for the rules of the domain:

- PLAYERS can join a GAME.
- A GAME starts once two PLAYERS have joined.
- PLAYERS take turns placing their marker on a free spot in a 3x3 grid.
- Once a PLAYER has placed 3 markers in the same row, column or diagonal, they win and the GAME is over.
- PLAYERS have a Score, stating the number of GAMES they played and the number of wins.
- PLAYERS earn Achievements based on their history of GAMES (e.g. "Win your first game")

##  Events

Some ideas for domain events to be "sourced":

- Game was created
- Player joined Game
- Game started
- Player took a turn
- Game ended
- Player earned an Achievement

## Thoughts

### Abstract Base Class

I extracted an abstract `Aggregate` as base class for the Domain Objects `Game` and `Player`.  
This base class provides infrastructure for emitting and applying Events. I tend to avoid class hierarchies,
especially for the purposes of sharing code. So far, it has helped me to move some of the noise 
introduced by the Events out of the Domain Objects. I will probably keep it like that until 
there is some awkwardness or I come up with a different way to do it.

### Domain Object Design

While the public interface of the Domain Entities does not look too different from a non-event-sourced version, 
the internal implementation details seem to be dominated by the Event Sourcing approach.

Public interface of `Game`:
* A factory method `Game create(GameId)` for creating a new Game
* Query methods `GameId id()` and `Stream<PlayerId> players()`
* Command method `void join(PlayerId)`

The additions introduced by Event Sourcing are:
* A public factory method `Game from(List<Event>)` to reconstitute a Game from its previous Events
* Methods introduced in the `Aggregate` base class:
  * Public `List<Event> cullEvents()` method for retrieving Events emitted from Command methods
  * Protected, final `void emit(Event)` method for emitting (and immediately applying) a new Event
  * Protected, abstract `void apply(Event)` method for apply an Event

The methods introduced for the persistence mechanism (`from` and `cullEvents`) seem similarly intrusive to the _Snapshot_ mechanism 
I tend to use for "traditional" database persistence. A _Snapshot_ usually requires a `snapshot()` query method and a `from(Snapshot)` static factory or constructor.

The internal implementation looks completely different from the way I'm used to doing it:

1. ORM-based approaches often assign an ID when an object is first saved. 
   In contrast, (at least to my current understanding of what people are doing) IDs for Event Sourcing are created beforehand, 
   often as a randomly generated UUID. 
2. Command methods are reduced to guarding the invariants of the Domain Object (e.g. `Game.join` only allows for a maximum of two players)
   and emitting an Event containing the state change. The Command methods don't change the state of any internal fields directly.
3. Emitted Events are immediately applied to the state of the object (in the `Aggregate` base class `emit` is calling `apply`).
   This introduces some distance between a Command being executed and its effect on the state of the object,
   which might make it harder to reason about the Command.
4. The `apply` method is the only method that actually changes the state of the object.
   This might make it easier to reason about Events and how they affect state. 
5. `apply` is also being used when reconstituting an object from a previous stream of Events, 
   which avoids inconsistencies (i.e. bugs) between executing a Command and replaying past Events.

### Future Exploration

#### Event Store

I want to implement a simple Event Store so that I can experiment with persisting event-sourced Aggregates.

#### Projections

Having an Event Store should allow me to experiment with creating _Projections_ (list Games that are in progress?).

I could also try to move some of the Query methods from the Domain Entities into Projections.

#### Domain Events consumed by other Entities

I'm not sure how I might implement the _Win your First Game_ Achievement.
The traditional approach would probably do something similar to the following code in an _Application Service_ (maybe _Domain Service_?):

```java
class GameService {
  void takeTurn(GameId gameId, Spot spot) {
    Game game = gameRepository.load(gameId);
    game.playerTakesTurn(spot);
    gameRepository.save(game);

    if (game.isOver()) {
      Player player = playerRepository.load(game.winner());
      player.recordWonGame(gameId);
      playerRepository.save(player);
    }
  }
}
```

This would probably work fine also for Domain Objects that use Event Sourcing as their persistence mechanism. I wonder, though, 
if there is a way to take advantage of the Domain Events being emitted to decouple a Game being over and the effects that
might have on the rest of the application. I do see the potential here to create systems where those side effects become
hard to keep track of.
