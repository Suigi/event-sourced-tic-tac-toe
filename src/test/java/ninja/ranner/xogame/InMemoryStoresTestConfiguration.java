package ninja.ranner.xogame;

import ninja.ranner.xogame.application.port.EventStore;
import ninja.ranner.xogame.application.port.GameRepository;
import ninja.ranner.xogame.application.port.InMemoryEventStore;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class InMemoryStoresTestConfiguration {

    @Bean
    GameRepository gameRepository(EventStore eventStore) {
        return new GameRepository(eventStore);
    }

    @Bean
    InMemoryEventStore eventStore() {
        return new InMemoryEventStore();
    }

}
