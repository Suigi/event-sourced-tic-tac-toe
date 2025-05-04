package ninja.ranner.xogame.adapter.in.web;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import ninja.ranner.xogame.application.GameService;
import ninja.ranner.xogame.application.port.EventStore;
import ninja.ranner.xogame.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;
    private final EventStore eventStore;

    public GameController(GameService gameService, EventStore eventStore) {
        this.gameService = gameService;
        this.eventStore = eventStore;
    }

    @GetMapping("/{gameId}")
    public String showGame(
            @PathVariable("gameId") String gameIdString,
            Model model) {
        Game game = findOrThrow(gameIdString);
        List<Event> events = eventStore.findAllForId(game.id()).orElseThrow();
        model.addAttribute("game", GameView.of(game));
        model.addAttribute("gameEvents",
                events.stream()
                      .map(EventView::from)
                      .toList()
                      .reversed()
        );
        return "game";
    }

    @HxRequest
    @GetMapping("/{gameId}")
    public Collection<ModelAndView> showGameHtmx(
            @PathVariable("gameId") String gameIdString,
            @RequestParam(value = "numberOfEventsToSkip", defaultValue = "0") int numberOfEventsToSkip) {
        List<Event> events = eventStore
                .findAllForId(GameId.of(UUID.fromString(gameIdString)))
                .orElseThrow();
        Game game = Game.reconstitute(events.subList(0, events.size() - numberOfEventsToSkip));
        GameView gameView = numberOfEventsToSkip > 0
                ? GameView.ofHistorical(game)
                : GameView.of(game);
        return List.of(
                new ModelAndView("board", Map.of(
                        "game", gameView
                )),
                new ModelAndView("game-result", Map.of(
                        "isHtmx", true,
                        "game", gameView
                )),
                new ModelAndView("events-list", Map.of(
                        "isHtmx", true,
                        "skippedEvents", numberOfEventsToSkip,
                        "baseUrl", "/games/" + gameIdString,
                        "events", events.stream().map(EventView::from).toList()
                ))
        );
    }

    @HxRequest
    @PostMapping("/{gameId}/fill")
    public Collection<ModelAndView> fill(@PathVariable("gameId") String gameIdString,
                                         @RequestParam("x") int x,
                                         @RequestParam("y") int y) {
        Game savedGame = gameService.fill(findOrThrow(gameIdString), Cell.at(x, y));
        List<Event> events = eventStore.findAllForId(savedGame.id()).orElseThrow();
        return List.of(
                new ModelAndView("board", Map.of(
                        "game", GameView.of(savedGame)
                )),
                new ModelAndView("game-result", Map.of(
                        "isHtmx", true,
                        "game", GameView.of(savedGame)
                )),
                new ModelAndView("events-list", Map.of(
                        "isHtmx", true,
                        "events", events.stream()
                                        .map(EventView::from)
                                        .toList()
                                        .reversed(),
                        "skippedEvents", 0,
                        "baseUrl", "/games/" + gameIdString
                ))
        );
    }

    @PostMapping()
    public String createGame(@RequestParam("gameName") String name) {
        Game game = gameService.create(name);
        return "redirect:/games/" + game.id().uuid().toString();
    }

    private Game findOrThrow(String gameIdString) {
        return gameService.find(GameId.of(UUID.fromString(gameIdString)))
                          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public record GameView(
            String gameId,
            String name,
            String result,
            boolean isPlayable,
            Map<Cell, Player> cells,
            String currentPlayerClass) {
        public static GameView of(Game game) {
            return new GameView(
                    game.id().uuid().toString(),
                    game.name(),
                    mapResult(game.result()),
                    game.result() == GameResult.GAME_IN_PROGRESS,
                    game.boardMap(),
                    mapTurn(game));
        }

        public static GameView ofHistorical(Game game) {
            return new GameView(
                    game.id().uuid().toString(),
                    game.name(),
                    mapResult(game.result()),
                    false,
                    game.boardMap(),
                    mapTurn(game));
        }

        private static String mapTurn(Game game) {
            if (game.result() != GameResult.GAME_IN_PROGRESS) {
                return "no-turn";
            }
            return switch (game.currentPlayer()) {
                case X -> "x-turn";
                case O -> "o-turn";
            };
        }

        private static String mapResult(GameResult result) {
            return switch (result) {
                case GAME_IN_PROGRESS -> "In Progress";
                case PLAYER_X_WINS -> "Player X wins!";
                case PLAYER_O_WINS -> "Player O wins!";
                case DRAW -> "It's a draw!";
            };
        }

        public String cellAt(int x, int y) {
            return Optional
                    .ofNullable(cells.get(new Cell(x, y)))
                    .map(Player::toString)
                    .orElse("");
        }

        public String cssClassFor(int x, int y) {
            return Optional
                    .ofNullable(cells.get(new Cell(x, y)))
                    .map(player -> switch (player) {
                        case X -> "cell player-x";
                        case O -> "cell player-o";
                    })
                    .orElse("cell");
        }

        public boolean canPlayCell(int x, int y) {
            if (!isPlayable) {
                return false;
            }
            return cells.get(Cell.at(x, y)) == null;
        }

    }
}
