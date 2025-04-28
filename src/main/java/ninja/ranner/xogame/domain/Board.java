package ninja.ranner.xogame.domain;

import ninja.ranner.xogame.util.stream.MyCollectors;
import ninja.ranner.xogame.util.stream.MyGatherers;

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

    Optional<Player> determineWinner() {
        return winningArrangements.stream()
                .map(this::determineWinner)
                .gather(MyGatherers.filterPresent())
                .findFirst();
    }

    private Optional<Player> determineWinner(List<Cell> cells) {
        return cells.stream()
                .map(board::get)

                // Make a grouping of player to number of cells filled by them
                .gather(MyGatherers.count())

                // Find the first player who filled three cells
                .collect(MyCollectors.findFirstBy(x -> x.getValue() == 3))
                .map(Map.Entry::getKey);
    }

    boolean isDraw() {
        return board.size() == 9;
    }

    public Map<Cell, Player> asMap() {
        return Map.copyOf(board);
    }

    // Commands

    void fill(Player player, Cell cell) {
        board.put(cell, player);
    }

}