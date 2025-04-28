package ninja.ranner.xogame.domain;

public record GameCreated(GameId gameId, String newGameName) implements Event {}
