package ninja.ranner.xogame.domain;

import java.util.List;

public class GameFactory {

    public static Game createDrawnGame() {
        return Game.reconstitute(Events.forDrawnGame(GameId.random()));
    }

    public static Game createGameWonByX() {
        // Game board state:
        // +-------+
        // | X . O |
        // | X . O |
        // | X . . |
        // +-------+
        GameCreated gameCreated = Events.gameCreated();
        return Game.reconstitute(List.of(
                gameCreated,
                Events.xFilledCell(0, 0),
                Events.oFilledCell(0, 2),
                Events.xFilledCell(1, 0),
                Events.oFilledCell(1, 2),
                Events.xFilledCell(2, 0),
                new GameWon(gameCreated.gameId(), Player.X)
        ));
    }

    public static Game createGameWonByO() {
        // Game board state:
        // +-------+
        // | X . O |
        // | X X O |
        // | . . O |
        // +-------+
        GameCreated gameCreated = Events.gameCreated();
        return Game.reconstitute(List.of(
                gameCreated,
                Events.xFilledCell(0, 0),
                Events.oFilledCell(0, 2),
                Events.xFilledCell(1, 0),
                Events.oFilledCell(1, 2),
                Events.xFilledCell(1, 1),
                Events.oFilledCell(2, 2),
                new GameWon(gameCreated.gameId(), Player.O)
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

        public static List<Event> forDrawnGame(GameId gameId) {
            // Game board state:
            // +-------+
            // | X O X |
            // | O O X |
            // | X X O |
            // +-------+
            return List.of(
                    new GameCreated(gameId, "Drawn Game"),
                    xFilledCell(0, 0),
                    oFilledCell(1, 1),
                    xFilledCell(2, 0),
                    oFilledCell(1, 0),
                    xFilledCell(1, 2),
                    oFilledCell(0, 1),
                    xFilledCell(2, 1),
                    oFilledCell(2, 2),
                    xFilledCell(0, 2),
                    new GameDrawn(gameId)
            );
        }
    }
}
