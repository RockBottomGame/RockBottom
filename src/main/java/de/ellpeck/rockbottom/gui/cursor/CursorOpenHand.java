package de.ellpeck.rockbottom.gui.cursor;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.IInteractionManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.ISpecialCursor;
import de.ellpeck.rockbottom.api.gui.component.ComponentSlot;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

public class CursorOpenHand implements ISpecialCursor{

    @Override
    public IResourceName getTexture(){
        return RockBottomAPI.createInternalRes("gui.cursor.open_hand");
    }

    @Override
    public boolean shouldUseCursor(IGameInstance game, IAssetManager manager, IGraphics graphics, IGuiManager guiManager, IInteractionManager interactionManager){
        Gui gui = guiManager.getGui();
        if(gui != null){
            for(GuiComponent component : gui.getComponents()){
                if(component.isMouseOverPrioritized(game)){
                    return component instanceof ComponentSlot && ((ComponentSlot)component).slot.get() != null;
                }
            }
        }
        return false;
    }

    @Override
    public int getPriority(){
        return 1000;
    }
}
