package ninja.ranner.xogame.domain;

import java.util.*;
import java.util.stream.Collectors;

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
        return cells.stream()

                // Map the cells to who filled them, and filter out any unfilled cells
                .map(board::get)
                .filter(Objects::nonNull)

                // Make a grouping of player to number cells filled by them
                .collect(Collectors.groupingBy(x -> x, Collectors.counting()))
                .entrySet().stream()

                // Find the first player who filled three cells
                .filter((entry) -> entry.getValue() == 3)
                .map(Map.Entry::getKey)
                .findFirst();
    }

    public Map<Cell, Player> asMap() {
        return Map.copyOf(board);
    }

    // Commands

    void fill(Player player, Cell cell) {
        board.put(cell, player);
    }
}