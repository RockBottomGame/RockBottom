package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.gui.GuiChest;
import de.ellpeck.rockbottom.gui.container.ContainerChest;
import de.ellpeck.rockbottom.render.tile.TileChestRenderer;
import de.ellpeck.rockbottom.world.tile.entity.TileEntityChest;

public class TileChest extends TileBasic{

    public TileChest(){
        super(RockBottomAPI.createInternalRes("chest"));
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y, TileLayer layer){
        return null;
    }

    @Override
    public boolean canProvideTileEntity(){
        return true;
    }

    @Override
    public TileEntity provideTileEntity(IWorld world, int x, int y, TileLayer layer){
        return new TileEntityChest(world, x, y, layer);
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer){
        return layer == TileLayer.MAIN;
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player){
        TileEntityChest tile = world.getTileEntity(x, y, TileEntityChest.class);
        return tile != null && player.openGuiContainer(new GuiChest(player, tile.getTileInventory()), new ContainerChest(player, tile));
    }

    @Override
    public void onRemoved(IWorld world, int x, int y, TileLayer layer){
        if(!world.isClient()){
            TileEntityChest tile = world.getTileEntity(x, y, TileEntityChest.class);
            if(tile != null){
                tile.dropInventory(tile.getTileInventory());
            }
        }
    }

    @Override
    protected ITileRenderer createRenderer(IResourceName name){
        return new TileChestRenderer(name);
    }
}
