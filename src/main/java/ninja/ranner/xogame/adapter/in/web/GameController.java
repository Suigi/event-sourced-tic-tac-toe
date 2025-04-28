package ninja.ranner.xogame.adapter.in.web;

import ninja.ranner.xogame.application.port.GameRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/games/")
public class GameController {

    public GameController(GameRepository gameRepository) {

    }

    @GetMapping("{gameId}")
    public String game(
            @PathVariable("gameId") String gameIdString,
            Model model) {
        model.addAttribute("game", new GameView("The name of the game"));
        return "game";
    }

    public record GameView(String name) {}
}
