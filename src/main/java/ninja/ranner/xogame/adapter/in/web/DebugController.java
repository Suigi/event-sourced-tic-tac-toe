package ninja.ranner.xogame.adapter.in.web;

import ninja.ranner.xogame.domain.Cell;
import ninja.ranner.xogame.domain.Game;
import ninja.ranner.xogame.domain.GameId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/debug/")
public class DebugController {
    @GetMapping("game")
    public String game(Model model) {
        Game game = Game.create(GameId.random(), "GAME NAME");
        game.fillCell(Cell.at(1,1));
        game.fillCell(Cell.at(2,2));
        model.addAttribute("game", GameController.GameView.from(game));
        return "game";
    }

    @GetMapping("lobby")
    public String lobby(Model model) {
        model.addAttribute("games", List.of(
                new LobbyController.GameSummary(UUID.randomUUID().toString(), "First Game"),
                new LobbyController.GameSummary(UUID.randomUUID().toString(), "Second Game")
        ));
        return "lobby";
    }
}
