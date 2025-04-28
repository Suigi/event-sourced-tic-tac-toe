package ninja.ranner.xogame.adapter.in.web;

import ninja.ranner.xogame.domain.GameCreated;
import ninja.ranner.xogame.domain.GameId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventViewTest {

    @Test
    void typeNameContainsClassSimpleName() {
        GameCreated event = new GameCreated(GameId.random(), "Game Name");

        EventView eventView = EventView.from(event);

        assertThat(eventView.eventName())
                .isEqualTo("GameCreated");
    }

    @Test
    void fieldsContainsAllEventRecordComponents() {
        GameId gameId = GameId.random();
        GameCreated event = new GameCreated(gameId, "Game Name");

        EventView eventView = EventView.from(event);

        assertThat(eventView.fields())
                .containsExactlyInAnyOrder(
                        new EventView.FieldView("newGameName", "Game Name"),
                        new EventView.FieldView("gameId", gameId.toString())
                );

    }

}