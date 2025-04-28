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
        model.addAttribute("game", GameView.from(game));
        model.addAttribute("gameEvents",
                events.stream()
                      .map(EventView::from)
                      .toList()
                      .reversed()
        );
        return "game";
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
                        "game", GameView.from(savedGame)
                )),
                new ModelAndView("game-result", Map.of(
                        "isHtmx", true,
                        "game", GameView.from(savedGame)
                )),
                new ModelAndView("events-list", Map.of(
                        "isHtmx", true,
                        "events", events.stream()
                                        .map(EventView::from)
                                        .toList()
                                        .reversed()
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
            boolean isOver,
            Map<Cell, Player> cells
    ) {
        public static GameView from(Game game) {
            return new GameView(
                    game.id().uuid().toString(),
                    game.name(),
                    mapResult(game.result()),
                    game.result() != GameResult.GAME_IN_PROGRESS,
                    game.boardMap()
            );
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
            if (isOver) {
                return false;
            }
            return cells.get(Cell.at(x, y)) == null;
        }
    }
}
