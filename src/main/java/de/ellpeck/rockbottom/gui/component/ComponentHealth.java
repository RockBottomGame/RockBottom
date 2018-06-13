package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
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
            ITexture heart = manager.getTexture(TEX_HEART);
            ITexture heartEmpty = manager.getTexture(TEX_HEART_EMPTY);

            AbstractEntityPlayer player = game.getPlayer();
            int health = player.getHealth();
            int max = player.getMaxHealth();

            float healthParts = health/20F;
            float maxParts = max/20F;

            int fullHealthParts = Util.ceil(healthParts);
            int fullMaxParts = Util.ceil(maxParts);

            float healthPercentage = fullHealthParts-healthParts;
            float maxPercentage = fullMaxParts-maxParts;

            int currX = this.width-13;
            for(int i = 0; i < fullMaxParts; i++){
                if(i == fullMaxParts-1 && maxPercentage > 0F){
                    float width = heartEmpty.getRenderWidth();
                    float height = heartEmpty.getRenderHeight();

                    heartEmpty.draw(x+currX+width*maxPercentage, y, x+currX+width, y+height, maxPercentage*width, 0, width, height);
                }
                else{
                    heartEmpty.draw(x+currX, y);
                }

                if(i == fullHealthParts-1 && healthPercentage > 0F){
                    float width = heart.getRenderWidth();
                    float height = heart.getRenderHeight();

                    heart.draw(x+currX+width*healthPercentage, y, x+currX+width, y+height, width*healthPercentage, 0, width, height);
                }
                else if(fullHealthParts > i){
                    heart.draw(x+currX, y);
                }

                currX -= 13;
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
