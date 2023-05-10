package ninja.ranner.tictactoe;

public record GameJoined(GameId gameId, PlayerId playerId) implements Event {}
