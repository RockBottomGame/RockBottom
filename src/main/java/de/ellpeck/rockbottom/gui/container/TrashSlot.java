package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.component.ComponentSlot;
import de.ellpeck.rockbottom.api.gui.container.ContainerSlot;
import de.ellpeck.rockbottom.api.inventory.AbstractInventory;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class TrashSlot extends ContainerSlot{

    public TrashSlot(int x, int y){
        super(makeInventory(), 0, x, y);
    }

    private static IInventory makeInventory(){
        return new AbstractInventory(){
            @Override
            public void set(int id, ItemInstance instance){

            }

            @Override
            public ItemInstance get(int id){
                return null;
            }

            @Override
            public int getSlotAmount(){
                return 1;
            }
        };
    }

    @Override
    public ComponentSlot getGraphicalSlot(GuiContainer gui, int index, int xOffset, int yOffset){
        return new ComponentSlot(gui, this, index, xOffset+this.x, yOffset+this.y){
            @Override
            public void render(IGameInstance game, IAssetManager manager, IRenderer g, int x, int y){
                super.render(game, manager, g, x, y);
                manager.getTexture(ResourceName.intern("gui.trash")).draw(x, y, this.width, this.height);
            }
        };
    }
}
