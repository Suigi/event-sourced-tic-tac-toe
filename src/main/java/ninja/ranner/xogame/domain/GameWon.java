package ninja.ranner.xogame.domain;

public record GameWon(GameId gameId, Player player) implements Event {}
