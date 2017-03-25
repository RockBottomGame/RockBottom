package de.ellpeck.game.world.tile;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.item.Item;
import de.ellpeck.game.item.ItemInstance;
import de.ellpeck.game.item.ItemTile;
import de.ellpeck.game.item.ToolType;
import de.ellpeck.game.render.tile.ITileRenderer;
import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.util.Direction;
import de.ellpeck.game.world.TileLayer;
import de.ellpeck.game.world.IWorld;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.entity.Entity;
import de.ellpeck.game.world.entity.EntityItem;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import de.ellpeck.game.world.tile.entity.TileEntity;

import java.util.Collections;
import java.util.List;

public class Tile{

    protected final int id;
    public static final BoundBox DEFAULT_BOUNDS = new BoundBox(0, 0, 1, 1);

    protected ToolType[] effectiveTools = new ToolType[0];
    protected float hardness = 1F;

    public Tile(int id){
        this.id = id;
    }

    public ITileRenderer getRenderer(){
        return null;
    }

    public BoundBox getBoundBox(IWorld world, int x, int y){
        return DEFAULT_BOUNDS;
    }

    public boolean canBreak(World world, int x, int y, TileLayer layer){
        if(layer == TileLayer.MAIN){
            return true;
        }
        else{
            if(!world.getTile(x, y).isFullTile()){
                for(Direction dir : Direction.ADJACENT_DIRECTIONS){
                    Tile tile = world.getTile(layer, x+dir.x, y+dir.y);
                    if(!tile.isFullTile()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean canPlace(World world, int x, int y, TileLayer layer){
        if(layer != TileLayer.MAIN && this.providesTileEntity()){
            return false;
        }

        if(world.getTile(layer.getOpposite(), x, y).isFullTile()){
            return true;
        }

        for(Direction dir : Direction.ADJACENT_DIRECTIONS){
            Tile tile = world.getTile(layer, x+dir.x, y+dir.y);
            if(tile.isFullTile()){
                return true;
            }
        }
        return false;
    }

    public Tile register(){
        ContentRegistry.TILE_REGISTRY.register(this.id, this);

        if(this.hasItem()){
            ItemTile item = new ItemTile(this.id);
            item.register();
        }

        return this;
    }

    protected boolean hasItem(){
        return true;
    }

    public int getId(){
        return this.id;
    }

    public Item getItem(){
        if(this.hasItem()){
            return ContentRegistry.ITEM_REGISTRY.get(this.id);
        }
        else{
            return null;
        }
    }

    public void onRemoved(World world, int x, int y){

    }

    public void onAdded(World world, int x, int y){

    }

    public boolean canReplace(World world, int x, int y, TileLayer layer){
        return false;
    }

    public void onDestroyed(World world, int x, int y, Entity destroyer, TileLayer layer){
        List<ItemInstance> drops = this.getDrops(world, x, y, destroyer);
        if(drops != null && !drops.isEmpty()){
            for(ItemInstance inst : drops){
                EntityItem.spawn(world, inst, x+0.5, y+0.5, 0, 0);
            }
        }
    }

    public boolean onInteractWith(World world, int x, int y, EntityPlayer player){
        return false;
    }

    public List<ItemInstance> getDrops(World world, int x, int y, Entity destroyer){
        Item item = this.getItem();
        if(item != null){
            return Collections.singletonList(new ItemInstance(item));
        }
        else{
            return null;
        }
    }

    @Override
    public int hashCode(){
        return this.getId();
    }

    public TileEntity provideTileEntity(World world, int x, int y){
        return null;
    }

    public boolean providesTileEntity(){
        return false;
    }

    public void onChangeAround(World world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){

    }

    public boolean isFullTile(){
        return true;
    }

    public boolean doesRandomUpdates(){
        return false;
    }

    public void updateRandomly(World world, int x, int y){

    }

    public void doPlace(World world, int x, int y, TileLayer layer, ItemInstance instance, EntityPlayer placer){
        world.setTile(layer, x, y, this);

        byte meta = instance.getMeta();
        if(meta != 0){
            world.setMeta(layer, x, y, meta);
        }
    }

    public float getHardness(World world, int x, int y, TileLayer layer){
        return this.hardness;
    }

    public Tile setHardness(float hardness){
        this.hardness = hardness;
        return this;
    }

    public ToolType[] getEffectiveTools(World world, int x, int y, TileLayer layer){
        return this.effectiveTools;
    }

    public Tile setEffectiveTools(ToolType... types){
        this.effectiveTools = types;
        return this;
    }
}
