package de.ellpeck.rockbottom.game.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.Log;

public class GuiMenu extends Gui{

    private int savingTimer;

    public GuiMenu(){
        super(100, 100);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        this.components.add(new ComponentButton(this, 0, this.guiLeft, this.guiTop, this.sizeX, 16, game.getAssetManager().localize("button.settings")));
        if(!RockBottomAPI.getNet().isClient()){
            this.components.add(new ComponentButton(this, 1, this.guiLeft, this.guiTop+20, this.sizeX, 16, null){
                @Override
                protected String getText(){
                    return game.getAssetManager().localize("button."+(RockBottomAPI.getNet().isServer() ? "close" : "open")+"_server");
                }
            });
        }

        this.components.add(new ComponentButton(this, -1, this.guiLeft+10, this.guiTop+this.sizeY-36, 80, 16, game.getAssetManager().localize("button.main_menu")));
        this.components.add(new ComponentButton(this, -2, this.guiLeft+10, this.guiTop+this.sizeY-16, 80, 16, game.getAssetManager().localize("button.close")));
    }

    @Override
    public void update(IGameInstance game){
        super.update(game);

        if(!RockBottomAPI.getNet().isClient()){
            if(this.savingTimer >= 0){
                this.savingTimer++;
                if(this.savingTimer >= 50){
                    this.savingTimer = -1;
                }
            }
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        super.render(game, manager, g);

        if(!RockBottomAPI.getNet().isClient()){
            if(this.savingTimer >= 0){
                String text = manager.localize("info.saved");
                manager.getFont().drawFadingString((float)game.getWidthInGui()/2-manager.getFont().getWidth(text, 0.35F)/2, (float)game.getHeightInGui()-15F, text, 0.35F, (float)this.savingTimer/50F, 0.25F, 0.75F);
            }
        }
    }

    @Override
    public boolean onButtonActivated(IGameInstance game, int button){
        if(button == -1){
            game.quitWorld();
            return true;
        }
        else if(button == -2){
            game.getGuiManager().closeGui();
            return true;
        }
        else if(button == 0){
            game.getGuiManager().openGui(new GuiSettings(this));
        }
        else if(button == 1){
            if(RockBottomAPI.getNet().isServer()){
                RockBottomAPI.getNet().shutdown();
            }
            else{
                try{
                    RockBottomAPI.getNet().init(null, game.getSettings().serverStartPort, true);
                }
                catch(Exception e){
                    Log.error("Couldn't start server", e);
                }
            }
        }
        return false;
    }

}
