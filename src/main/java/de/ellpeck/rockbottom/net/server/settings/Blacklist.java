package de.ellpeck.rockbottom.net.server.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.settings.IJsonSettings;
import de.ellpeck.rockbottom.api.data.settings.IPropSettings;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class Blacklist implements IPropSettings, IJsonSettings{

    private final Map<UUID, String> blacklistedPlayers = new HashMap<>();

    @Override
    public void load(Properties props){
        this.blacklistedPlayers.clear();

        for(String s : props.stringPropertyNames()){
            this.blacklistedPlayers.put(UUID.fromString(s), props.getProperty(s));
        }
    }

    @Override
    public File getFile(IDataManager manager){
        return new File(manager.getGameDir(), "blacklist.properties");
    }

    @Override
    public void load(JsonObject object){
        this.blacklistedPlayers.clear();

        if(object.has("players")){
            JsonArray array = object.get("players").getAsJsonArray();
            for(int i = 0; i < array.size(); i++){
                JsonObject sub = array.get(i).getAsJsonObject();

                UUID id = UUID.fromString(sub.get("id").getAsString());
                String reason = sub.get("reason").getAsString();
                this.blacklistedPlayers.put(id, reason);
            }
        }
    }

    @Override
    public void save(JsonObject object){
        JsonArray array = new JsonArray();
        for(Map.Entry<UUID, String> entry : this.blacklistedPlayers.entrySet()){
            JsonObject sub = new JsonObject();
            sub.addProperty("id", entry.getKey().toString());
            sub.addProperty("reason", entry.getValue());
            array.add(sub);
        }
        object.add("players", array);
    }

    @Override
    public File getSettingsFile(IDataManager manager){
        return manager.getBlacklistFile();
    }

    @Override
    public String getName(){
        return "Blacklist";
    }

    public void add(UUID id, String reason){
        this.blacklistedPlayers.put(id, reason);
    }

    public void remove(UUID id){
        this.blacklistedPlayers.remove(id);
    }

    public boolean isBlacklisted(UUID id){
        return this.blacklistedPlayers.containsKey(id);
    }

    public String getBlacklistReason(UUID id){
        return this.blacklistedPlayers.get(id);
    }
}
