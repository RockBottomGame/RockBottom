package de.ellpeck.rockbottom.game.gui.menu;

import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.game.gui.Gui;
import de.ellpeck.rockbottom.game.gui.component.ComponentButton;
import de.ellpeck.rockbottom.game.net.NetHandler;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.Log;

public class GuiMenu extends Gui{

    private int savingTimer;

    public GuiMenu(){
        super(100, 100);
    }

    @Override
    public void initGui(RockBottom game){
        super.initGui(game);

        this.components.add(new ComponentButton(this, 0, this.guiLeft, this.guiTop, this.sizeX, 16, game.assetManager.localize("button.settings")));
        if(!NetHandler.isClient()){
            this.components.add(new ComponentButton(this, 1, this.guiLeft, this.guiTop+20, this.sizeX, 16, null){
                @Override
                protected String getText(){
                    return game.assetManager.localize("button."+(NetHandler.isServer() ? "close" : "open")+"_server");
                }
            });
        }

        this.components.add(new ComponentButton(this, -1, this.guiLeft+10, this.guiTop+this.sizeY-36, 80, 16, game.assetManager.localize("button.main_menu")));
        this.components.add(new ComponentButton(this, -2, this.guiLeft+10, this.guiTop+this.sizeY-16, 80, 16, game.assetManager.localize("button.close")));
    }

    @Override
    public void update(RockBottom game){
        super.update(game);

        if(!NetHandler.isClient()){
            if(this.savingTimer >= 0){
                this.savingTimer++;
                if(this.savingTimer >= 50){
                    this.savingTimer = -1;
                }
            }
        }
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g){
        super.render(game, manager, g);

        if(!NetHandler.isClient()){
            if(this.savingTimer >= 0){
                String text = manager.localize("info.saved");
                manager.getFont().drawFadingString((float)game.getWidthInGui()/2-manager.getFont().getWidth(text, 0.35F)/2, (float)game.getHeightInGui()-15F, text, 0.35F, (float)this.savingTimer/50F, 0.25F, 0.75F);
            }
        }
    }

    @Override
    public boolean onButtonActivated(RockBottom game, int button){
        if(button == -1){
            game.quitWorld();
            return true;
        }
        else if(button == -2){
            game.guiManager.closeGui();
            return true;
        }
        else if(button == 0){
            game.guiManager.openGui(new GuiSettings(this));
        }
        else if(button == 1){
            if(NetHandler.isServer()){
                NetHandler.shutdown();
            }
            else{
                try{
                    NetHandler.init(null, game.settings.serverStartPort, true);
                }
                catch(Exception e){
                    Log.error("Couldn't start server", e);
                }
            }
        }
        return false;
    }

}
