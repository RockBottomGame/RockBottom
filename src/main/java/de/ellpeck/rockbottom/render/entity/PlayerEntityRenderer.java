package de.ellpeck.rockbottom.render.entity;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.world.IWorld;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class PlayerEntityRenderer implements IEntityRenderer<EntityPlayer>{

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g, IWorld world, EntityPlayer entity, float x, float y, Color filter){
        g.setColor(entity.color.multiply(filter));
        g.fillRect(x-0.5F, y-1.5F, 1F, 2F);
    }
}
