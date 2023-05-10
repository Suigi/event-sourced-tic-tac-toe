package ninja.ranner.tictactoe.domain;

public record GameJoined(GameId gameId, PlayerId playerId) implements Event {}
