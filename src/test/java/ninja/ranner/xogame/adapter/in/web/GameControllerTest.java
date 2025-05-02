package ninja.ranner.xogame.adapter.in.web;

import ninja.ranner.xogame.application.GameService;
import ninja.ranner.xogame.application.port.GameRepository;
import ninja.ranner.xogame.application.port.InMemoryEventStore;
import ninja.ranner.xogame.domain.*;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GameControllerTest {

    @Test
    void showGame_loadsGameFromRepository() {
        Fixture fixture = Fixture.create();
        GameId gameId = GameId.random();
        Game game = Game.create(gameId, "Game Name");
        game.fillCell(Cell.at(0, 1));
        fixture.gameRepository().save(game);

        ConcurrentModel model = new ConcurrentModel();
        fixture.gameController().showGame(gameId.uuid().toString(), model);

        assertThat(model)
                .extracting("game", InstanceOfAssertFactories.type(GameController.GameView.class))
                .extracting(GameController.GameView::name)
                .isEqualTo("Game Name");
        assertThat(model)
                .extracting("gameEvents", InstanceOfAssertFactories.list(EventView.class))
                .containsExactly(
                        EventView.from(new CellFilled(Player.X, Cell.at(0, 1))),
                        EventView.from(new GameCreated(gameId, "Game Name"))
                );
    }

    @Test
    void fill_fillsCell() {
        Fixture fixture = Fixture.create();
        GameId gameId = GameId.random();
        fixture.gameRepository.save(Game.create(gameId, "Game Name"));

        Collection<ModelAndView> modelsAndViews = fixture.gameController.fill(gameId.uuid().toString(), 1, 2);

        assertThat(modelForView(modelsAndViews, "board"))
                .extracting("game", InstanceOfAssertFactories.type(GameController.GameView.class))
                .extracting(gameView -> gameView.cellAt(1, 2))
                .isEqualTo("X");
    }

    @Test
    void fill_updatesGameResult_outOfBand() {
        Fixture fixture = Fixture.create();
        GameId gameId = GameId.random();
        fixture.gameRepository.save(Game.create(gameId, "Game Name"));

        Collection<ModelAndView> modelsAndViews = fixture.gameController.fill(gameId.uuid().toString(), 1, 2);

        Map<String, Object> model = modelForView(modelsAndViews, "game-result");
        assertThat(model)
                .containsEntry("isHtmx", true)
                .extracting("game", InstanceOfAssertFactories.type(GameController.GameView.class))
                .extracting(GameController.GameView::result)
                .isEqualTo("In Progress");
    }

    @Test
    void fill_updatesGameEvents_outOfBand() {
        Fixture fixture = Fixture.create();
        GameId gameId = GameId.random();
        Game game = Game.create(gameId, "Game Name");
        fixture.gameRepository.save(game);

        Collection<ModelAndView> modelsAndViews = fixture.gameController.fill(gameId.uuid().toString(), 1, 2);

        assertThat(modelForView(modelsAndViews, "events-list"))
                .containsEntry("isHtmx", true)
                .extracting("events", InstanceOfAssertFactories.list(EventView.class))
                .extracting(EventView::eventName)
                .containsExactly(
                        "CellFilled",
                        "GameCreated"
                );
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

        @Test
        void currentTurnClass_whenItsPlayerXsTurn() {
            Game game = Game.create(GameId.random(), "Player X's turn");

            GameController.GameView gameView = GameController.GameView.from(game);

            assertThat(gameView.currentPlayerClass())
                    .isEqualTo("x-turn");
        }

        @Test
        void currentTurnClass_whenItsPlayerOsTurn() {
            Game game = Game.create(GameId.random(), "Player X's turn");
            game.fillCell(Cell.at(1, 1));

            GameController.GameView gameView = GameController.GameView.from(game);

            assertThat(gameView.currentPlayerClass())
                    .isEqualTo("o-turn");
        }

        @ParameterizedTest
        @MethodSource("gamesThatAreOver")
        void currentTurnClass_whenGameIsOver_isNoTurn(Game game) {
            GameController.GameView gameView = GameController.GameView.from(game);

            assertThat(gameView.currentPlayerClass())
                    .isEqualTo("no-turn");
        }

        public static Stream<Game> gamesThatAreOver() {
            return Stream.of(
                    GameFactory.createDrawnGame(),
                    GameFactory.createGameWonByX(),
                    GameFactory.createGameWonByO()
            );
        }

    }

    @Test
    void createGame_storesCreatedGame() {
        InMemoryEventStore eventStore = new InMemoryEventStore();
        GameRepository gameRepository = new GameRepository(eventStore);
        GameId newGameId = GameId.random();
        GameService gameService = new GameService(gameRepository, () -> newGameId);
        GameController gameController = new GameController(gameService, eventStore);

        gameController.createGame("My New Game");

        assertThat(gameRepository.findById(newGameId))
                .get()
                .extracting(Game::name)
                .isEqualTo("My New Game");
    }

    private record Fixture(GameRepository gameRepository, GameController gameController) {
        private static Fixture create() {
            InMemoryEventStore eventStore = new InMemoryEventStore();
            GameRepository gameRepository = new GameRepository(eventStore);
            GameController gameController = new GameController(GameService.createForTest(gameRepository), eventStore);
            return new Fixture(gameRepository, gameController);
        }
    }

    private Map<String, Object> modelForView(Collection<ModelAndView> modelsAndViews, String viewName) {
        Map<String, Map<String, Object>> modelByView = modelsAndViews.stream().collect(Collectors.toMap(
                ModelAndView::getViewName,
                ModelAndView::getModel));
        assertThat(modelByView)
                .as("collection contains model for view " + viewName)
                .containsKey(viewName);
        return modelByView.get(viewName);
    }

}