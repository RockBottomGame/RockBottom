package de.ellpeck.rockbottom.net.server.settings;

import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.settings.IPropSettings;

import java.io.File;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

public class Blacklist implements IPropSettings{

    private final Set<UUID> blacklistedPlayers = new HashSet<>();

    @Override
    public void load(Properties props){
        this.blacklistedPlayers.clear();

        for(String s : props.stringPropertyNames()){
            this.blacklistedPlayers.add(UUID.fromString(s));
        }
    }

    @Override
    public void save(Properties props){
        for(UUID id : this.blacklistedPlayers){
            props.setProperty(id.toString(), "");
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

    public void add(UUID id){
        this.blacklistedPlayers.add(id);
    }

    public void remove(UUID id){
        this.blacklistedPlayers.remove(id);
    }

    public boolean isBlacklisted(UUID id){
        return this.blacklistedPlayers.contains(id);
    }
}
