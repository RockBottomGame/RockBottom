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
        super.render(game, manager, g);

        if(this.tile.maxSmeltTime > 0 && this.tile.smeltTime > 0){
            int width = Util.ceil((double)this.tile.smeltTime/(double)this.tile.maxSmeltTime*41);

            g.setColor(PROGRESS_COLOR);
            g.fillRect(this.guiLeft+80, this.guiTop+15, width-1, 8);
        }

        if(this.tile.maxCoalTime > 0 && this.tile.coalTime > 0){
            int height = Util.ceil((double)this.tile.coalTime/(double)this.tile.maxCoalTime*19);

            g.setColor(FIRE_COLOR);
            g.fillRect(this.guiLeft+74, this.guiTop+48-height+1, 8, height-1);
        }

        g.setColor(Color.black);
        g.drawRect(this.guiLeft+80, this.guiTop+15, 40, 8);
        g.drawRect(this.guiLeft+74, this.guiTop+30, 8, 18);
    }
}
