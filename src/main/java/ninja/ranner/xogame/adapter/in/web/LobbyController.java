package ninja.ranner.xogame.adapter.in.web;

import ninja.ranner.xogame.application.OpenGamesProjection;
import ninja.ranner.xogame.application.port.EventStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LobbyController {

    private final EventStore eventStore;

    public LobbyController(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @GetMapping("/")
    public String lobby(Model model) {
        OpenGamesProjection allGames = OpenGamesProjection.onDemand(eventStore);

        model.addAttribute("games", allGames.games());
        return "lobby";
    }

}
