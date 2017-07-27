package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.init.AbstractGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ComponentHealth extends GuiComponent{

    private static final IResourceName TEX_HEART = AbstractGame.internalRes("gui.heart");
    private static final IResourceName TEX_HEART_EMPTY = AbstractGame.internalRes("gui.heart_empty");
    private static final IResourceName LOC_HEALTH = AbstractGame.internalRes("info.health");

    public ComponentHealth(Gui gui, int x, int y, int sizeX, int sizeY){
        super(gui, x, y, sizeX, sizeY);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        if(game.getWorld() != null){
            int healthParts = Util.floor(game.getPlayer().getHealth()/20);
            int maxHealthParts = Util.floor(game.getPlayer().getMaxHealth()/20);

            Image heart = manager.getTexture(TEX_HEART);
            Image heartEmpty = manager.getTexture(TEX_HEART_EMPTY);

            int currX = 0;
            for(int i = 0; i < maxHealthParts; i++){
                Image toUse = healthParts > i ? heart : heartEmpty;
                toUse.draw(this.x+currX, this.y, toUse.getWidth()*0.75F, toUse.getHeight()*0.75F, Color.white);
                currX += 13;
            }
        }
    }

    @Override
    public void renderOverlay(IGameInstance game, IAssetManager manager, Graphics g){
        if(game.getWorld() != null){
            if(this.isMouseOverPrioritized(game)){
                RockBottomAPI.getApiHandler().drawHoverInfoAtMouse(game, manager, g, false, 0, manager.localize(LOC_HEALTH)+":", game.getPlayer().getHealth()+"/"+game.getPlayer().getMaxHealth());
            }
        }
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("health");
    }
}
