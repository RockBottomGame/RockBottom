package de.ellpeck.rockbottom.gui.cursor;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.IInteractionManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.ContainerGui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.ISpecialCursor;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class ClosedHandCursor implements ISpecialCursor {

    @Override
    public ResourceName getTexture() {
        return ResourceName.intern("gui.cursor.closed_hand");
    }

    @Override
    public boolean shouldUseCursor(IGameInstance game, IAssetManager manager, IRenderer graphics, IGuiManager guiManager, IInteractionManager interactionManager) {
        Gui gui = guiManager.getGui();
        return gui instanceof ContainerGui && ((ContainerGui) gui).getContainer().holdingInst != null;
    }

    @Override
    public int getPriority() {
        return 1000;
    }
}
