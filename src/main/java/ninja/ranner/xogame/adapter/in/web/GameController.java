package ninja.ranner.xogame.adapter.in.web;

import ninja.ranner.xogame.application.port.GameRepository;
import ninja.ranner.xogame.domain.Game;
import ninja.ranner.xogame.domain.GameId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
        model.addAttribute("game", GameView.from(game.get()));
        return "game";
    }

    public record GameView(String name) {
        public static GameView from(Game game) {
            return new GameView(
                    game.name()
            );
        }
    }
}
