package ninja.ranner.xogame.adapter.in.web;

import ninja.ranner.xogame.application.port.GameRepository;
import ninja.ranner.xogame.domain.Cell;
import ninja.ranner.xogame.domain.Game;
import ninja.ranner.xogame.domain.GameId;
import ninja.ranner.xogame.domain.Player;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
        Optional<Game> game = gameRepository.findById(GameId.of(UUID.fromString(gameIdString)));
        if (game.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        model.addAttribute("game", GameView.from(game.get()));
        return "game";
    }

    public record GameView(String name, Map<Cell, Player> cells) {
        public static GameView from(Game game) {
            return new GameView(
                    game.name(),
                    game.boardMap()
            );
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
