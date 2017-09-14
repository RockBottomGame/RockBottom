package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.tex.ITexture;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.init.AbstractGame;

public class ComponentHealth extends GuiComponent{

    private static final IResourceName TEX_HEART = AbstractGame.internalRes("gui.heart");
    private static final IResourceName TEX_HEART_EMPTY = AbstractGame.internalRes("gui.heart_empty");
    private static final IResourceName LOC_HEALTH = AbstractGame.internalRes("info.health");

    public ComponentHealth(Gui gui, int x, int y, int sizeX, int sizeY){
        super(gui, x, y, sizeX, sizeY);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IGraphics g, int x, int y){
        if(game.getWorld() != null){
            int healthParts = Util.floor(game.getPlayer().getHealth()/20);
            int maxHealthParts = Util.floor(game.getPlayer().getMaxHealth()/20);

            ITexture heart = manager.getTexture(TEX_HEART);
            ITexture heartEmpty = manager.getTexture(TEX_HEART_EMPTY);

            int currX = 0;
            for(int i = 0; i < maxHealthParts; i++){
                ITexture toUse = healthParts > i ? heart : heartEmpty;
                toUse.draw(x+currX, y, toUse.getWidth()*0.75F, toUse.getHeight()*0.75F);
                currX += 13;
            }
        }
    }

    @Override
    public void renderOverlay(IGameInstance game, IAssetManager manager, IGraphics g, int x, int y){
        if(game.getWorld() != null){
            if(this.isMouseOverPrioritized(game)){
                g.drawHoverInfoAtMouse(game, manager, false, 0, manager.localize(LOC_HEALTH)+":", game.getPlayer().getHealth()+"/"+game.getPlayer().getMaxHealth());
            }
        }
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("health");
    }
}
