package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.inventory.Inventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.net.packet.toserver.PacketHotbar;

public class ComponentHotbarSlot extends GuiComponent{

    private static final IResourceName TEX_ARROW = RockBottomAPI.createInternalRes("gui.selection_arrow");

    private final AbstractEntityPlayer player;
    private final Inventory inv;
    private final int id;

    public ComponentHotbarSlot(AbstractEntityPlayer player, Inventory inventory, int id, int x, int y){
        super(null, x, y, 12, 12);
        this.player = player;
        this.inv = inventory;
        this.id = id;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, int x, int y){
        g.renderSlotInGui(game, manager, this.inv.get(this.id), x, y, 0.75F, game.getGuiManager().getGui() == null && this.isMouseOverPrioritized(game));

        if(this.player.getSelectedSlot() == this.id){
            manager.getTexture(TEX_ARROW).draw(x, 1F, 0.75F);
        }
    }

    @Override
    public void renderOverlay(IGameInstance game, IAssetManager manager, IRenderer g, int x, int y){
        if(this.isMouseOverPrioritized(game)){
            ItemInstance instance = this.inv.get(this.id);
            if(instance != null){
                g.describeItem(game, manager, instance);
            }
        }
    }

    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y){
        if(this.isMouseOver(game)){
            if(this.player.getSelectedSlot() != this.id){
                this.player.setSelectedSlot(this.id);

                if(RockBottomAPI.getNet().isClient()){
                    RockBottomAPI.getNet().sendToServer(new PacketHotbar(game.getPlayer().getUniqueId(), this.id));
                }

                return true;
            }
        }
        return false;
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("hotbar_slot");
    }
}
