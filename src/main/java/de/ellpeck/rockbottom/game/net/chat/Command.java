package de.ellpeck.rockbottom.game.net.chat;

import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.game.world.entity.player.EntityPlayer;

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

    public abstract String execute(String[] args, EntityPlayer player, String playerName, RockBottom game, AssetManager manager, ChatLog chat);
}
