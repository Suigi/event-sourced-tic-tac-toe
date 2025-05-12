package ninja.ranner.xogame.adapter.out.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@JdbcTest
class DatabaseInitializerJdbcTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void createsEventsTable() {
        DatabaseInitializer databaseInitializer = new DatabaseInitializer(jdbcTemplate);

        databaseInitializer.initialize();

        Map<String, Object> stringObjectMap = jdbcTemplate.queryForMap("""
                SELECT EXISTS (
                    SELECT 1
                    FROM information_schema.tables
                    WHERE table_name = 'events'
                ) AS table_existence;
                """);
        assertThat(stringObjectMap)
                .containsEntry("table_existence", true);
    }

    @Test
    void initializationIsIdempotent() {
        DatabaseInitializer databaseInitializer = new DatabaseInitializer(jdbcTemplate);
        databaseInitializer.initialize();

        assertThatNoException()
                .isThrownBy(databaseInitializer::initialize);
    }
}