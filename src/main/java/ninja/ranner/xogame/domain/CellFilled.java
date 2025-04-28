package ninja.ranner.xogame.domain;

public record CellFilled(Game.Player player, Cell cell) implements Event {}
