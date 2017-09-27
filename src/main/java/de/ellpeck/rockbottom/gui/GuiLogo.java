package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.tex.ITexture;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

public class GuiLogo extends Gui{

    private final ITexture texture;
    private final Gui followUp;

    private int timer;

    public GuiLogo(ITexture texture, Gui followUp){
        this.texture = texture;
        this.followUp = followUp;
    }

    @Override
    public void update(IGameInstance game){
        super.update(game);

        this.timer++;

        IGuiManager gui = game.getGuiManager();
        if(this.timer == 120){
            gui.fadeOut(30, () -> gui.fadeIn(30, null));
        }
        else if(this.timer >= 150){
            gui.openGui(this.followUp);
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IGraphics g){
        g.fillRect(0, 0, this.width, this.height, 0xFF519FFF);
        this.texture.draw(0, 0, this.width, this.height);
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("logo");
    }

    @Override
    public boolean hasGradient(){
        return false;
    }
}
