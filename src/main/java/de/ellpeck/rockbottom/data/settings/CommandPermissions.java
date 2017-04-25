package de.ellpeck.rockbottom.data.settings;

import de.ellpeck.rockbottom.data.DataManager;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

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
    public File getFile(DataManager manager){
        return manager.commandPermissionFile;
    }

    public int getCommandLevel(EntityPlayer player){
        return this.commandLevels.getOrDefault(player.getUniqueId(), 0);
    }

    public void setCommandLevel(EntityPlayer player, int level){
        this.setCommandLevel(player.getUniqueId(), level);
    }

    public void setCommandLevel(UUID id, int level){
        this.commandLevels.put(id, level);
    }
}