package de.ellpeck.rockbottom.api.net.chat;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;

public abstract class Command{

    private final String name;
    private final String description;
    private final int level;

    public Command(String name, String description, int level){
        this.name = name;
        this.description = description;
        this.level = level;
    }

    public String getName(){
        return this.name;
    }

    public String getDescription(){
        return this.description;
    }

    public int getLevel(){
        return this.level;
    }

    public abstract String execute(String[] args, AbstractEntityPlayer player, String playerName, IGameInstance game, IAssetManager manager, IChatLog chat);
}
