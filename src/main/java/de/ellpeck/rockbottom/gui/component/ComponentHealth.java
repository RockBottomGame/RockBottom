package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class ComponentHealth extends GuiComponent{

    private static final ResourceName TEX_HEART = ResourceName.intern("gui.heart");
    private static final ResourceName TEX_HEART_EMPTY = ResourceName.intern("gui.heart_empty");
    private static final ResourceName LOC_HEALTH = ResourceName.intern("info.health");

    public ComponentHealth(Gui gui, int x, int y, int sizeX, int sizeY){
        super(gui, x, y, sizeX, sizeY);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, int x, int y){
        if(game.getWorld() != null){
            int healthParts = Util.floor(game.getPlayer().getHealth()/20);
            int maxHealthParts = Util.floor(game.getPlayer().getMaxHealth()/20);

            ITexture heart = manager.getTexture(TEX_HEART);
            ITexture heartEmpty = manager.getTexture(TEX_HEART_EMPTY);

            int currX = 0;
            for(int i = 0; i < maxHealthParts; i++){
                float alpha = 1F;

                ITexture toUse;
                if(healthParts >= i){
                    toUse = heart;

                    if(healthParts == i){
                        alpha = ((float)game.getPlayer().getHealth()%20)/20F;
                        heartEmpty.draw(x+currX, y, heartEmpty.getRenderWidth(), heartEmpty.getRenderHeight());
                    }
                }
                else{
                    toUse = heartEmpty;
                }

                toUse.draw(x+currX, y, toUse.getRenderWidth(), toUse.getRenderHeight(), Colors.setA(Colors.WHITE, alpha));
                currX += 13;
            }
        }
    }

    @Override
    public void renderOverlay(IGameInstance game, IAssetManager manager, IRenderer g, int x, int y){
        if(game.getWorld() != null){
            if(this.isMouseOverPrioritized(game)){
                g.drawHoverInfoAtMouse(game, manager, false, 0, manager.localize(LOC_HEALTH)+':', game.getPlayer().getHealth()+"/"+game.getPlayer().getMaxHealth());
            }
        }
    }

    @Override
    public ResourceName getName(){
        return ResourceName.intern("health");
    }
}
