package de.ellpeck.rockbottom.game.world.entity.player;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.MutableInt;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.game.data.settings.CommandPermissions;
import de.ellpeck.rockbottom.game.gui.Gui;
import de.ellpeck.rockbottom.game.gui.container.ContainerInventory;
import de.ellpeck.rockbottom.game.gui.container.ItemContainer;
import de.ellpeck.rockbottom.game.inventory.IInvChangeCallback;
import de.ellpeck.rockbottom.game.inventory.IInventory;
import de.ellpeck.rockbottom.game.inventory.InventoryPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.game.net.NetHandler;
import de.ellpeck.rockbottom.game.net.packet.IPacket;
import de.ellpeck.rockbottom.game.net.packet.toclient.PacketContainerChange;
import de.ellpeck.rockbottom.game.net.packet.toclient.PacketContainerData;
import de.ellpeck.rockbottom.game.net.packet.toserver.PacketOpenUnboundContainer;
import de.ellpeck.rockbottom.game.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.game.render.entity.PlayerEntityRenderer;
import de.ellpeck.rockbottom.game.util.Util;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.game.world.World;
import de.ellpeck.rockbottom.game.world.entity.EntityItem;
import de.ellpeck.rockbottom.game.world.entity.EntityLiving;
import org.newdawn.slick.Color;
import org.newdawn.slick.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EntityPlayer extends EntityLiving implements IInvChangeCallback{

    public final InventoryPlayer inv = new InventoryPlayer(this);
    public final ItemContainer inventoryContainer = new ContainerInventory(this);
    private final BoundBox boundingBox = new BoundBox(-0.5, -0.5, 0.5, 1.5);
    private final IEntityRenderer renderer;
    public Color color = Util.randomColor(Util.RANDOM);
    public List<IChunk> chunksInRange = new ArrayList<>();
    private ItemContainer currentContainer;
    private int respawnTimer;

    public EntityPlayer(IWorld world){
        super(world);
        this.renderer = new PlayerEntityRenderer();
        this.facing = Direction.RIGHT;
    }

    public EntityPlayer(IWorld world, UUID uniqueId){
        this(world);
        this.uniqueId = uniqueId;
    }

    @Override
    public IEntityRenderer getRenderer(){
        return this.renderer;
    }

    public void openGuiContainer(Gui gui, ItemContainer container){
        this.openContainer(container);

        if(NetHandler.isThePlayer(this)){
            RockBottom.get().getGuiManager().openGui(gui);
        }
    }

    public void openContainer(ItemContainer container){
        if(this.currentContainer != null){
            for(IInventory inv : this.currentContainer.containedInventories){
                if(inv != this.inv){
                    inv.removeChangeCallback(this);
                }
            }

            this.currentContainer.onClosed();
        }

        this.currentContainer = container;

        if(this.currentContainer != null){
            if(NetHandler.isClient()){
                int id = this.currentContainer.getUnboundId();
                if(id >= 0){
                    NetHandler.sendToServer(new PacketOpenUnboundContainer(this.getUniqueId(), id));
                }
            }
            else if(NetHandler.isServer()){
                this.sendPacket(new PacketContainerData(container));
            }

            this.currentContainer.onOpened();

            for(IInventory inv : this.currentContainer.containedInventories){
                if(inv != this.inv){
                    inv.addChangeCallback(this);
                }
            }
        }
        else{
            if(NetHandler.isClient()){
                NetHandler.sendToServer(new PacketOpenUnboundContainer(this.getUniqueId(), PacketOpenUnboundContainer.CLOSE_ID));
            }
        }

        if(this.currentContainer == null){
            Log.debug("Closed Container for player with unique id "+this.getUniqueId());
        }
        else{
            Log.debug("Opened Container "+this.currentContainer+" for player with unique id "+this.getUniqueId());
        }
    }

    public void closeContainer(){
        this.openContainer(null);
    }

    public ItemContainer getContainer(){
        return this.currentContainer;
    }

    @Override
    public void update(IGameInstance game){
        super.update(game);

        if(!NetHandler.isClient()){
            if(this.isDead()){
                this.respawnTimer++;

                if(this.respawnTimer >= 400){
                    this.resetAndSpawn(game);
                }
            }
            else{
                List<EntityItem> entities = this.world.getEntities(this.getBoundingBox().copy().add(this.x, this.y), EntityItem.class);
                for(EntityItem entity : entities){
                    if(entity.canPickUp()){
                        ItemInstance left = this.inv.addExistingFirst(entity.item, false);

                        if(left == null){
                            entity.kill();
                        }
                        else{
                            entity.item = left;
                        }
                    }
                }
            }
        }

        if(NetHandler.isThePlayer(this)){
            for(int i = 0; i < Constants.RANDOM_TILE_RENDER_UPDATES; i++){
                TileLayer layer = TileLayer.LAYERS[Util.RANDOM.nextInt(TileLayer.LAYERS.length)];
                int x = Util.floor(this.x)+Util.RANDOM.nextInt(33)-16;
                int y = Util.floor(this.y)+Util.RANDOM.nextInt(33)-16;

                if(this.world.isPosLoaded(x, y)){
                    Tile tile = this.world.getTile(layer, x, y);
                    tile.updateRandomlyForRendering(this.world, x, y, layer, this);
                }
            }
        }
    }

    @Override
    public int getUpdateFrequency(){
        return 1;
    }

    public void resetAndSpawn(IGameInstance game){
        this.respawnTimer = 0;
        this.dead = false;
        this.motionX = 0;
        this.motionY = 0;
        this.fallAmount = 0;
        this.health = this.getMaxHealth();

        if(game.getGuiManager() != null){
            game.getGuiManager().closeGui();
        }

        this.setPos(this.world.getSpawnX()+0.5, this.world.getLowestAirUpwards(TileLayer.MAIN, this.world.getSpawnX(), 0)+1);
    }

    @Override
    public void onGroundHit(){
        if(!NetHandler.isClient()){
            if(this.fallAmount >= 20){
                this.health -= this.fallAmount*1.5;
            }
        }
    }

    @Override
    public boolean shouldBeRemoved(){
        return false;
    }

    @Override
    public int getMaxHealth(){
        return 100;
    }

    @Override
    public int getRegenRate(){
        return 10;
    }

    @Override
    public BoundBox getBoundingBox(){
        return this.boundingBox;
    }

    @Override
    public void save(DataSet set){
        super.save(set);
        this.inv.save(set);

        set.addFloat("color_r", this.color.r);
        set.addFloat("color_g", this.color.g);
        set.addFloat("color_b", this.color.b);
    }

    @Override
    public void load(DataSet set){
        super.load(set);
        this.inv.load(set);

        this.color = new Color(set.getFloat("color_r"), set.getFloat("color_g"), set.getFloat("color_b"));
    }

    public void sendPacket(IPacket packet){

    }

    public void onChunkLoaded(IChunk chunk){

    }

    public void onChunkUnloaded(IChunk chunk){

    }

    public void move(int type){
        if(type == 0){
            this.motionX -= 0.2;
            this.facing = Direction.LEFT;
        }
        else if(type == 1){
            this.motionX += 0.2;
            this.facing = Direction.RIGHT;
        }
        else if(type == 2){
            this.jump(0.28);
        }
    }

    @Override
    public void onChange(IInventory inv, int slot, ItemInstance newInstance){
        if(NetHandler.isServer()){
            boolean isInv = inv instanceof InventoryPlayer;
            ItemContainer container = isInv ? this.inventoryContainer : this.currentContainer;

            if(container != null){
                int index = container.getIndexForInvSlot(inv, slot);
                if(index >= 0){
                    this.sendPacket(new PacketContainerChange(isInv, index, newInstance));
                }
            }
        }
    }

    @Override
    public void moveToChunk(IChunk newChunk){
        super.moveToChunk(newChunk);

        if(!NetHandler.isClient()){
            List<IChunk> nowLoaded = new ArrayList<>();

            for(int x = -Constants.CHUNK_LOAD_DISTANCE; x <= Constants.CHUNK_LOAD_DISTANCE; x++){
                for(int y = Constants.CHUNK_LOAD_DISTANCE; y >= -Constants.CHUNK_LOAD_DISTANCE; y--){
                    IChunk chunk = this.world.getChunkFromGridCoords(this.chunkX+x, this.chunkY+y);
                    nowLoaded.add(chunk);
                }
            }

            int newLoad = nowLoaded.size();
            int unload = 0;
            for(IChunk chunk : this.chunksInRange){
                int nowIndex = nowLoaded.indexOf(chunk);

                if(nowIndex < 0){
                    List<EntityPlayer> inRange = chunk.getPlayersInRange();
                    if(inRange.contains(this)){
                        inRange.remove(this);
                        unload++;
                    }

                    List<EntityPlayer> outOfRange = chunk.getPlayersLeftRange();
                    if(!outOfRange.contains(this)){
                        outOfRange.add(this);
                        chunk.getLeftPlayerTimers().put(this, new MutableInt(Constants.CHUNK_LOAD_TIME));
                    }
                }
                else{
                    newLoad--;
                }
            }

            Log.debug("Player with id "+this.getUniqueId()+" leaving range of "+unload+" chunks and loading "+newLoad+" new ones");

            for(IChunk chunk : nowLoaded){
                List<EntityPlayer> inRange = chunk.getPlayersInRange();
                if(!inRange.contains(this)){
                    inRange.add(this);
                }

                if(!this.chunksInRange.contains(chunk)){
                    this.chunksInRange.add(chunk);

                    this.onChunkLoaded(chunk);
                }
                else{
                    chunk.getPlayersLeftRange().remove(this);
                    chunk.getLeftPlayerTimers().remove(this);
                }
            }
        }
    }

    public int getCommandLevel(){
        if(NetHandler.isServer()){
            CommandPermissions permissions = NetHandler.getCommandPermissions();
            int level = permissions.getCommandLevel(this);

            if(level < Constants.ADMIN_PERMISSION && NetHandler.isThePlayer(this)){
                level = Constants.ADMIN_PERMISSION;

                permissions.setCommandLevel(this, level);
                RockBottom.get().getDataManager().savePropSettings(permissions);

                Log.info("Setting command level for server host with id "+this.getUniqueId()+" to "+level+"!");
            }

            return level;
        }
        else{
            return 0;
        }
    }

    @Override
    public void onRemoveFromWorld(){
        if(!NetHandler.isClient()){
            for(IChunk chunk : this.chunksInRange){
                chunk.getPlayersInRange().remove(this);
                chunk.getPlayersLeftRange().remove(this);
                chunk.getLeftPlayerTimers().remove(this);
            }
        }
    }

    public String getChatColorFormat(){
        return "&("+this.color.r+","+this.color.g+","+this.color.b+")";
    }
}
