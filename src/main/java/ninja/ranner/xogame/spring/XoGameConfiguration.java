package ninja.ranner.xogame.spring;

import ninja.ranner.xogame.application.port.GameRepository;
import ninja.ranner.xogame.domain.Cell;
import ninja.ranner.xogame.domain.Game;
import ninja.ranner.xogame.domain.GameId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class XoGameConfiguration {

    @Bean
    GameRepository gameRepository() {
        return new GameRepository() {
            @Override
            public Game save(Game game) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Optional<Game> findById(GameId gameId) {
                Game game = Game.create(GameId.random(), "Hard-coded Game");
                game.fillCell(Cell.at(0, 0));
                game.fillCell(Cell.at(1, 1));
                return Optional.of(game);
            }
        };
    }

}
