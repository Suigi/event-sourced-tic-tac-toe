package ninja.ranner.xogame.domain;

import java.util.*;

public class Board {
    static final List<List<Cell>> winningArrangements = List.of(
            // Rows
            List.of(Cell.at(0, 0), Cell.at(0, 1), Cell.at(0, 2)),
            List.of(Cell.at(1, 0), Cell.at(1, 1), Cell.at(1, 2)),
            List.of(Cell.at(2, 0), Cell.at(2, 1), Cell.at(2, 2)),
            // Columns
            List.of(Cell.at(0, 0), Cell.at(1, 0), Cell.at(2, 0)),
            List.of(Cell.at(0, 1), Cell.at(1, 1), Cell.at(2, 1)),
            List.of(Cell.at(0, 2), Cell.at(1, 2), Cell.at(2, 2)),
            // Diagonals
            List.of(Cell.at(0, 0), Cell.at(1, 1), Cell.at(2, 2)),
            List.of(Cell.at(0, 2), Cell.at(1, 1), Cell.at(2, 0))
    );
    final HashMap<Cell, Player> board = new HashMap<>();

    // Queries

    Player cell(Cell key) {
        // TODO make this return an Optional
        return board.get(key);
    }

    boolean isDraw() {
        return board.size() == 9;
    }

    Optional<Player> determineWinner() {
        return winningArrangements.stream()
                .map(this::determineWinner)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    private Optional<Player> determineWinner(List<Cell> cells) {
        List<Player> fills = cells.stream()
                .map(this::cell)
                .filter(Objects::nonNull)
                .toList();
        if (fills.size() != 3) {
            return Optional.empty();
        }
        if (fills.stream().allMatch(Player.X::equals)) {
            return Optional.of(Player.X);
        }
        if (fills.stream().allMatch(Player.O::equals)) {
            return Optional.of(Player.O);
        }
        return Optional.empty();
    }

    public Map<Cell, Player> asMap() {
        return Map.copyOf(board);
    }

    // Commands

    void fill(Player player, Cell cell) {
        board.put(cell, player);
    }
}