package com.github.hhhzzzsss.songplayer.stage;

import java.util.HashMap;
import java.util.Set;

public class StageTypeRegistry {
    public static final StageTypeRegistry instance = new StageTypeRegistry();
    private final HashMap<String, StageType> types = new HashMap<>();

    private StageTypeRegistry() {}

    public void registerStageTypes(StageType... types) {
        for (StageType type : types) {
            if (type.getIdentifier().contains(" ")) continue; // Spaces are not allowed.
            this.types.put(type.getIdentifier(), type);
        }
    }

    public StageType getType(String identifier) {
        return types.get(identifier);
    }

    public Set<String> getIdentifiers() {
        return types.keySet();
    }
}
