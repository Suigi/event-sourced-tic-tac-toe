package ninja.ranner.xogame.spring;

import ninja.ranner.xogame.application.GameService;
import ninja.ranner.xogame.application.port.EventStore;
import ninja.ranner.xogame.application.port.GameIdGenerator;
import ninja.ranner.xogame.application.port.GameRepository;
import ninja.ranner.xogame.application.port.InMemoryEventStore;
import ninja.ranner.xogame.domain.GameId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class XoGameConfiguration {

    @Bean
    EventStore eventStore() {
        return new InMemoryEventStore();
    }

    @Bean
    GameRepository gameRepository(EventStore eventStore) {
        return new GameRepository(eventStore);
    }

    @Bean
    GameIdGenerator gameIdGenerator() {
        return GameId::random;
    }

    @Bean
    GameService gameService(GameRepository gameRepository, GameIdGenerator gameIdGenerator) {
        return new GameService(gameRepository, gameIdGenerator);
    }

}
