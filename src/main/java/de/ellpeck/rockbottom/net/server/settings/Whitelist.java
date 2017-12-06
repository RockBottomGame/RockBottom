package de.ellpeck.rockbottom.net.server.settings;

import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.settings.IPropSettings;

import java.io.File;
import java.util.*;

public class Whitelist implements IPropSettings{

    private final Set<UUID> whitelistedPlayers = new HashSet<>();
    private boolean isEnabled = false;

    @Override
    public void load(Properties props){
        this.whitelistedPlayers.clear();

        for(String s : props.stringPropertyNames()){
            if(s.equals("enabled")){
                this.isEnabled = Objects.equals(props.getProperty(s), "true");
            }
            else{
                this.whitelistedPlayers.add(UUID.fromString(s));
            }
        }
    }

    @Override
    public void save(Properties props){
        props.setProperty("enabled", String.valueOf(this.isEnabled));

        for(UUID id : this.whitelistedPlayers){
            props.setProperty(id.toString(), "");
        }
    }

    @Override
    public File getFile(IDataManager manager){
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
