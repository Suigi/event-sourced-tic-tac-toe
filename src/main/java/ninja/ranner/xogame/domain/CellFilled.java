package ninja.ranner.xogame.domain;

public record CellFilled(Player player, Cell cell) implements Event {}
