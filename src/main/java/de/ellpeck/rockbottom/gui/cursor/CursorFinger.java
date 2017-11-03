package de.ellpeck.rockbottom.gui.cursor;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.IInteractionManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.ISpecialCursor;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.time.LocalDateTime;
import java.time.Month;

public class CursorFinger implements ISpecialCursor{

    @Override
    public IResourceName getTexture(){
        LocalDateTime now = LocalDateTime.now();

        if(now.getMonth() == Month.APRIL && now.getDayOfMonth() == 1){
            return RockBottomAPI.createInternalRes("gui.cursor.finger_104");
        }
        else{
            return RockBottomAPI.createInternalRes("gui.cursor.finger");
        }
    }

    @Override
    public boolean shouldUseCursor(IGameInstance game, IAssetManager manager, IGraphics graphics, IGuiManager guiManager, IInteractionManager interactionManager){
        Gui gui = guiManager.getGui();
        return gui != null && gui.isMouseOverComponent(game);
    }
}
