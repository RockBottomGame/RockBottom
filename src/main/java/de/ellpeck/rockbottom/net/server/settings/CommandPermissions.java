package de.ellpeck.rockbottom.net.server.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.settings.IJsonSettings;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandPermissions implements IJsonSettings {

    private final Map<UUID, Integer> commandLevels = new HashMap<>();

    @Override
    public void load(JsonObject object) {
        this.commandLevels.clear();

        if (object.has("players")) {
            JsonArray array = object.get("players").getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                JsonObject sub = array.get(i).getAsJsonObject();

                UUID id = UUID.fromString(sub.get("id").getAsString());
                int level = sub.get("level").getAsInt();
                this.commandLevels.put(id, level);
            }
        }
    }

    @Override
    public void save(JsonObject object) {
        JsonArray array = new JsonArray();
        for (Map.Entry<UUID, Integer> entry : this.commandLevels.entrySet()) {
            JsonObject sub = new JsonObject();
            sub.addProperty("id", entry.getKey().toString());
            sub.addProperty("level", entry.getValue());
            array.add(sub);
        }
        object.add("players", array);
    }

    @Override
    public File getSettingsFile(IDataManager manager) {
        return manager.getCommandPermsFile();
    }

    @Override
    public String getName() {
        return "Command permission settings";
    }

    public int getCommandLevel(AbstractEntityPlayer player) {
        return this.commandLevels.getOrDefault(player.getUniqueId(), 0);
    }

    public void setCommandLevel(UUID id, int level) {
        this.commandLevels.put(id, level);
    }
}
