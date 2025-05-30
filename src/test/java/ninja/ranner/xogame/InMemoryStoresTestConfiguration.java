package ninja.ranner.xogame;

import ninja.ranner.xogame.application.GameService;
import ninja.ranner.xogame.application.port.ConfigurableGameIdGenerator;
import ninja.ranner.xogame.application.port.EventStore;
import ninja.ranner.xogame.application.port.GameRepository;
import ninja.ranner.xogame.application.port.InMemoryEventStore;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration(proxyBeanMethods = false)
public class InMemoryStoresTestConfiguration {

    @Bean
    GameRepository gameRepository(EventStore eventStore) {
        return new GameRepository(eventStore);
    }

    @Bean
    InMemoryEventStore eventStore() {
        return new InMemoryEventStore();
    }

    @Bean
    ConfigurableGameIdGenerator gameIdGenerator() {
        return new ConfigurableGameIdGenerator();
    }

    @Bean
    GameService GameService(GameRepository gameRepository, ConfigurableGameIdGenerator gameIdGenerator) {
        return new GameService(gameRepository, gameIdGenerator);
    }

}
