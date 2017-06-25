package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.util.Util;
import org.newdawn.slick.Graphics;

public class GuiPlayerEditor extends Gui{

    private boolean changed;

    public GuiPlayerEditor(Gui parent){
        super(100, 100, parent);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        IPlayerDesign design = game.getPlayerDesign();


        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2-40, (int)game.getHeightInGui()-20, 80, 16, game.getAssetManager().localize(RockBottom.internalRes("button.back"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        super.render(game, manager, g);
    }

    @Override
    public void onClosed(IGameInstance game){
        super.onClosed(game);

        if(this.changed){
            IDataManager dataManager = game.getDataManager();
            DataSet gameInfo = dataManager.getGameInfo();

            RockBottom.get().getPlayerDesign().save(gameInfo);
            gameInfo.write(dataManager.getGameDataFile());
        }
    }

    @Override
    public boolean onButtonActivated(IGameInstance game, int button){
        IGuiManager guiManager = game.getGuiManager();

        if(button == -1){
            guiManager.openGui(this.parent);
            return true;
        }
        else{
            return false;
        }
    }

}
