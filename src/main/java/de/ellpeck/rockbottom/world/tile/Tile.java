package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.ContentRegistry;
import de.ellpeck.rockbottom.item.Item;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.item.ItemTile;
import de.ellpeck.rockbottom.item.ToolType;
import de.ellpeck.rockbottom.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.util.BoundBox;
import de.ellpeck.rockbottom.util.Direction;
import de.ellpeck.rockbottom.util.Util;
import de.ellpeck.rockbottom.world.IWorld;
import de.ellpeck.rockbottom.world.TileLayer;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.Entity;
import de.ellpeck.rockbottom.world.entity.EntityItem;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.tile.entity.TileEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tile{

    public static final BoundBox DEFAULT_BOUNDS = new BoundBox(0, 0, 1, 1);

    protected final int id;
    protected final String name;

    protected Map<ToolType, Integer> effectiveTools = new HashMap<>();
    protected boolean forceDrop;
    protected float hardness = 1F;

    public Tile(int id, String name){
        this.id = id;
        this.name = name;
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
                for(Direction dir : Direction.ADJACENT){
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
        if(!this.canPlaceInLayer(layer)){
            return false;
        }

        if(!world.getTile(layer.getOpposite(), x, y).isAir()){
            return true;
        }

        for(TileLayer testLayer : TileLayer.LAYERS){
            for(Direction dir : Direction.ADJACENT){
                Tile tile = world.getTile(testLayer, x+dir.x, y+dir.y);
                if(!tile.isAir()){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canPlaceInLayer(TileLayer layer){
        return layer != TileLayer.BACKGROUND || !this.canProvideTileEntity();
    }

    public Tile register(){
        ContentRegistry.TILE_REGISTRY.register(this.getId(), this);

        if(this.hasItem()){
            this.createItemTile().register();
        }

        return this;
    }

    protected ItemTile createItemTile(){
        return new ItemTile(this.getId(), this.getName());
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

    public void onRemoved(World world, int x, int y, TileLayer layer){

    }

    public void onAdded(World world, int x, int y, TileLayer layer){

    }

    public boolean onInteractWith(World world, int x, int y, EntityPlayer player){
        return false;
    }

    public boolean canReplace(World world, int x, int y, TileLayer layer, Tile replacementTile){
        return false;
    }

    public void onDestroyed(World world, int x, int y, Entity destroyer, TileLayer layer, boolean shouldDrop){
        if(shouldDrop){
            List<ItemInstance> drops = this.getDrops(world, x, y, destroyer);
            if(drops != null && !drops.isEmpty()){
                for(ItemInstance inst : drops){
                    EntityItem.spawn(world, inst, x+0.5, y+0.5, Util.RANDOM.nextGaussian()*0.1, Util.RANDOM.nextGaussian()*0.1);
                }
            }
        }
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

    public boolean canProvideTileEntity(){
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

    public void doBreak(World world, int x, int y, TileLayer layer, EntityPlayer breaker, boolean isRightTool){
        world.destroyTile(x, y, layer, breaker, this.forceDrop || isRightTool);
    }

    public void doPlace(World world, int x, int y, TileLayer layer, ItemInstance instance, EntityPlayer placer){
        world.setTile(layer, x, y, this, this.getPlacementMeta(world, x, y, layer, instance));
    }

    public int getPlacementMeta(World world, int x, int y, TileLayer layer, ItemInstance instance){
        return instance.getMeta();
    }

    public float getHardness(World world, int x, int y, TileLayer layer){
        return this.hardness;
    }

    public Tile setHardness(float hardness){
        this.hardness = hardness;
        return this;
    }

    public Tile setForceDrop(){
        this.forceDrop = true;
        return this;
    }

    public boolean isToolEffective(World world, int x, int y, TileLayer layer, ToolType type, int level){
        for(Map.Entry<ToolType, Integer> entry : this.effectiveTools.entrySet()){
            if(entry.getKey() == type && level >= entry.getValue()){
                return true;
            }
        }
        return false;
    }

    public Tile addEffectiveTool(ToolType type, int level){
        this.effectiveTools.put(type, level);
        return this;
    }

    public int getLight(World world, int x, int y, TileLayer layer){
        return 0;
    }

    public float getTranslucentModifier(World world, int x, int y, TileLayer layer){
        return layer == TileLayer.BACKGROUND ? 0.9F : 0.8F;
    }

    public boolean isAir(){
        return false;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public String toString(){
        return this.getName()+"@"+this.getId();
    }

    public void onScheduledUpdate(World world, int x, int y, TileLayer layer){

    }
}
