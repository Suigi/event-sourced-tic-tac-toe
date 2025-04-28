package ninja.ranner.xogame;

import ninja.ranner.xogame.application.port.EventStore;
import ninja.ranner.xogame.application.port.InMemoryEventStore;
import ninja.ranner.xogame.application.port.InMemoryGameRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class InMemoryStoresTestConfiguration {

    @Bean
    InMemoryGameRepository gameRepository(EventStore eventStore) {
        return new InMemoryGameRepository(eventStore);
    }

    @Bean
    InMemoryEventStore eventStore() {
        return new InMemoryEventStore();
    }

}
