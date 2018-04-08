package de.ellpeck.rockbottom.net.server.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.settings.IJsonSettings;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Whitelist implements IJsonSettings{

    private final Set<UUID> whitelistedPlayers = new HashSet<>();
    private boolean isEnabled = false;

    @Override
    public void load(JsonObject object){
        this.isEnabled = this.get(object, "enabled", false);

        this.whitelistedPlayers.clear();
        if(object.has("players")){
            JsonArray array = object.get("players").getAsJsonArray();
            for(int i = 0; i < array.size(); i++){
                this.whitelistedPlayers.add(UUID.fromString(array.get(i).getAsString()));
            }
        }
    }

    @Override
    public void save(JsonObject object){
        object.addProperty("enabled", this.isEnabled);

        JsonArray array = new JsonArray();
        for(UUID id : this.whitelistedPlayers){
            array.add(id.toString());
        }
        object.add("players", array);
    }

    @Override
    public File getSettingsFile(IDataManager manager){
        return manager.getWhitelistFile();
    }

    @Override
    public String getName(){
        return "Whitelist";
    }

    public void add(UUID id){
        this.whitelistedPlayers.add(id);
    }

    public void remove(UUID id){
        this.whitelistedPlayers.remove(id);
    }

    public boolean isWhitelisted(UUID id){
        return this.whitelistedPlayers.contains(id);
    }

    public boolean isEnabled(){
        return this.isEnabled;
    }

    public void setEnabled(boolean enabled){
        this.isEnabled = enabled;
    }
}
