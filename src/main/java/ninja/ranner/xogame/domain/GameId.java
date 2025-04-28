package ninja.ranner.xogame.domain;

import java.util.UUID;

public record GameId(UUID uuid) implements Identifier {
    public static GameId of(UUID id) {
        return new GameId(id);
    }

    public static GameId random() {
        return new GameId(UUID.randomUUID());
    }
}
