/*
 * This file ("CommandPermissions.java") is part of the RockBottomAPI by Ellpeck.
 * View the source code at <https://github.com/RockBottomGame/>.
 * View information on the project at <https://rockbottom.ellpeck.de/>.
 *
 * The RockBottomAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The RockBottomAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the RockBottomAPI. If not, see <http://www.gnu.org/licenses/>.
 *
 * © 2017 Ellpeck
 */

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
