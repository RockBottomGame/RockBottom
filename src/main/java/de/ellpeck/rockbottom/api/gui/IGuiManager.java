package de.ellpeck.rockbottom.api.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;

public interface IGuiManager{

    void reInitSelf(IGameInstance game);

    void initInWorldComponents(IGameInstance game, AbstractEntityPlayer player);

    void setReInit();

    void openGui(Gui gui);

    void closeGui();

    Gui getGui();
}
