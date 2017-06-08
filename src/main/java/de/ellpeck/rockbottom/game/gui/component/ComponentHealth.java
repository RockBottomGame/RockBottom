package de.ellpeck.rockbottom.game.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.game.util.Util;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ComponentHealth extends GuiComponent{

    public ComponentHealth(Gui gui, int x, int y, int sizeX, int sizeY){
        super(gui, x, y, sizeX, sizeY);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        if(game.isInWorld()){
            int healthParts = Util.floor(game.getPlayer().getHealth()/20);
            int maxHealthParts = Util.floor(game.getPlayer().getMaxHealth()/20);

            Image heart = manager.getImage("gui.heart");
            Image heartEmpty = manager.getImage("gui.heart_empty");

            int currX = 0;
            for(int i = 0; i < maxHealthParts; i++){
                RockBottomAPI.getApiHandler().drawScaledImage(g, healthParts > i ? heart : heartEmpty, this.x+currX, this.y, 0.75F, Color.white);
                currX += 13;
            }
        }
    }

    @Override
    public void renderOverlay(IGameInstance game, IAssetManager manager, Graphics g){
        if(game.isInWorld()){
            if(this.isMouseOver(game)){
                RockBottomAPI.getApiHandler().drawHoverInfoAtMouse(game, manager, g, false, 0, manager.localize("info.health")+":", game.getPlayer().getHealth()+"/"+game.getPlayer().getMaxHealth());
            }
        }
    }
}
