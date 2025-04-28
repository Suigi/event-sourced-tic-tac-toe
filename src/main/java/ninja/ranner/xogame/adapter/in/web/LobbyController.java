package ninja.ranner.xogame.adapter.in.web;

import ninja.ranner.xogame.application.port.EventStore;
import ninja.ranner.xogame.domain.Event;
import ninja.ranner.xogame.domain.GameCreated;
import ninja.ranner.xogame.domain.GameId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class LobbyController {

    private final EventStore eventStore;

    public LobbyController(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @GetMapping("/")
    public String lobby(Model model) {
        List<Event> events = eventStore.findAllForType(GameCreated.class);
        AllGamesProjection allGames = new AllGamesProjection();
        events.forEach(allGames::apply);

        model.addAttribute("games", allGames.games());
        return "lobby";
    }

    public record GameSummary(String gameId, String name) {}

    static class AllGamesProjection {
        private final List<GameSummary> games = new ArrayList<>();

        public void apply(Event event) {
            if (event instanceof GameCreated(GameId gameId, String newGameName)) {
                games.add(new GameSummary(
                        gameId.uuid().toString(),
                        newGameName));
            }
        }

        public List<GameSummary> games() {
            return games;
        }
    }

}
