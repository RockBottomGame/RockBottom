package de.ellpeck.rockbottom.gui.cursor;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.IInteractionManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.ISpecialCursor;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

public class CursorFinger implements ISpecialCursor{

    @Override
    public IResourceName getTexture(){
        return RockBottomAPI.createInternalRes("gui.cursor.finger");
    }

    @Override
    public boolean shouldUseCursor(IGameInstance game, IAssetManager manager, IRenderer graphics, IGuiManager guiManager, IInteractionManager interactionManager){
        Gui gui = guiManager.getGui();
        return gui != null && gui.shouldDoFingerCursor(game);
    }
}
