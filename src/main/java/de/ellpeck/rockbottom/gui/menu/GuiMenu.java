package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.toast.Toast;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.init.AbstractGame;
import org.newdawn.slick.util.Log;

public class GuiMenu extends Gui{

    private static final IResourceName LOC_OPEN_SERVER = AbstractGame.internalRes("button.open_server");
    private static final IResourceName LOC_CLOSE_SERVER = AbstractGame.internalRes("button.close_server");

    public GuiMenu(){
        super(100, 100);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        this.components.add(new ComponentButton(this, this.guiLeft, this.guiTop, this.sizeX, 16, () -> {
            game.getGuiManager().openGui(new GuiSettings(this));
            return true;
        }, game.getAssetManager().localize(AbstractGame.internalRes("button.settings"))));
        if(!RockBottomAPI.getNet().isClient()){
            this.components.add(new ComponentButton(this, this.guiLeft, this.guiTop+20, this.sizeX, 16, () -> {
                if(RockBottomAPI.getNet().isServer()){
                    RockBottomAPI.getNet().shutdown();

                    game.getToaster().displayToast(new Toast(new ChatComponentTranslation(AbstractGame.internalRes("info.server_shutdown.title")), new ChatComponentTranslation(AbstractGame.internalRes("info.server_shutdown")), 160));

                    return true;
                }
                else{
                    try{
                        RockBottomAPI.getNet().init(null, Main.port, true);

                        game.getToaster().displayToast(new Toast(new ChatComponentTranslation(AbstractGame.internalRes("info.server_started.title")), new ChatComponentTranslation(AbstractGame.internalRes("info.server_started"), String.valueOf(Main.port)), 160));

                        return true;
                    }
                    catch(Exception e){
                        Log.error("Couldn't start server", e);
                    }
                }
                return false;
            }, null){
                @Override
                protected String getText(){
                    return game.getAssetManager().localize(RockBottomAPI.getNet().isServer() ? LOC_CLOSE_SERVER : LOC_OPEN_SERVER);
                }
            });
        }

        this.components.add(new ComponentButton(this, this.guiLeft+10, this.guiTop+this.sizeY-36, 80, 16, () -> {
            game.quitWorld();
            return true;
        }, game.getAssetManager().localize(AbstractGame.internalRes("button.main_menu"))));
        this.components.add(new ComponentButton(this, this.guiLeft+10, this.guiTop+this.sizeY-16, 80, 16, () -> {
            game.getGuiManager().closeGui();
            return true;
        }, game.getAssetManager().localize(AbstractGame.internalRes("button.close"))));
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("ingame_menu");
    }

}
