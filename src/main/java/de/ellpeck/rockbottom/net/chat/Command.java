package de.ellpeck.rockbottom.net.chat;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

public abstract class Command{

    private final String name;
    private final int level;

    public Command(String name, int level){
        this.name = name;
        this.level = level;
    }

    public String getName(){
        return this.name;
    }

    public int getLevel(){
        return this.level;
    }

    public abstract String execute(String[] args, EntityPlayer player, RockBottom game, AssetManager manager);
}
