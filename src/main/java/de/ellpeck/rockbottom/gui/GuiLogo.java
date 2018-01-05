package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.ITexture;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

public class GuiLogo extends Gui{

    private final IResourceName texture;
    private final IResourceName texAngry;
    private final Gui followUp;
    private BoundBox faceBox;

    private int timer = 120;
    private boolean isAngry;

    public GuiLogo(String name, Gui followUp){
        this.texture = RockBottomAPI.createInternalRes(name);
        this.texAngry = this.texture.addSuffix("_skipped");
        this.followUp = followUp;
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);
        this.faceBox = new BoundBox(-20, -32, 20, 16).add(this.width/2, this.height/2);
    }

    @Override
    public void update(IGameInstance game){
        super.update(game);

        this.timer--;

        if(this.timer <= 0){
            IGuiManager gui = game.getGuiManager();

            gui.fadeOut(30, () -> {
                gui.openGui(this.followUp);
                gui.fadeIn(30, null);
            });
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g){
        g.addFilledRect(0, 0, this.width, this.height, 0xFF519FFF);

        ITexture tex = manager.getTexture(this.isAngry ? this.texAngry : this.texture);
        tex.draw(this.width/2-tex.getWidth()/2, this.height/2-tex.getHeight()/2);
    }

    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y){
        if(!this.isAngry && this.faceBox.contains(x, y)){
            this.isAngry = true;
            this.timer = 5;
            return true;
        }
        else{
            return super.onMouseAction(game, button, x, y);
        }
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("logo");
    }

    @Override
    public boolean hasGradient(){
        return false;
    }

    @Override
    protected boolean tryEscape(IGameInstance game){
        return false;
    }
}
