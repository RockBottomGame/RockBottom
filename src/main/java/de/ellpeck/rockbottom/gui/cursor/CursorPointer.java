package de.ellpeck.rockbottom.gui.cursor;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.IInteractionManager;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.ISpecialCursor;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

public class CursorPointer implements ISpecialCursor{

    @Override
    public IResourceName getTexture(){
        return RockBottomAPI.createInternalRes("gui.cursor.pointer");
    }

    @Override
    public boolean shouldUseCursor(IGameInstance game, IAssetManager manager, IGraphics graphics, IGuiManager guiManager, IInteractionManager interactionManager){
        return true;
    }

    @Override
    public int getPriority(){
        return -1000;
    }
}
