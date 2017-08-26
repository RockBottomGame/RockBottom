package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.component.ComponentConstruction;
import de.ellpeck.rockbottom.gui.component.ComponentFancyToggleButton;
import de.ellpeck.rockbottom.gui.container.ContainerInventory;
import de.ellpeck.rockbottom.init.AbstractGame;
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
        super(player, 158, 87);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        this.components.add(new ComponentFancyToggleButton(this, this.guiLeft-14, this.guiTop, 12, 12, !isConstructionOpen, () -> {
            isConstructionOpen = !isConstructionOpen;
            this.initGui(game);
            return true;
        }, AbstractGame.internalRes("gui.construction"), game.getAssetManager().localize(AbstractGame.internalRes("button.construction"))));

        if(isConstructionOpen){
            this.construction = new ComponentConstruction(this, this.guiLeft-112, this.guiTop, 110, 88, 5, 5, true, RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES, (recipe, recipeId) -> {
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
    protected void initGuiVars(IGameInstance game){
        super.initGuiVars(game);

        if(isConstructionOpen){
            this.guiLeft += 52;
        }
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("inventory");
    }
}