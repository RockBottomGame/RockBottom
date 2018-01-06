package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentSlider;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

public class GuiSound extends Gui{

    public GuiSound(Gui parent){
        super(150, 150, parent);
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);

        this.components.add(new ComponentSlider(this, this.width/2-75, 0, 150, 16, (int)(game.getSettings().soundVolume*100F), 0, 100, (integer, aBoolean) -> {
            if(aBoolean){
                game.getSettings().soundVolume = (float)integer/100F;
            }
        }, game.getAssetManager().localize(RockBottomAPI.createInternalRes("button.sound_volume"))));
        this.components.add(new ComponentSlider(this, this.width/2-75, 20, 150, 16, (int)(game.getSettings().musicVolume*100F), 0, 100, (integer, aBoolean) -> {
            if(aBoolean){
                game.getSettings().musicVolume = (float)integer/100F;
            }
        }, game.getAssetManager().localize(RockBottomAPI.createInternalRes("button.music_volume"))));

        this.components.add(new ComponentButton(this, this.width/2-40, this.height-16, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, game.getAssetManager().localize(RockBottomAPI.createInternalRes("button.back"))));
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("sound");
    }
}
