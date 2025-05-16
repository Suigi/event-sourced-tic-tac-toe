package ninja.ranner.xogame.spring;

import ninja.ranner.xogame.adapter.out.jdbc.GameEventNameMapper;
import ninja.ranner.xogame.adapter.out.jdbc.JdbcEventStore;
import ninja.ranner.xogame.application.GameService;
import ninja.ranner.xogame.application.port.EventStore;
import ninja.ranner.xogame.application.port.GameIdGenerator;
import ninja.ranner.xogame.application.port.GameRepository;
import ninja.ranner.xogame.domain.GameId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration(proxyBeanMethods = false)
public class XoGameConfiguration {

    @Bean
    EventStore eventStore(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new JdbcEventStore(
                namedParameterJdbcTemplate,
                new GameEventNameMapper()
        );
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
