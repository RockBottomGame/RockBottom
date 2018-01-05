package de.ellpeck.rockbottom.gui.menu;

import com.google.common.collect.Iterators;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.data.settings.ModSettings;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentFancyButton;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.component.ComponentModButton;

import java.util.Iterator;
import java.util.logging.Level;

public class GuiMods extends Gui{

    private IMod selectedMod;
    private ComponentButton modGuiButton;
    private ComponentButton disabledButton;

    public GuiMods(Gui parent){
        super(parent);
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);

        int i = 0;
        for(IMod mod : RockBottomAPI.getModLoader().getAllTheMods()){
            this.components.add(new ComponentModButton(this, mod, 10, 10+(i*20)));
            i++;
        }

        this.components.add(new ComponentFancyButton(this, this.width-18, 2, 16, 16, () -> Util.createAndOpen(game.getDataManager().getModsDir()), RockBottomAPI.createInternalRes("gui.mods_folder"), game.getAssetManager().localize(RockBottomAPI.createInternalRes("button.mods_folder"))));

        this.modGuiButton = new ComponentButton(this, 100+(this.width-100)/2-60, this.height-30, 55, 16, () -> {
            Class<? extends Gui> guiClass = this.selectedMod.getModGuiClass();
            if(guiClass != null){
                try{
                    Gui gui = guiClass.getConstructor(Gui.class).newInstance(this);
                    game.getGuiManager().openGui(gui);
                    return true;
                }
                catch(Exception e){
                    RockBottomAPI.logger().log(Level.WARNING, "Failed initializing mod gui for mod "+this.selectedMod.getDisplayName(), e);
                }
            }
            return false;
        }, game.getAssetManager().localize(RockBottomAPI.createInternalRes("button.mod_info")));
        this.modGuiButton.setActive(this.selectedMod != null && this.selectedMod.getModGuiClass() != null);
        this.components.add(this.modGuiButton);

        this.disabledButton = new ComponentButton(this, 100+(this.width-100)/2+5, this.height-30, 55, 16, () -> {
            if(this.selectedMod.isDisableable()){
                ModSettings settings = RockBottomAPI.getModLoader().getModSettings();
                settings.setDisabled(this.selectedMod.getId(), !settings.isDisabled(this.selectedMod.getId()));
                game.getDataManager().savePropSettings(settings);

                this.updateDisableButton(game);
                return true;
            }
            return false;
        }, "", game.getAssetManager().localize(RockBottomAPI.createInternalRes("info.requires_restart")));
        this.updateDisableButton(game);
        this.components.add(this.disabledButton);

        this.components.add(new ComponentButton(this, this.width/2-40, this.height-30, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, game.getAssetManager().localize(RockBottomAPI.createInternalRes("button.back"))));
    }

    private void updateDisableButton(IGameInstance game){
        this.disabledButton.setActive(this.selectedMod != null && this.selectedMod.isDisableable());

        if(this.selectedMod != null){
            String s = "button."+(RockBottomAPI.getModLoader().getModSettings().isDisabled(this.selectedMod.getId()) ? "enable" : "disable");
            this.disabledButton.setText(game.getAssetManager().localize(RockBottomAPI.createInternalRes(s)));
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g){
        super.render(game, manager, g);

        if(this.selectedMod != null){
            IFont font = manager.getFont();

            int width = this.width-100;
            float center = 100+(width/2);
            font.drawCenteredString(center, 45, FormattingCode.BOLD+this.selectedMod.getDisplayName(), 0.75F, false);

            String[] authors = this.selectedMod.getAuthors();
            if(authors != null && authors.length > 0){
                String authorString = "";
                Iterator<String> it = Iterators.forArray(authors);
                while(it.hasNext()){
                    authorString += it.next();
                    if(it.hasNext()){
                        authorString += ", ";
                    }
                }
                font.drawCenteredString(center, 65, "By: "+FormattingCode.BOLD+authorString, 0.4F, false);
            }

            font.drawCenteredString(center, 75, "Version: "+FormattingCode.LIGHT_GRAY+FormattingCode.BOLD+this.selectedMod.getVersion(), 0.35F, false);
            font.drawCenteredString(center, 85, "Mod ID: "+FormattingCode.LIGHT_GRAY+FormattingCode.BOLD+this.selectedMod.getId(), 0.35F, false);

            font.drawSplitString(120, 100, this.selectedMod.getDescription(), 0.4F, width-30);
        }
    }

    public void selectMod(IMod mod){
        this.selectedMod = mod;
        this.modGuiButton.setActive(mod.getModGuiClass() != null);
        this.updateDisableButton(RockBottomAPI.getGame());
    }

    public IMod getSelectedMod(){
        return this.selectedMod;
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("mods");
    }
}
