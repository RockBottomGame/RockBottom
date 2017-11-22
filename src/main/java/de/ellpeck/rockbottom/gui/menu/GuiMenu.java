package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.toast.Toast;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.util.logging.Level;

public class GuiMenu extends Gui{

    private static final IResourceName LOC_OPEN_SERVER = RockBottomAPI.createInternalRes("button.open_server");
    private static final IResourceName LOC_CLOSE_SERVER = RockBottomAPI.createInternalRes("button.close_server");

    public GuiMenu(){
        super(100, 100);
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);

        this.components.add(new ComponentButton(this, 0, 0, this.width, 16, () -> {
            game.getGuiManager().openGui(new GuiSettings(this));
            return true;
        }, game.getAssetManager().localize(RockBottomAPI.createInternalRes("button.settings"))));
        if(!RockBottomAPI.getNet().isClient()){
            this.components.add(new ComponentButton(this, 0, 20, this.width, 16, () -> {
                if(RockBottomAPI.getNet().isServer()){
                    RockBottomAPI.getNet().shutdown();

                    game.getToaster().displayToast(new Toast(new ChatComponentTranslation(RockBottomAPI.createInternalRes("info.server_shutdown.title")), new ChatComponentTranslation(RockBottomAPI.createInternalRes("info.server_shutdown")), 160));

                    return true;
                }
                else{
                    try{
                        RockBottomAPI.getNet().init(null, Main.port, true);

                        game.getToaster().displayToast(new Toast(new ChatComponentTranslation(RockBottomAPI.createInternalRes("info.server_started.title")), new ChatComponentTranslation(RockBottomAPI.createInternalRes("info.server_started"), String.valueOf(Main.port)), 160));

                        return true;
                    }
                    catch(Exception e){
                        RockBottomAPI.logger().log(Level.WARNING, "Couldn't start server", e);
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

        this.components.add(new ComponentButton(this, 10, this.height-36, 80, 16, () -> {
            game.getGuiManager().fadeOut(20, ()->{
                game.quitWorld();
                game.getGuiManager().fadeIn(20, null);
            });
            return true;
        }, game.getAssetManager().localize(RockBottomAPI.createInternalRes("button.main_menu"))));
        this.components.add(new ComponentButton(this, 10, this.height-16, 80, 16, () -> {
            game.getGuiManager().closeGui();
            return true;
        }, game.getAssetManager().localize(RockBottomAPI.createInternalRes("button.close"))));
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("ingame_menu");
    }

}
