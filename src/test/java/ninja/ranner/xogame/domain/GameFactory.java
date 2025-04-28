package ninja.ranner.xogame.domain;

import java.util.List;

public class GameFactory {

    public static Game createDrawnGame() {
        // Game board state:
        // +-------+
        // | X O X |
        // | O O X |
        // | X X O |
        // +-------+
        return ninja.ranner.xogame.domain.Game.reconstitute(List.of(
                Events.gameCreated(),
                Events.xFilledCell(0, 0),
                Events.oFilledCell(1, 1),
                Events.xFilledCell(2, 0),
                Events.oFilledCell(1, 0),
                Events.xFilledCell(1, 2),
                Events.oFilledCell(0, 1),
                Events.xFilledCell(2, 1),
                Events.oFilledCell(2, 2),
                Events.xFilledCell(0, 2),
                new GameDrawn()
        ));
    }

    public static Game createGameWonByX() {
        // Game board state:
        // +-------+
        // | X . O |
        // | X . O |
        // | X . . |
        // +-------+
        return ninja.ranner.xogame.domain.Game.reconstitute(List.of(
                Events.gameCreated(),
                Events.xFilledCell(0, 0),
                Events.oFilledCell(0, 2),
                Events.xFilledCell(1, 0),
                Events.oFilledCell(1, 2),
                Events.xFilledCell(2, 0),
                new GameWon(Player.X)
        ));
    }

    public static Game createGameWonByO() {
        // Game board state:
        // +-------+
        // | X . O |
        // | X X O |
        // | . . O |
        // +-------+
        return Game.reconstitute(List.of(
                Events.gameCreated(),
                Events.xFilledCell(0, 0),
                Events.oFilledCell(0, 2),
                Events.xFilledCell(1, 0),
                Events.oFilledCell(1, 2),
                Events.xFilledCell(1, 1),
                Events.oFilledCell(2, 2),
                new GameWon(Player.O)
        ));
    }

    public static class Events {
        static GameCreated gameCreated() {
            return new GameCreated(GameId.random(), "IRRELEVANT GAME NAME");
        }

        static CellFilled xFilledCell(int x, int y) {
            return new CellFilled(Player.X, Cell.at(x, y));
        }

        static CellFilled oFilledCell(int x, int y) {
            return new CellFilled(Player.O, Cell.at(x, y));
        }
    }
}
