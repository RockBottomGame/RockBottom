package de.ellpeck.rockbottom.world.entity.player;

import de.ellpeck.rockbottom.Constants;
import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.data.set.DataSet;
import de.ellpeck.rockbottom.gui.Gui;
import de.ellpeck.rockbottom.gui.container.ContainerInventory;
import de.ellpeck.rockbottom.gui.container.ItemContainer;
import de.ellpeck.rockbottom.inventory.IInvChangeCallback;
import de.ellpeck.rockbottom.inventory.IInventory;
import de.ellpeck.rockbottom.inventory.InventoryPlayer;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.net.packet.IPacket;
import de.ellpeck.rockbottom.net.packet.toclient.PacketContainerChange;
import de.ellpeck.rockbottom.net.packet.toclient.PacketContainerData;
import de.ellpeck.rockbottom.net.packet.toserver.PacketOpenUnboundContainer;
import de.ellpeck.rockbottom.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.render.entity.PlayerEntityRenderer;
import de.ellpeck.rockbottom.data.settings.CommandPermissions;
import de.ellpeck.rockbottom.util.BoundBox;
import de.ellpeck.rockbottom.util.Direction;
import de.ellpeck.rockbottom.util.Util;
import de.ellpeck.rockbottom.world.Chunk;
import de.ellpeck.rockbottom.world.TileLayer;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.EntityItem;
import de.ellpeck.rockbottom.world.entity.EntityLiving;
import org.newdawn.slick.Color;
import org.newdawn.slick.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EntityPlayer extends EntityLiving implements IInvChangeCallback{

    private final BoundBox boundingBox = new BoundBox(-0.5, -0.5, 0.5, 1.5);
    public Color color = Util.randomColor(Util.RANDOM);

    private final IEntityRenderer renderer;
    public final InventoryPlayer inv = new InventoryPlayer(this);

    public final ItemContainer inventoryContainer = new ContainerInventory(this);
    private ItemContainer currentContainer;

    public List<Chunk> chunksInRange = new ArrayList<>();

    private int respawnTimer;

    public EntityPlayer(World world){
        super(world);
        this.renderer = new PlayerEntityRenderer();
        this.facing = Direction.RIGHT;
    }

    public EntityPlayer(World world, UUID uniqueId){
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
            RockBottom.get().guiManager.openGui(gui);
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
    public void update(RockBottom game){
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
    }

    @Override
    public int getUpdateFrequency(){
        return 1;
    }

    public void resetAndSpawn(RockBottom game){
        this.respawnTimer = 0;
        this.dead = false;
        this.motionX = 0;
        this.motionY = 0;
        this.fallAmount = 0;
        this.health = this.getMaxHealth();

        if(game.guiManager != null){
            game.guiManager.closeGui();
        }

        this.setPos(this.world.spawnX+0.5, this.world.getLowestAirUpwards(TileLayer.MAIN, this.world.spawnX, 0)+1);
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

    public void onChunkNewlyLoaded(Chunk chunk){

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
    public void moveToChunk(Chunk newChunk){
        super.moveToChunk(newChunk);

        if(NetHandler.isServer() || NetHandler.isThePlayer(this)){
            List<Chunk> nowLoaded = new ArrayList<>();

            for(int x = -Constants.CHUNK_LOAD_DISTANCE; x <= Constants.CHUNK_LOAD_DISTANCE; x++){
                for(int y = Constants.CHUNK_LOAD_DISTANCE; y >= -Constants.CHUNK_LOAD_DISTANCE; y--){
                    Chunk chunk = this.world.getChunkFromGridCoords(this.chunkX+x, this.chunkY+y);
                    nowLoaded.add(chunk);
                }
            }

            int unload = 0;
            for(Chunk chunk : this.chunksInRange){
                int nowIndex = nowLoaded.indexOf(chunk);

                if(nowIndex < 0){
                    chunk.playersInRange.remove(this);
                    chunk.playersOutOfRangeCached.add(this);

                    unload++;
                }
                else{
                    nowLoaded.remove(nowIndex);
                }
            }

            Log.debug("Player with id "+this.getUniqueId()+" unloading "+unload+" chunks and loading "+nowLoaded.size()+" new ones");

            for(Chunk chunk : nowLoaded){
                chunk.playersInRange.add(this);

                if(!this.chunksInRange.contains(chunk)){
                    this.chunksInRange.add(chunk);

                    this.onChunkNewlyLoaded(chunk);
                }
                else{
                    chunk.playersOutOfRangeCached.remove(this);
                }
            }
        }
    }

    public int getCommandLevel(){
        if(NetHandler.isServer()){
            CommandPermissions permissions = NetHandler.getCommandPermissions();
            int level = permissions.getCommandLevel(this);

            if(level <= 0 && NetHandler.isThePlayer(this)){
                level = 10;
                permissions.setCommandLevel(this, level);

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
        for(Chunk chunk : this.chunksInRange){
            chunk.playersInRange.remove(this);
            chunk.playersOutOfRangeCached.remove(this);
        }
    }
}
