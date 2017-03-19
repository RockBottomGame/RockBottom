package de.ellpeck.game.render.entity;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.world.IWorld;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class PlayerEntityRenderer implements IEntityRenderer<EntityPlayer>{

    @Override
    public void render(Game game, AssetManager manager, Graphics g, IWorld world, EntityPlayer entity, float x, float y){
        g.setColor(Color.orange);
        g.fillRect(x-0.5F, y-1.5F, 1F, 2F);
    }
}
