package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.local.AssetLocale;
import de.ellpeck.rockbottom.api.assets.local.Locale;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.init.AbstractGame;

import java.util.Map;

public class GuiLanguage extends Gui{

    public GuiLanguage(Gui parent){
        super(304, 150, parent);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        IAssetManager manager = game.getAssetManager();

        int i = 0;
        for(Map.Entry<IResourceName, AssetLocale> entry : manager.getAllOfType(AssetLocale.class).entrySet()){
            IResourceName res = entry.getKey();
            Locale loc = entry.getValue().get();
            this.components.add(new ComponentButton(this, i, this.guiLeft+this.sizeX/2-75, this.guiTop+(i*20), 150, 16, loc.localize(null, res)){
                @Override
                public boolean onPressed(IGameInstance game){
                    if(manager.getLocale() != loc){
                        game.getSettings().currentLocale = res.toString();
                        game.getDataManager().savePropSettings(game.getSettings());

                        manager.setLocale(loc);
                        game.getGuiManager().setReInit();
                        return true;
                    }
                    return false;
                }
            });
            i++;
        }

        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2-40, this.guiTop+this.sizeY-16, 80, 16, game.getAssetManager().localize(AbstractGame.internalRes("button.back"))));
    }

    @Override
    public boolean onButtonActivated(IGameInstance game, int button){
        if(button == -1){
            game.getGuiManager().openGui(this.parent);
            return true;
        }
        return false;
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("language");
    }
}
