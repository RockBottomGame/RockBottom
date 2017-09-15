package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.local.AssetLocale;
import de.ellpeck.rockbottom.api.assets.local.Locale;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentScrollMenu;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.util.Map;

public class GuiLanguage extends Gui{

    public GuiLanguage(Gui parent){
        super(150, 150, parent);
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);

        BoundBox area = new BoundBox(0, 0, 150, 106);
        ComponentScrollMenu menu = new ComponentScrollMenu(this, -8, 0, 106, 1, 6, area);
        this.components.add(menu);

        IAssetManager manager = game.getAssetManager();
        for(Map.Entry<IResourceName, AssetLocale> entry : manager.getAllOfType(AssetLocale.class).entrySet()){
            IResourceName res = entry.getKey();
            Locale loc = entry.getValue().get();

            menu.add(new ComponentButton(this, 0, 0, 150, 16, () -> {
                if(manager.getLocale() != loc){
                    game.getSettings().currentLocale = res.toString();
                    game.getDataManager().savePropSettings(game.getSettings());

                    manager.setLocale(loc);
                    game.getGuiManager().updateDimensions();
                    return true;
                }
                return false;
            }, loc.localize(null, res)));
        }

        menu.organize();

        this.components.add(new ComponentButton(this, this.width/2-40, this.height-16, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, game.getAssetManager().localize(RockBottomAPI.createInternalRes("button.back"))));
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("language");
    }
}
