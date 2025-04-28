package ninja.ranner.xogame.spring;

import ninja.ranner.xogame.application.port.EventStore;
import ninja.ranner.xogame.application.port.GameIdGenerator;
import ninja.ranner.xogame.application.port.GameRepository;
import ninja.ranner.xogame.application.port.InMemoryEventStore;
import ninja.ranner.xogame.domain.GameId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XoGameConfiguration {

    @Bean
    GameRepository gameRepository(EventStore eventStore) {
        return new GameRepository(eventStore);
    }

    @Bean
    EventStore eventStore() {
        return new InMemoryEventStore();
    }

    @Bean
    GameIdGenerator gameIdGenerator() {
        return GameId::random;
    }

}
