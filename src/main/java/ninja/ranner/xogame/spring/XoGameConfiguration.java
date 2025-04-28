package ninja.ranner.xogame.spring;

import ninja.ranner.xogame.application.port.EventStore;
import ninja.ranner.xogame.application.port.GameRepository;
import ninja.ranner.xogame.application.port.InMemoryEventStore;
import ninja.ranner.xogame.application.port.InMemoryGameRepository;
import ninja.ranner.xogame.domain.Cell;
import ninja.ranner.xogame.domain.Game;
import ninja.ranner.xogame.domain.GameId;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class XoGameConfiguration {

    @Bean
    GameRepository gameRepository(EventStore eventStore) {
        return new InMemoryGameRepository(eventStore);
    }

    @Bean
    EventStore eventStore() {
        return new InMemoryEventStore();
    }

    @Bean
    ApplicationRunner initializeGame(GameRepository gameRepository) {
        return _ -> {
            Game game = Game.create(
                    GameId.of(UUID.fromString("00000000-0000-0000-0000-000000000000")),
                    "Hard-coded Game");
            game.fillCell(Cell.at(0, 0));
            game.fillCell(Cell.at(1, 1));
            gameRepository.save(game);
        };
    }


}
