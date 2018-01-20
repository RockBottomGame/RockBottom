package de.ellpeck.rockbottom.net.server.settings;

import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.settings.IPropSettings;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class CommandPermissions implements IPropSettings{

    private final Map<UUID, Integer> commandLevels = new HashMap<>();

    @Override
    public void load(Properties props){
        this.commandLevels.clear();

        for(String key : props.stringPropertyNames()){
            int level = Integer.parseInt(props.getProperty(key));
            UUID id = UUID.fromString(key);

            this.commandLevels.put(id, level);
        }
    }

    @Override
    public void save(Properties props){
        for(Map.Entry<UUID, Integer> entry : this.commandLevels.entrySet()){
            props.setProperty(entry.getKey().toString(), entry.getValue().toString());
        }
    }

    @Override
    public File getFile(IDataManager manager){
        return manager.getCommandPermsFile();
    }

    @Override
    public String getName(){
        return "Command permission settings";
    }

    public int getCommandLevel(AbstractEntityPlayer player){
        return this.commandLevels.getOrDefault(player.getUniqueId(), 0);
    }

    public void setCommandLevel(UUID id, int level){
        this.commandLevels.put(id, level);
    }
}
