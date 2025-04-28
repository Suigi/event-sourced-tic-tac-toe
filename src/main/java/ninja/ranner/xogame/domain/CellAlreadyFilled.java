package ninja.ranner.xogame.domain;

public class CellAlreadyFilled extends RuntimeException {
    public CellAlreadyFilled(Cell cell, Player player) {
        super("Cell(%d,%d) is already filled by %s".formatted(cell.x(), cell.y(), player));
    }
}
