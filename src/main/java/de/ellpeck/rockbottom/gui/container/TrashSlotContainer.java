package de.ellpeck.rockbottom.gui.container;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.ContainerGui;
import de.ellpeck.rockbottom.api.gui.component.SlotComponent;
import de.ellpeck.rockbottom.api.gui.container.SlotContainer;
import de.ellpeck.rockbottom.api.inventory.AbstractInventory;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.net.packet.toserver.ClearInventoryPacket;
import org.lwjgl.glfw.GLFW;

public class TrashSlotContainer extends SlotContainer {

    public TrashSlotContainer(int x, int y) {
        super(makeInventory(), 0, x, y);
    }

    private static IInventory makeInventory() {
        return new AbstractInventory() {
            @Override
            public void set(int id, ItemInstance instance) {

            }

            @Override
            public ItemInstance get(int id) {
                return null;
            }

            @Override
            public int getSlotAmount() {
                return 1;
            }

            @Override
            public void clear() {}
        };
    }

    @Override
    public SlotComponent getGraphicalSlot(ContainerGui gui, int index, int xOffset, int yOffset) {
        return new SlotComponent(gui, this, index, xOffset + this.x, yOffset + this.y) {
            @Override
            public void render(IGameInstance game, IAssetManager manager, IRenderer g, int x, int y) {
                super.render(game, manager, g, x, y);
                manager.getTexture(ResourceName.intern("gui.trash")).draw(x, y, this.width, this.height);
            }

            @Override
            public boolean onMouseAction(IGameInstance game, int button, float x, float y) {
                if (this.isMouseOver(game)) {
                    if (game.getInput().isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                        RockBottomAPI.getNet().sendToServer(new ClearInventoryPacket(game.getPlayer().getUniqueId()));
                        game.getPlayer().getInv().clear();
                        return true;
                    } else {
                        return RockBottomAPI.getInternalHooks().doDefaultSlotMovement(game, button, x, y, this.container, this);
                    }
                } else {
                    return false;
                }
            }
        };
    }
}
