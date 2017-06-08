package de.ellpeck.rockbottom.api.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.game.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.ItemTile;
import de.ellpeck.rockbottom.game.item.ToolType;
import de.ellpeck.rockbottom.game.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.game.world.entity.Entity;
import de.ellpeck.rockbottom.game.world.entity.EntityItem;
import de.ellpeck.rockbottom.game.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.game.world.tile.entity.TileEntity;
import org.newdawn.slick.Input;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tile{

    public static final BoundBox DEFAULT_BOUNDS = new BoundBox(0, 0, 1, 1);

    protected final String name;

    protected Map<ToolType, Integer> effectiveTools = new HashMap<>();
    protected boolean forceDrop;
    protected float hardness = 1F;

    public Tile(String name){
        this.name = name;
    }

    public ITileRenderer getRenderer(){
        return null;
    }

    public BoundBox getBoundBox(IWorld world, int x, int y){
        return DEFAULT_BOUNDS;
    }

    public boolean canBreak(IWorld world, int x, int y, TileLayer layer){
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

    public boolean canPlace(IWorld world, int x, int y, TileLayer layer){
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
        RockBottomAPI.TILE_REGISTRY.register(this.getName(), this);

        if(this.hasItem()){
            this.createItemTile().register();
        }

        return this;
    }

    protected ItemTile createItemTile(){
        return new ItemTile(this.getName());
    }

    protected boolean hasItem(){
        return true;
    }

    public Item getItem(){
        if(this.hasItem()){
            return RockBottomAPI.ITEM_REGISTRY.get(this.getName());
        }
        else{
            return null;
        }
    }

    public void onRemoved(IWorld world, int x, int y, TileLayer layer){

    }

    public void onAdded(IWorld world, int x, int y, TileLayer layer){

    }

    public boolean onInteractWith(IWorld world, int x, int y, EntityPlayer player){
        return false;
    }

    public boolean canReplace(IWorld world, int x, int y, TileLayer layer, Tile replacementTile){
        return false;
    }

    public void onDestroyed(IWorld world, int x, int y, Entity destroyer, TileLayer layer, boolean shouldDrop){
        if(shouldDrop){
            List<ItemInstance> drops = this.getDrops(world, x, y, destroyer);
            if(drops != null && !drops.isEmpty()){
                for(ItemInstance inst : drops){
                    EntityItem.spawn(world, inst, x+0.5, y+0.5, RockBottomAPI.RANDOM.nextGaussian()*0.1, RockBottomAPI.RANDOM.nextGaussian()*0.1);
                }
            }
        }
    }

    public List<ItemInstance> getDrops(IWorld world, int x, int y, Entity destroyer){
        Item item = this.getItem();
        if(item != null){
            return Collections.singletonList(new ItemInstance(item));
        }
        else{
            return null;
        }
    }

    public TileEntity provideTileEntity(IWorld world, int x, int y){
        return null;
    }

    public boolean canProvideTileEntity(){
        return false;
    }

    public void onChangeAround(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){

    }

    public boolean isFullTile(){
        return true;
    }

    public void updateRandomly(IWorld world, int x, int y){

    }

    public void updateRandomlyForRendering(IWorld world, int x, int y, TileLayer layer, EntityPlayer player){

    }

    public void doBreak(IWorld world, int x, int y, TileLayer layer, EntityPlayer breaker, boolean isRightTool){
        world.destroyTile(x, y, layer, breaker, this.forceDrop || isRightTool);
    }

    public void doPlace(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, EntityPlayer placer){
        world.setTile(layer, x, y, this, this.getPlacementMeta(world, x, y, layer, instance));
    }

    public int getPlacementMeta(IWorld world, int x, int y, TileLayer layer, ItemInstance instance){
        return instance.getMeta();
    }

    public float getHardness(IWorld world, int x, int y, TileLayer layer){
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

    public boolean isToolEffective(IWorld world, int x, int y, TileLayer layer, ToolType type, int level){
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

    public int getLight(IWorld world, int x, int y, TileLayer layer){
        return 0;
    }

    public float getTranslucentModifier(IWorld world, int x, int y, TileLayer layer){
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
        return this.getName();
    }

    public void onScheduledUpdate(IWorld world, int x, int y, TileLayer layer){

    }

    public void describeItem(AssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced){
        if(isAdvanced){
            for(TileLayer layer : TileLayer.LAYERS){
                if(this.canPlaceInLayer(layer)){
                    desc.add(FormattingCode.GRAY+manager.localize("info.layer_placement", manager.localize(layer.name)));
                }
            }
        }
        else{
            desc.add(FormattingCode.DARK_GRAY+manager.localize("info.advanced_info", Input.getKeyName(RockBottom.get().getSettings().keyAdvancedInfo.key)));
        }
    }
}
