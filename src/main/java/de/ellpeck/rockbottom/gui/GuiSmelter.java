package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.util.Util;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySmelter;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class GuiSmelter extends GuiContainer{

    private static final Color PROGRESS_COLOR = new Color(0.1F, 0.5F, 0.1F);
    private static final Color FIRE_COLOR = new Color(0.5F, 0.1F, 0.1F);

    private final TileEntitySmelter tile;

    public GuiSmelter(EntityPlayer player, TileEntitySmelter tile){
        super(player, 158, 150);
        this.tile = tile;
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g){
        if(this.tile.maxSmeltTime > 0 && this.tile.smeltTime > 0){
            float width = (float)this.tile.smeltTime/(float)this.tile.maxSmeltTime*40F;

            g.setColor(PROGRESS_COLOR);
            g.fillRect(this.guiLeft+80, this.guiTop+15, width, 8);
        }

        if(this.tile.maxCoalTime > 0 && this.tile.coalTime > 0){
            float height = (float)this.tile.coalTime/(float)this.tile.maxCoalTime*18F;

            g.setColor(FIRE_COLOR);
            g.fillRect(this.guiLeft+74, this.guiTop+48-height, 8, height);
        }

        g.setColor(Color.black);
        g.drawRect(this.guiLeft+80, this.guiTop+15, 40, 8);
        g.drawRect(this.guiLeft+74, this.guiTop+30, 8, 18);

        super.render(game, manager, g);
    }
}
