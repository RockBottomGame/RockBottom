package de.ellpeck.rockbottom.render.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class PlayerEntityRenderer implements IEntityRenderer<EntityPlayer>{

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, EntityPlayer entity, float x, float y, Color light){
        g.setColor(entity.getColor().multiply(light));
        g.fillRect(x-0.5F, y-1.5F, 1F, 2F);
    }
}
