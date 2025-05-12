package ninja.ranner.xogame.adapter.out.jdbc;

import jakarta.annotation.Nonnull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseInitializer implements
        ApplicationListener<ApplicationReadyEvent> {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void onApplicationEvent(@Nonnull ApplicationReadyEvent event) {
        initialize();
    }

    void initialize() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS events
                (
                    aggregate_type text not null,
                    aggregate_id text not null,
                    event_index integer not null,
                    event_type text not null,
                    event_payload json not null,
                
                    constraint pk_events primary key (aggregate_type, aggregate_id, event_index)
                );
                """);
    }

}
