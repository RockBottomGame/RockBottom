package de.ellpeck.rockbottom.game.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.inventory.Inventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.net.packet.toserver.PacketHotbar;
import org.newdawn.slick.Graphics;

public class ComponentHotbarSlot extends GuiComponent{

    private static final IResourceName TEX_ARROW = RockBottom.internalRes("gui.selection_arrow");

    private final AbstractEntityPlayer player;
    private final Inventory inv;
    private final int id;

    public ComponentHotbarSlot(AbstractEntityPlayer player, Inventory inventory, int id, int x, int y){
        super(null, x, y, 14, 14);
        this.player = player;
        this.inv = inventory;
        this.id = id;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        RockBottomAPI.getApiHandler().renderSlotInGui(game, manager, g, this.inv.get(this.id), this.x, this.y, 0.75F);

        if(this.player.getSelectedSlot() == this.id){
            manager.getImage(TEX_ARROW).draw(this.x+0.75F, 1);
        }
    }

    @Override
    public void renderOverlay(IGameInstance game, IAssetManager manager, Graphics g){
        if(this.isMouseOver(game)){
            ItemInstance instance = this.inv.get(this.id);
            if(instance != null){
                RockBottomAPI.getApiHandler().describeItem(game, manager, g, instance);
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
}
