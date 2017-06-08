package de.ellpeck.rockbottom.game.gui.component;

import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.game.gui.Gui;
import de.ellpeck.rockbottom.game.util.Util;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ComponentHealth extends GuiComponent{

    public ComponentHealth(Gui gui, int x, int y, int sizeX, int sizeY){
        super(gui, x, y, sizeX, sizeY);
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g){
        if(game.isInWorld()){
            int healthParts = Util.floor(game.player.getHealth()/20);
            int maxHealthParts = Util.floor(game.player.getMaxHealth()/20);

            Image heart = manager.getImage("gui.heart");
            Image heartEmpty = manager.getImage("gui.heart_empty");

            int currX = 0;
            for(int i = 0; i < maxHealthParts; i++){
                Gui.drawScaledImage(g, healthParts > i ? heart : heartEmpty, this.x+currX, this.y, 0.75F, Color.white);
                currX += 13;
            }
        }
    }

    @Override
    public void renderOverlay(RockBottom game, AssetManager manager, Graphics g){
        if(game.isInWorld()){
            if(this.isMouseOver(game)){
                Gui.drawHoverInfoAtMouse(game, manager, g, false, 0, manager.localize("info.health")+":", game.player.getHealth()+"/"+game.player.getMaxHealth());
            }
        }
    }
}
