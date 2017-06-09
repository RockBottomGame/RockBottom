package de.ellpeck.rockbottom.game.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.Font;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.gui.component.ComponentModButton;
import org.newdawn.slick.Graphics;

public class GuiMods extends Gui{

    public IMod selectedMod;

    public GuiMods(Gui parent){
        super(100, 100, parent);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        int i = 0;
        for(IMod mod : RockBottomAPI.getModLoader().getAllMods()){
            this.components.add(new ComponentModButton(this, mod, i, 10, 10+(i*20)));
            i++;
        }

        this.components.add(new ComponentButton(this, -1, (int)game.getWidthInGui()-47, 2, 45, 10, game.getAssetManager().localize(RockBottom.internalRes("button.back"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        super.render(game, manager, g);

        if(this.selectedMod != null){
            Font font = manager.getFont();

            int width = (int)game.getWidthInGui()-100;
            float center = 100+(width/2);
            font.drawCenteredString(center, 25, this.selectedMod.getDisplayName(), 1F, false);
            font.drawCenteredString(center, 50, "Version: "+FormattingCode.GRAY+this.selectedMod.getVersion(), 0.35F, false);
            font.drawCenteredString(center, 60, "Mod ID: "+FormattingCode.GRAY+this.selectedMod.getId(), 0.35F, false);

            font.drawSplitString(120, 80, this.selectedMod.getDescription(), 0.4F, width-30);
        }
    }

    @Override
    public boolean onButtonActivated(IGameInstance game, int button){
        if(button == -1){
            game.getGuiManager().openGui(this.parent);
            return true;
        }
        return false;
    }
}
