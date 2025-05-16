package ninja.ranner.xogame.adapter.out.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import ninja.ranner.xogame.domain.Event;

class JsonEventSerializer implements JdbcEventStore.EventSerializer {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Override
    public String serialize(Event event) {
        try {
            return jsonMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Event deserialize(Class<? extends Event> eventType, String json) {
        try {
            return (Event) jsonMapper.readValue(json, (Class<?>) eventType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
