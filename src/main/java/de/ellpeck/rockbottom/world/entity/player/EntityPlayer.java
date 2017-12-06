package de.ellpeck.rockbottom.world.entity.player;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.net.INetHandler;
import de.ellpeck.rockbottom.net.server.settings.CommandPermissions;
import de.ellpeck.rockbottom.api.entity.EntityItem;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.entity.player.knowledge.IKnowledgeManager;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.ContainerOpenEvent;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.inventory.Inventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.*;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;
import de.ellpeck.rockbottom.gui.container.ContainerInventory;
import de.ellpeck.rockbottom.inventory.InventoryPlayer;
import de.ellpeck.rockbottom.net.packet.toclient.*;
import de.ellpeck.rockbottom.net.packet.toserver.PacketOpenUnboundContainer;
import de.ellpeck.rockbottom.render.entity.PlayerEntityRenderer;
import de.ellpeck.rockbottom.world.entity.player.knowledge.KnowledgeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public class EntityPlayer extends AbstractEntityPlayer{

    private final KnowledgeManager knowledge = new KnowledgeManager(this);
    private final InventoryPlayer inv = new InventoryPlayer(this);
    private final ItemContainer inventoryContainer = new ContainerInventory(this);
    private final BoundBox boundingBox = new BoundBox(-0.45, -0.5, 0.45, 1.35);
    private final IEntityRenderer renderer = new PlayerEntityRenderer();
    private final List<IChunk> chunksInRange = new ArrayList<>();
    private final IPlayerDesign design;
    private ItemContainer currentContainer;
    public final BiConsumer<IInventory, Integer> invCallback = (inv, slot) -> {
        if(this.world.isServer()){
            boolean isInv = inv instanceof InventoryPlayer;
            ItemContainer container = isInv ? this.inventoryContainer : this.currentContainer;

            if(container != null){
                int index = container.getIndexForInvSlot(inv, slot);
                if(index >= 0){
                    this.sendPacket(new PacketContainerChange(isInv, index, inv.get(slot)));
                }

                if(isInv && slot == this.getSelectedSlot()){
                    RockBottomAPI.getNet().sendToAllPlayersWithLoadedPosExcept(this.world, new PacketActiveItem(this.getUniqueId(), slot, this.inv.get(slot)), this.x, this.y, this);
                }
            }
        }
    };
    private int respawnTimer;

    public EntityPlayer(IWorld world, UUID uniqueId, IPlayerDesign design){
        super(world);
        this.facing = Direction.RIGHT;
        this.uniqueId = uniqueId;
        this.design = design;
        this.inv.addChangeCallback(this.invCallback);
    }

    @Override
    public IEntityRenderer getRenderer(){
        return this.renderer;
    }

    @Override
    public boolean openGui(Gui gui){
        if(RockBottomAPI.getNet().isThePlayer(this)){
            RockBottomAPI.getGame().getGuiManager().openGui(gui);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public boolean openGuiContainer(Gui gui, ItemContainer container){
        boolean containerOpened = this.openContainer(container);
        boolean guiOpened = this.openGui(gui);

        return containerOpened || guiOpened;
    }

    @Override
    public boolean openContainer(ItemContainer container){
        ContainerOpenEvent event = new ContainerOpenEvent(this, container);
        if(RockBottomAPI.getEventHandler().fireEvent(event) != EventResult.CANCELLED){
            if(this.currentContainer != null){
                for(IInventory inv : this.currentContainer.containedInventories){
                    if(inv != this.inv){
                        inv.removeChangeCallback(this.invCallback);
                    }
                }

                this.currentContainer.onClosed();
            }

            this.currentContainer = event.container;

            if(this.currentContainer != null){
                if(this.world.isClient()){
                    int id = this.currentContainer.getUnboundId();
                    if(id >= 0){
                        RockBottomAPI.getNet().sendToServer(new PacketOpenUnboundContainer(this.getUniqueId(), id));
                    }
                }
                else if(this.world.isServer()){
                    this.sendPacket(new PacketContainerData(this.currentContainer));
                }

                this.currentContainer.onOpened();

                for(IInventory inv : this.currentContainer.containedInventories){
                    if(inv != this.inv){
                        inv.addChangeCallback(this.invCallback);
                    }
                }
            }
            else{
                if(this.world.isClient()){
                    RockBottomAPI.getNet().sendToServer(new PacketOpenUnboundContainer(this.getUniqueId(), PacketOpenUnboundContainer.CLOSE_ID));
                }
            }

            if(this.currentContainer == null){
                RockBottomAPI.logger().config("Closed Container for player "+this.getName()+" with unique id "+this.getUniqueId());
            }
            else{
                RockBottomAPI.logger().config("Opened Container "+this.currentContainer.getName()+" for player "+this.getName()+" with unique id "+this.getUniqueId());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean closeContainer(){
        return this.openContainer(null);
    }

    @Override
    public ItemContainer getContainer(){
        return this.currentContainer;
    }

    @Override
    public void update(IGameInstance game){
        super.update(game);

        if(!this.world.isClient()){
            if(this.isDead()){
                this.respawnTimer++;

                if(this.respawnTimer >= 400){
                    this.resetAndSpawn(game);
                }
            }
            else{
                List<EntityItem> entities = this.world.getEntities(this.getBoundingBox().copy().add(this.x, this.y).expand(1), EntityItem.class);
                for(EntityItem entity : entities){
                    if(entity.canPickUp()){
                        if(Util.distanceSq(entity.x, entity.y, this.x, this.y+0.5) <= 0.25){
                            ItemInstance left = this.inv.addExistingFirst(entity.item, false);

                            if(left == null){
                                entity.kill();
                            }
                            else{
                                entity.item = left;
                            }
                        }
                        else{
                            double x = this.x-entity.x;
                            double y = (this.y+0.5)-entity.y;
                            double length = Util.distance(0, 0, x, y);

                            entity.motionX = 0.3*(x/length);
                            entity.motionY = 0.3*(y/length);
                        }
                    }
                }

                if(this.y <= 0){
                    this.getKnowledge().teachRecipe(ConstructionRegistry.ladder, true);
                }
            }
        }

        if(RockBottomAPI.getNet().isThePlayer(this)){
            int range = 24;
            int layers = TileLayer.getAllLayers().size();
            for(int i = 0; i < Constants.RANDOM_TILE_RENDER_UPDATES*layers; i++){
                TileLayer layer = TileLayer.getAllLayers().get(Util.RANDOM.nextInt(layers));
                int x = Util.floor(this.x)+Util.RANDOM.nextInt(range*2+1)-range;
                int y = Util.floor(this.y)+Util.RANDOM.nextInt(range*2+1)-range;

                if(this.world.isPosLoaded(x, y)){
                    Tile tile = this.world.getState(layer, x, y).getTile();
                    tile.updateRandomlyForRendering(this.world, x, y, layer, this);
                }
            }
        }
    }

    @Override
    public int getSyncFrequency(){
        return 1;
    }

    @Override
    public void onGroundHit(double fallDistance){
        if(!this.world.isClient()){
            if(fallDistance > 5){
                this.takeDamage(Util.ceil((fallDistance-5D)*2D));
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
        this.knowledge.save(set);
    }

    @Override
    public void load(DataSet set){
        super.load(set);
        this.inv.load(set);
        this.knowledge.load(set);
    }

    @Override
    public void sendPacket(IPacket packet){

    }

    @Override
    public void onChunkLoaded(IChunk chunk){

    }

    @Override
    public void onChunkUnloaded(IChunk chunk){

    }

    @Override
    public void moveToChunk(IChunk newChunk){
        super.moveToChunk(newChunk);

        if(!this.world.isClient()){
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
                    List<AbstractEntityPlayer> inRange = chunk.getPlayersInRange();
                    if(inRange.contains(this)){
                        inRange.remove(this);
                        unload++;
                    }

                    List<AbstractEntityPlayer> outOfRange = chunk.getPlayersLeftRange();
                    if(!outOfRange.contains(this)){
                        outOfRange.add(this);
                        chunk.getLeftPlayerTimers().put(this, new Counter(Constants.CHUNK_LOAD_TIME));
                    }
                }
                else{
                    newLoad--;
                }
            }

            RockBottomAPI.logger().config("Player "+this.getName()+" with id "+this.getUniqueId()+" leaving range of "+unload+" chunks and loading "+newLoad+" new ones");

            for(IChunk chunk : nowLoaded){
                List<AbstractEntityPlayer> inRange = chunk.getPlayersInRange();
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

    @Override
    public List<IChunk> getChunksInRange(){
        return this.chunksInRange;
    }

    @Override
    public int getCommandLevel(){
        if(this.world.isServer()){
            INetHandler net = RockBottomAPI.getNet();
            int level = net.getCommandLevel(this);

            if(level < Constants.ADMIN_PERMISSION && RockBottomAPI.getNet().isThePlayer(this)){
                level = Constants.ADMIN_PERMISSION;

                net.setCommandLevel(this, level);
                net.saveServerSettings();

                RockBottomAPI.logger().info("Setting command level for server host "+this.getName()+" with id "+this.getUniqueId()+" to "+level+"!");
            }

            return level;
        }
        else{
            return 0;
        }
    }

    @Override
    public void setDead(boolean dead){
        super.setDead(dead);

        if(!this.world.isClient() && this.dead){
            int id = Util.RANDOM.nextInt(25)+1;
            RockBottomAPI.getGame().getChatLog().broadcastMessage(new ChatComponentText(FormattingCode.RED.toString()).append(new ChatComponentTranslation(RockBottomAPI.createInternalRes("death.flavor."+id), this.getName())));
        }
    }

    @Override
    public void onRemoveFromWorld(){
        if(!this.world.isClient()){
            for(IChunk chunk : this.chunksInRange){
                chunk.getPlayersInRange().remove(this);
                chunk.getPlayersLeftRange().remove(this);
                chunk.getLeftPlayerTimers().remove(this);
            }
        }
    }

    @Override
    public ItemContainer getInvContainer(){
        return this.inventoryContainer;
    }

    @Override
    public Inventory getInv(){
        return this.inv;
    }

    @Override
    public int getSelectedSlot(){
        return this.inv.selectedSlot;
    }

    @Override
    public void setSelectedSlot(int slot){
        this.inv.selectedSlot = slot;

        if(this.world.isServer()){
            RockBottomAPI.getNet().sendToAllPlayersExcept(this.world, new PacketActiveItem(this.getUniqueId(), slot, this.inv.get(slot)), this);
        }
    }

    @Override
    public String getChatColorFormat(){
        int color = this.design.getFavoriteColor();
        return Colors.toFormattingCode(color);
    }

    @Override
    public void sendMessageTo(IChatLog chat, ChatComponent message){
        if(RockBottomAPI.getNet().isThePlayer(this)){
            chat.displayMessage(message);
        }
        else if(RockBottomAPI.getNet().isActive()){
            this.sendPacket(new PacketChatMessage(message));
        }
    }

    @Override
    public String getName(){
        return this.design.getName();
    }

    @Override
    public int getColor(){
        return this.design.getFavoriteColor();
    }

    @Override
    public IPlayerDesign getDesign(){
        return this.design;
    }

    @Override
    public boolean isInRange(double x, double y, double maxDistance){
        return Util.distanceSq(this.x, this.y+1, x, y) <= maxDistance*maxDistance;
    }

    @Override
    public IKnowledgeManager getKnowledge(){
        return this.knowledge;
    }

    @Override
    public void resetAndSpawn(IGameInstance game){
        this.respawnTimer = 0;
        this.dead = false;
        this.motionX = 0;
        this.motionY = 0;
        this.isFalling = false;
        this.fallStartY = 0;
        this.setHealth(this.getMaxHealth());

        if(RockBottomAPI.getNet().isThePlayer(this)){
            if(game.getGuiManager() != null){
                game.getGuiManager().closeGui();
            }
        }

        this.setPos(this.world.getSpawnX()+0.5, this.world.getLowestAirUpwards(TileLayer.MAIN, this.world.getSpawnX(), 0)+1);

        if(this.world.isServer()){
            RockBottomAPI.getNet().sendToAllPlayers(this.world, new PacketRespawn(this.getUniqueId()));
        }
    }

    @Override
    public boolean move(int type){
        if(type == 0){
            this.motionX -= MOVE_SPEED;
            this.facing = Direction.LEFT;
            return true;
        }
        else if(type == 1){
            this.motionX += MOVE_SPEED;
            this.facing = Direction.RIGHT;
            return true;
        }
        else if(type == 2){
            this.jump(0.28);
            return true;
        }
        else if(type == 3){
            if(this.canClimb){
                this.motionY += CLIMB_SPEED;
                this.facing = Direction.UP;
                return true;
            }
        }
        else if(type == 4){
            if(this.canClimb){
                this.motionY -= CLIMB_SPEED;
                this.facing = Direction.DOWN;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldStartClimbing(int x, int y, TileLayer layer, TileState state, BoundBox entityBox, BoundBox entityBoxMotion, List<BoundBox> tileBoxes){
        return true;
    }
}
