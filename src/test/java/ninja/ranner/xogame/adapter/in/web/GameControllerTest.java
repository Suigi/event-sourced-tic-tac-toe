package ninja.ranner.xogame.adapter.in.web;

import ninja.ranner.xogame.application.port.GameRepository;
import ninja.ranner.xogame.application.port.InMemoryEventStore;
import ninja.ranner.xogame.domain.Cell;
import ninja.ranner.xogame.domain.Game;
import ninja.ranner.xogame.domain.GameFactory;
import ninja.ranner.xogame.domain.GameId;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class GameControllerTest {

    @Test
    void loadsGameFromRepository() {
        GameRepository gameRepository = new GameRepository(new InMemoryEventStore());
        GameController gameController = new GameController(gameRepository, GameId::random);
        GameId gameId = GameId.random();
        gameRepository.save(Game.create(gameId, "Game Name"));

        ConcurrentModel model = new ConcurrentModel();
        gameController.game(gameId.id().toString(), model);

        assertThat(model)
                .extracting("game", InstanceOfAssertFactories.type(GameController.GameView.class))
                .extracting(GameController.GameView::name)
                .isEqualTo("Game Name");
    }

    @Test
    void fill_fillsCell() {
        GameRepository gameRepository = new GameRepository(new InMemoryEventStore());
        GameController gameController = new GameController(gameRepository, GameId::random);
        GameId gameId = GameId.random();
        gameRepository.save(Game.create(gameId, "Game Name"));

        Collection<ModelAndView> modelsAndViews = gameController.fill(gameId.id().toString(), 1, 2);

        Map<String, Map<String, Object>> modelByView = modelsAndViews.stream().collect(Collectors.toMap(
                ModelAndView::getViewName,
                ModelAndView::getModel));
        assertThat(modelByView)
                .extractingByKey("board")
                .extracting("game", InstanceOfAssertFactories.type(GameController.GameView.class))
                .extracting(gameView -> gameView.cellAt(1, 2))
                .isEqualTo("X");
        assertThat(modelByView)
                .extractingByKey("game-result")
                .extracting("game", InstanceOfAssertFactories.type(GameController.GameView.class))
                .extracting(GameController.GameView::result)
                .isEqualTo("In Progress");
        assertThat(modelByView)
                .extractingByKey("game-result", InstanceOfAssertFactories.map(String.class, Object.class))
                .containsEntry("isHtmx", true);
    }

    @Nested
    public class GameViewTest {
        @Test
        void containsGameName() {
            Game game = Game.create(GameId.random(), "Game Name");

            GameController.GameView gameView = GameController.GameView.from(game);

            assertThat(gameView.name())
                    .isEqualTo("Game Name");
        }

        @Test
        void fillOfEmptyCellIsEmptyString() {
            Game game = Game.create(GameId.random(), "IRRELEVANT GAME NAME");

            GameController.GameView gameView = GameController.GameView.from(game);

            assertThat(gameView.cellAt(1, 1))
                    .isEqualTo("");
        }

        @Test
        void fillOfFilledCellIsNameOfThePlayer() {
            Game game = Game.create(GameId.random(), "IRRELEVANT GAME NAME");
            game.fillCell(Cell.at(1, 1));
            game.fillCell(Cell.at(2, 2));

            GameController.GameView gameView = GameController.GameView.from(game);

            assertThat(gameView.cellAt(1, 1))
                    .isEqualTo("X");
            assertThat(gameView.cellAt(2, 2))
                    .isEqualTo("O");
        }

        @Test
        void classOfEmptyCellIsOnlyCell() {
            Game game = Game.create(GameId.random(), "IRRELEVANT GAME NAME");

            GameController.GameView gameView = GameController.GameView.from(game);

            assertThat(gameView.cssClassFor(0, 0))
                    .isEqualTo("cell");
        }

        @Test
        void classOfFilledCellContainsNameOfThePlayer() {
            Game game = Game.create(GameId.random(), "IRRELEVANT GAME NAME");
            game.fillCell(Cell.at(1, 1));
            game.fillCell(Cell.at(2, 2));

            GameController.GameView gameView = GameController.GameView.from(game);

            assertThat(gameView.cssClassFor(1, 1))
                    .isEqualTo("cell player-x");
            assertThat(gameView.cssClassFor(2, 2))
                    .isEqualTo("cell player-o");
        }

        @Test
        void resultOfInProgressGameIsInProgress() {
            Game game = Game.create(GameId.random(), "IRRELEVANT GAME NAME");
            game.fillCell(Cell.at(1, 1));
            game.fillCell(Cell.at(2, 2));

            GameController.GameView gameView = GameController.GameView.from(game);

            assertThat(gameView.result())
                    .isEqualTo("In Progress");
        }

        @Test
        void resultOfDrawnGameIsInDrawn() {
            Game drawnGame = GameFactory.createDrawnGame();

            GameController.GameView gameView = GameController.GameView.from(drawnGame);

            assertThat(gameView.result())
                    .isEqualTo("It's a draw!");
        }

        @Test
        void resultOfGameWonByXIndicatesThatXWon() {
            Game drawnGame = GameFactory.createGameWonByX();

            GameController.GameView gameView = GameController.GameView.from(drawnGame);

            assertThat(gameView.result())
                    .isEqualTo("Player X wins!");
        }

        @Test
        void resultOfGameWonByOIndicatesThatOWon() {
            Game drawnGame = GameFactory.createGameWonByO();

            GameController.GameView gameView = GameController.GameView.from(drawnGame);

            assertThat(gameView.result())
                    .isEqualTo("Player O wins!");
        }
    }

    @Test
    void createGame_storesCreatedGame() {
        GameRepository gameRepository = new GameRepository(new InMemoryEventStore());
        GameId newGameId = GameId.random();
        GameController gameController = new GameController(gameRepository, () -> newGameId);

        gameController.createGame("My New Game");

        assertThat(gameRepository.findById(newGameId))
                .get()
                .extracting(Game::name)
                .isEqualTo("My New Game");
    }
}