package de.ellpeck.rockbottom.net.server.settings;

import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.settings.IPropSettings;

import java.io.File;
import java.util.*;

public class Blacklist implements IPropSettings{

    private final Map<UUID, String> blacklistedPlayers = new HashMap<>();

    @Override
    public void load(Properties props){
        this.blacklistedPlayers.clear();

        for(String s : props.stringPropertyNames()){
            this.blacklistedPlayers.put(UUID.fromString(s), props.getProperty(s));
        }
    }

    @Override
    public void save(Properties props){
        for(Map.Entry<UUID, String> entry : this.blacklistedPlayers.entrySet()){
            props.setProperty(entry.getKey().toString(), entry.getValue());
        }
    }

    @Override
    public File getFile(IDataManager manager){
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
