package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.component.ComponentConstruction;
import de.ellpeck.rockbottom.api.gui.component.ComponentFancyToggleButton;
import de.ellpeck.rockbottom.gui.container.ContainerInventory;
import de.ellpeck.rockbottom.net.packet.toserver.PacketManualConstruction;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

import java.util.function.BiConsumer;

public class GuiInventory extends GuiContainer{

    private static boolean isConstructionOpen;

    private final BiConsumer<IInventory, Integer> invCallback = (inv, slot) -> {
        if(isConstructionOpen && this.construction != null){
            this.construction.organize();
        }
    };

    private ComponentConstruction construction;

    public GuiInventory(EntityPlayer player){
        super(player, 158, 83);
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);

        this.components.add(new ComponentFancyToggleButton(this, -14, 0, 12, 12, !isConstructionOpen, () -> {
            isConstructionOpen = !isConstructionOpen;
            this.init(game);
            return true;
        }, RockBottomAPI.createInternalRes("gui.construction"), game.getAssetManager().localize(RockBottomAPI.createInternalRes("button.construction"))));

        if(isConstructionOpen){
            this.construction = new ComponentConstruction(this, -112,0, 110, 88, 5, 5, true, RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES, (recipe, recipeId) -> {
                if(RockBottomAPI.getNet().isClient()){
                    RockBottomAPI.getNet().sendToServer(new PacketManualConstruction(game.getPlayer().getUniqueId(), recipeId, 1));
                }
                else{
                    ContainerInventory.doInvBasedConstruction(game.getPlayer(), recipe, 1);
                }
            });
            this.components.add(this.construction);
        }
        else{
            this.construction = null;
        }
    }

    @Override
    public void onOpened(IGameInstance game){
        super.onOpened(game);
        this.player.getInv().addChangeCallback(this.invCallback);
    }

    @Override
    public void onClosed(IGameInstance game){
        super.onClosed(game);
        this.player.getInv().removeChangeCallback(this.invCallback);
    }

    @Override
    protected void updateDimensions(IGameInstance game){
        super.updateDimensions(game);

        if(isConstructionOpen){
            this.x += 52;
        }
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("inventory");
    }
}