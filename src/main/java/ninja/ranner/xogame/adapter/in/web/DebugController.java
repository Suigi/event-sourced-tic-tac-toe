package ninja.ranner.xogame.adapter.in.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/debug/")
public class DebugController {
    @GetMapping("game")
    public String game(Model model) {
        model.addAttribute("game",
                new GameController.GameView("GAME_NAME"));
        return "game";
    }
}
