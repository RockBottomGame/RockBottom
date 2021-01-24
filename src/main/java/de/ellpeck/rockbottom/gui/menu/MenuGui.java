package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ButtonComponent;
import de.ellpeck.rockbottom.api.net.chat.component.TextChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.TranslationChatComponent;
import de.ellpeck.rockbottom.api.toast.BasicToast;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.logging.Level;

public class MenuGui extends Gui {

    private static final ResourceName LOC_OPEN_SERVER = ResourceName.intern("button.open_server");
    private static final ResourceName LOC_CLOSE_SERVER = ResourceName.intern("button.close_server");

    public MenuGui() {
        super(100, 100);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);
        this.components.add(new ButtonComponent(this, 0, 0, this.width, 16, () -> {
            game.getGuiManager().openGui(new StatisticsGui(this));
            return true;
        }, game.getAssetManager().localize(ResourceName.intern("button.stats"))));

        if (!RockBottomAPI.getNet().isClient()) {
            this.components.add(new ButtonComponent(this, 0, 18, this.width, 16, () -> {
                if (RockBottomAPI.getNet().isServer()) {
                    RockBottomAPI.getNet().shutdown();

                    game.getToaster().displayToast(new BasicToast(new TranslationChatComponent(ResourceName.intern("info.server_shutdown.title")), new TranslationChatComponent(ResourceName.intern("info.server_shutdown")), 160));

                    return true;
                } else {
                    try {
                        RockBottomAPI.getNet().init(null, Main.port, true);

                        game.getToaster().displayToast(new BasicToast(new TranslationChatComponent(ResourceName.intern("info.server_started.title")), new TranslationChatComponent(ResourceName.intern("info.server_started"), String.valueOf(Main.port)), 160));

                        return true;
                    } catch (Exception e) {
                        RockBottomAPI.logger().log(Level.WARNING, "Couldn't start server", e);
                        game.getToaster().displayToast(new BasicToast(new TextChatComponent("Oh no!"), new TextChatComponent("Something went wrong, please check the log."), 160));
                    }
                }
                return false;
            }, null) {
                @Override
                protected String getText() {
                    return game.getAssetManager().localize(RockBottomAPI.getNet().isServer() ? LOC_CLOSE_SERVER : LOC_OPEN_SERVER);
                }
            });
        }

        this.components.add(new ButtonComponent(this, 10, this.height - 52, 80, 16, () -> {
            game.getGuiManager().openGui(new SettingsGui(this));
            return true;
        }, game.getAssetManager().localize(ResourceName.intern("button.settings"))));

        this.components.add(new ButtonComponent(this, 10, this.height - 34, 80, 16, () -> {
            game.getGuiManager().fadeOut(20, () -> {
                game.quitWorld();
                game.getGuiManager().fadeIn(20, null);
            });
            return true;
        }, game.getAssetManager().localize(ResourceName.intern("button.main_menu"))));
        this.components.add(new ButtonComponent(this, 10, this.height - 16, 80, 16, () -> {
            game.getGuiManager().closeGui();
            return true;
        }, game.getAssetManager().localize(ResourceName.intern("button.close"))));
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("ingame_menu");
    }

}
