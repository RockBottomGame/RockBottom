package de.ellpeck.rockbottom.settings;

import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

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

    public int getCommandLevel(EntityPlayer player){
        return this.commandLevels.getOrDefault(player.getUniqueId(), 0);
    }

    public void setCommandLevel(EntityPlayer player, int level){
        this.commandLevels.put(player.getUniqueId(), level);
    }
}
