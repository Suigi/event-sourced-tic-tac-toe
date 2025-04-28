package ninja.ranner.xogame.domain;

public record Cell(int x, int y) {
    public static Cell at(int x, int y) {
        return new Cell(x, y);
    }
}
