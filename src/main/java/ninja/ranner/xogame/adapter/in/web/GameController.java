package ninja.ranner.xogame.adapter.in.web;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import ninja.ranner.xogame.application.port.GameRepository;
import ninja.ranner.xogame.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
@RequestMapping("/games/")
public class GameController {

    private final GameRepository gameRepository;

    public GameController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @GetMapping("{gameId}")
    public String game(
            @PathVariable("gameId") String gameIdString,
            Model model) {
        Game game = findOrThrow(gameIdString);
        model.addAttribute("game", GameView.from(game));
        return "game";
    }

    @HxRequest
    @PostMapping("/{gameId}/fill")
    public Collection<ModelAndView> fill(@PathVariable("gameId") String gameIdString,
                                         @RequestParam("x") int x,
                                         @RequestParam("y") int y) {
        Game game = findOrThrow(gameIdString);
        game.fillCell(Cell.at(x, y));
        Game savedGame = gameRepository.save(game);
        return List.of(
                new ModelAndView("board", Map.of("game", GameView.from(savedGame))),
                new ModelAndView("game-result", Map.of(
                        "game", GameView.from(savedGame),
                        "isHtmx", true))
        );
    }

    private Game findOrThrow(String gameIdString) {
        return gameRepository
                .findById(GameId.of(UUID.fromString(gameIdString)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public record GameView(String gameId, String name, String result, Map<Cell, Player> cells) {
        public static GameView from(Game game) {
            return new GameView(
                    game.id().id().toString(),
                    game.name(),
                    mapResult(game.result()),
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

    }
}
