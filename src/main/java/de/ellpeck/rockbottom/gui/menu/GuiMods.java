package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.Font;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.data.settings.ModSettings;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.gui.component.ComponentModButton;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.Log;

public class GuiMods extends Gui{

    private IMod selectedMod;
    private ComponentButton modGuiButton;
    private ComponentButton disabledButton;

    public GuiMods(Gui parent){
        super(100, 100, parent);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        int i = 0;
        for(IMod mod : RockBottomAPI.getModLoader().getAllMods()){
            this.components.add(new ComponentModButton(this, mod, i+1, 10, 10+(i*20)));
            i++;
        }

        int x = (int)game.getWidthInGui();
        this.components.add(new ComponentButton(this, -1, x-47, 2, 45, 10, game.getAssetManager().localize(RockBottom.internalRes("button.back"))));

        this.modGuiButton = new ComponentButton(this, 0, x-57, 14, 55, 10, game.getAssetManager().localize(RockBottom.internalRes("button.mod_info")));
        this.modGuiButton.isVisible = this.selectedMod != null && this.selectedMod.getModGuiClass() != null;
        this.components.add(this.modGuiButton);

        this.disabledButton = new ComponentButton(this, 1, x-57, 26, 55, 10, "", game.getAssetManager().localize(RockBottom.internalRes("info.requires_restart")));
        this.updateDisableButton(game);
        this.components.add(this.disabledButton);
    }

    private void updateDisableButton(IGameInstance game){
        this.disabledButton.isVisible = this.selectedMod != null && this.selectedMod.isDisableable();

        if(this.selectedMod != null){
            String s = "button."+(RockBottomAPI.getModLoader().getModSettings().isDisabled(this.selectedMod.getId()) ? "enable" : "disable");
            this.disabledButton.setText(game.getAssetManager().localize(RockBottom.internalRes(s)));
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        super.render(game, manager, g);

        if(this.selectedMod != null){
            Font font = manager.getFont();

            int width = (int)game.getWidthInGui()-100;
            float center = 100+(width/2);
            font.drawCenteredString(center, 45, this.selectedMod.getDisplayName(), 0.75F, false);
            font.drawCenteredString(center, 70, "Version: "+FormattingCode.GRAY+this.selectedMod.getVersion(), 0.35F, false);
            font.drawCenteredString(center, 80, "Mod ID: "+FormattingCode.GRAY+this.selectedMod.getId(), 0.35F, false);

            font.drawSplitString(120, 90, this.selectedMod.getDescription(), 0.4F, width-30);
        }
    }

    public void selectMod(IMod mod){
        this.selectedMod = mod;
        this.modGuiButton.isVisible = mod.getModGuiClass() != null;
        this.updateDisableButton(RockBottomAPI.getGame());
    }

    public IMod getSelectedMod(){
        return this.selectedMod;
    }

    @Override
    public boolean onButtonActivated(IGameInstance game, int button){
        if(button == 0){
            Class<? extends Gui> guiClass = this.selectedMod.getModGuiClass();
            if(guiClass != null){
                try{
                    Gui gui = guiClass.getConstructor(Gui.class).newInstance(this);
                    game.getGuiManager().openGui(gui);
                    return true;
                }
                catch(Exception e){
                    Log.error("Failed initializing mod gui for mod "+this.selectedMod.getDisplayName(), e);
                }
            }
        }
        else if(button == 1){
            ModSettings settings = RockBottomAPI.getModLoader().getModSettings();
            settings.setDisabled(this.selectedMod.getId(), !settings.isDisabled(this.selectedMod.getId()));
            game.getDataManager().savePropSettings(settings);

            this.updateDisableButton(game);
            return true;
        }
        else if(button == -1){
            game.getGuiManager().openGui(this.parent);
            return true;
        }
        return false;
    }
}
