package de.ellpeck.rockbottom.net.chat;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

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

    public abstract String execute(String[] args, EntityPlayer player, RockBottom game, AssetManager manager, ChatLog chat);
}
