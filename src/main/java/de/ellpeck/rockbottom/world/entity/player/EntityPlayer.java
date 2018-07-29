package de.ellpeck.rockbottom.world.entity.player;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.AbstractEntityItem;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.entity.player.knowledge.IKnowledgeManager;
import de.ellpeck.rockbottom.api.entity.player.statistics.IStatistics;
import de.ellpeck.rockbottom.api.entity.player.statistics.NumberStatistic;
import de.ellpeck.rockbottom.api.entity.spawn.SpawnBehavior;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.ContainerOpenEvent;
import de.ellpeck.rockbottom.api.event.impl.ItemPickupEvent;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.inventory.Inventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.net.INetHandler;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.IntProp;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.*;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.assets.sound.SoundHandler;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;
import de.ellpeck.rockbottom.gui.container.ContainerInventory;
import de.ellpeck.rockbottom.inventory.InventoryPlayer;
import de.ellpeck.rockbottom.net.packet.backandforth.PacketOpenUnboundContainer;
import de.ellpeck.rockbottom.net.packet.toclient.*;
import de.ellpeck.rockbottom.net.packet.toserver.PacketPlayerMovement;
import de.ellpeck.rockbottom.render.entity.PlayerEntityRenderer;
import de.ellpeck.rockbottom.world.entity.player.knowledge.KnowledgeManager;
import de.ellpeck.rockbottom.world.entity.player.statistics.StatisticList;
import de.ellpeck.rockbottom.world.entity.player.statistics.Statistics;
import de.ellpeck.rockbottom.world.tile.TileWater;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public class EntityPlayer extends AbstractEntityPlayer {

    private final KnowledgeManager knowledge = new KnowledgeManager(this);
    private final Statistics statistics = new Statistics();
    private final InventoryPlayer inv = new InventoryPlayer(this);
    private final ItemContainer inventoryContainer = new ContainerInventory(this);
    private final IEntityRenderer renderer = new PlayerEntityRenderer();
    private final List<IChunk> chunksInRange = new ArrayList<>();
    private final IPlayerDesign design;
    private ItemContainer currentContainer;
    public final BiConsumer<IInventory, Integer> invCallback = (inv, slot) -> {
        if (this.world.isServer()) {
            boolean isInv = inv instanceof InventoryPlayer;
            ItemContainer container = isInv ? this.inventoryContainer : this.currentContainer;

            if (container != null) {
                int index = container.getIndexForInvSlot(inv, slot);
                if (index >= 0) {
                    this.sendPacket(new PacketContainerChange(isInv, index, inv.get(slot)));
                }

                if (isInv && slot == this.getSelectedSlot()) {
                    RockBottomAPI.getNet().sendToAllPlayersWithLoadedPosExcept(this.world, new PacketActiveItem(this.getUniqueId(), slot, this.inv.get(slot)), this.getX(), this.getY(), this);
                }
            }
        }
    };
    private int respawnTimer;
    private double lastStatX;
    private double lastStatY;
    private boolean canSwim;

    public EntityPlayer(IWorld world, UUID uniqueId, IPlayerDesign design) {
        super(world);
        this.setUniqueId(uniqueId);
        this.facing = Direction.RIGHT;
        this.design = design;
        this.inv.addChangeCallback(this.invCallback);
    }

    @Override
    public IEntityRenderer getRenderer() {
        return this.renderer;
    }

    @Override
    public boolean openGui(Gui gui) {
        if (this.isLocalPlayer()) {
            RockBottomAPI.getGame().getGuiManager().openGui(gui);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean openGuiContainer(Gui gui, ItemContainer container) {
        boolean containerOpened = this.openContainer(container);
        boolean guiOpened = this.openGui(gui);

        return containerOpened || guiOpened;
    }

    @Override
    public boolean openContainer(ItemContainer container) {
        ContainerOpenEvent event = new ContainerOpenEvent(this, container);
        if (RockBottomAPI.getEventHandler().fireEvent(event) != EventResult.CANCELLED) {
            if (this.currentContainer != null) {
                for (IInventory inv : this.currentContainer.getContainedInventories()) {
                    if (inv != this.inv) {
                        inv.removeChangeCallback(this.invCallback);
                    }
                }

                this.currentContainer.onClosed();
            }

            this.currentContainer = event.container;
            if (this.currentContainer != null) {
                if (this.world.isClient()) {
                    if (this.currentContainer.getName().equals(ContainerInventory.NAME)) {
                        RockBottomAPI.getNet().sendToServer(new PacketOpenUnboundContainer(this.getUniqueId(), PacketOpenUnboundContainer.INV_ID));
                    }
                } else {
                    this.statistics.getOrInit(StatisticList.CONTAINERS_OPENED, NumberStatistic.class).update();

                    if (this.world.isServer()) {
                        this.sendPacket(new PacketContainerData(this.currentContainer));
                    }
                }

                this.currentContainer.onOpened();

                for (IInventory inv : this.currentContainer.getContainedInventories()) {
                    if (inv != this.inv) {
                        inv.addChangeCallback(this.invCallback);
                    }
                }
            } else {
                if (this.world.isClient()) {
                    RockBottomAPI.getNet().sendToServer(new PacketOpenUnboundContainer(this.getUniqueId(), PacketOpenUnboundContainer.CLOSE_ID));
                }
            }

            if (this.currentContainer == null) {
                RockBottomAPI.logger().config("Closed Container for player " + this.getName() + " with unique id " + this.getUniqueId());
            } else {
                RockBottomAPI.logger().config("Opened Container " + this.currentContainer.getName() + " for player " + this.getName() + " with unique id " + this.getUniqueId());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean closeContainer() {
        return this.openContainer(null);
    }

    @Override
    public ItemContainer getContainer() {
        return this.currentContainer;
    }

    @Override
    public void update(IGameInstance game) {
        this.canSwim = false;
        super.update(game);

        if (this.collidedHor) {
            this.motionX = 0;
        }

        double x = this.getX();
        double y = this.getY();

        if (!this.world.isClient()) {
            if (this.isDead()) {
                this.respawnTimer++;

                if (this.respawnTimer >= 400) {
                    this.resetAndSpawn(game);
                }
            } else {
                List<AbstractEntityItem> entities = this.world.getEntities(this.currentBounds.copy().expand(1), AbstractEntityItem.class);
                for (AbstractEntityItem entity : entities) {
                    if (entity.canPickUp()) {
                        double entityX = entity.getX();
                        double entityY = entity.getY();

                        ItemInstance instance = entity.getItem();

                        ItemPickupEvent event = new ItemPickupEvent(this, entity, instance);
                        if (RockBottomAPI.getEventHandler().fireEvent(event) != EventResult.CANCELLED) {
                            instance = event.instance;

                            ItemInstance theoreticalLeft = this.inv.add(instance, true);
                            if (theoreticalLeft == null || theoreticalLeft.getAmount() != instance.getAmount()) {
                                if (Util.distanceSq(entityX, entityY, x, y) <= 0.25) {
                                    ItemInstance left = this.inv.addExistingFirst(instance, false);

                                    if (left == null) {
                                        entity.setReadyToRemove();
                                    } else {
                                        entity.setItem(left);
                                    }
                                } else {
                                    double moveX = x - entityX;
                                    double moveY = y - entityY;
                                    double length = Util.distance(0, 0, moveX, moveY);

                                    entity.motionX = 0.3 * (moveX / length);
                                    entity.motionY = 0.3 * (moveY / length);
                                }
                            }
                        }
                    }
                }

                if (y <= 0 && ConstructionRegistry.ladder != null) {
                    this.getKnowledge().teachRecipe(ConstructionRegistry.ladder, true);
                }

                this.handleEntitySpawns(x, y);
            }

            if (this.world.getTotalTime() % Constants.TARGET_TPS == 0) {
                this.statistics.getOrInit(StatisticList.SECONDS_PLAYED, NumberStatistic.class).update();
            }

            if (Util.distanceSq(this.lastStatX, this.lastStatY, x, y) >= 1F) {
                this.lastStatX = x;
                this.lastStatY = y;
                this.statistics.getOrInit(StatisticList.TILES_WALKED, NumberStatistic.class).update();
            }
        }

        if (this.isLocalPlayer()) {
            SoundHandler.setPlayerPos(x, y);

            if (this.world.isClient() && this.ticksExisted % this.getSyncFrequency() == 0) {
                if (this.lastX != x || this.lastY != y) {
                    RockBottomAPI.getNet().sendToServer(new PacketPlayerMovement(this.getUniqueId(), this.getOriginX(), this.getOriginY(), this.motionX, this.motionY, this.facing, this.collidedHor, this.collidedVert, this.onGround));
                    this.lastX = x;
                    this.lastY = y;
                }
            }
        }
    }

    private void handleEntitySpawns(double thisX, double thisY) {
        for (SpawnBehavior behavior : RockBottomAPI.SPAWN_BEHAVIOR_REGISTRY.values()) {
            if (this.world.getTotalTime() % behavior.getSpawnFrequency(this.world) == 0 && behavior.isReadyToSpawn(this.world)) {
                double cap = behavior.getEntityCap(this.world);
                if (cap > 0) {
                    List<Entity> entities = this.world.getEntities(new BoundBox(thisX, thisY, thisX, thisY).expand(behavior.getEntityCapArea(this.world, this)), behavior::belongsToCap);
                    if (entities.size() >= cap) {
                        continue;
                    }
                }

                double min = behavior.getMinPlayerDistance(this.world, this);
                double max = behavior.getMaxPlayerDistance(this.world, this);

                for (int i = behavior.getSpawnTries(this.world); i > 0; i--) {
                    double x = thisX + (min + Util.RANDOM.nextDouble() * (max - min)) * (Util.RANDOM.nextBoolean() ? 1 : -1);
                    double y = thisY + (min + Util.RANDOM.nextDouble() * (max - min)) * (Util.RANDOM.nextBoolean() ? 1 : -1);

                    for (int j = behavior.getPackSize(this.world, x, y); j > 0; j--) {
                        double theX = x + Util.RANDOM.nextGaussian() * 5D;
                        double theY = y + Util.RANDOM.nextGaussian() * 5D;

                        if (behavior.canSpawnHere(this.world, theX, theY)) {
                            Entity entity = behavior.createEntity(this.world, theX, theY);
                            if (entity != null) {
                                this.world.addEntity(entity);

                                RockBottomAPI.logger().finest("Spawned " + entity + " at " + theX + ", " + theY);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getSyncFrequency() {
        return 2;
    }

    @Override
    public void onGroundHit(double fallDistance) {
        if (!this.world.isClient()) {
            if (fallDistance > 4) {
                this.takeDamage(Util.ceil((fallDistance - 4D) * 4.5D));
            }
        }
    }

    @Override
    public boolean shouldBeRemoved() {
        return false;
    }

    @Override
    public int getInitialMaxHealth() {
        return 100;
    }

    @Override
    public int getRegenRate() {
        return 10;
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        super.setMaxHealth(maxHealth);

        if (!this.world.isDedicatedServer()) {
            RockBottomAPI.getGame().getGuiManager().initOnScreenComponents();
        }
    }

    @Override
    public void save(DataSet set) {
        super.save(set);
        this.inv.save(set);
        this.knowledge.save(set);
        this.statistics.save(set);
    }

    @Override
    public void load(DataSet set) {
        super.load(set);
        this.inv.load(set);
        this.knowledge.load(set);
        this.statistics.load(set);
    }

    @Override
    public void sendPacket(IPacket packet) {

    }

    @Override
    public void onChunkLoaded(IChunk chunk) {

    }

    @Override
    public void onChunkUnloaded(IChunk chunk) {

    }

    @Override
    public void moveToChunk(IChunk newChunk) {
        super.moveToChunk(newChunk);

        if (!this.world.isClient()) {
            List<IChunk> nowLoaded = new ArrayList<>();

            for (int x = -Constants.CHUNK_LOAD_DISTANCE; x <= Constants.CHUNK_LOAD_DISTANCE; x++) {
                for (int y = Constants.CHUNK_LOAD_DISTANCE; y >= -Constants.CHUNK_LOAD_DISTANCE; y--) {
                    IChunk chunk = this.world.getChunkFromGridCoords(this.chunkX + x, this.chunkY + y);
                    nowLoaded.add(chunk);
                }
            }

            int newLoad = nowLoaded.size();
            int unload = 0;
            for (IChunk chunk : this.chunksInRange) {
                int nowIndex = nowLoaded.indexOf(chunk);

                if (nowIndex < 0) {
                    List<AbstractEntityPlayer> inRange = chunk.getPlayersInRange();
                    if (inRange.contains(this)) {
                        inRange.remove(this);
                        unload++;
                    }

                    List<AbstractEntityPlayer> outOfRange = chunk.getPlayersLeftRange();
                    if (!outOfRange.contains(this)) {
                        outOfRange.add(this);
                        chunk.getLeftPlayerTimers().put(this, new Counter(Constants.CHUNK_LOAD_TIME));
                    }
                } else {
                    newLoad--;
                }
            }

            RockBottomAPI.logger().config("Player " + this.getName() + " with id " + this.getUniqueId() + " leaving range of " + unload + " chunks and loading " + newLoad + " new ones");

            for (IChunk chunk : nowLoaded) {
                List<AbstractEntityPlayer> inRange = chunk.getPlayersInRange();
                if (!inRange.contains(this)) {
                    inRange.add(this);
                }

                if (!this.chunksInRange.contains(chunk)) {
                    this.chunksInRange.add(chunk);

                    this.onChunkLoaded(chunk);
                } else {
                    chunk.getPlayersLeftRange().remove(this);
                    chunk.getLeftPlayerTimers().remove(this);
                }
            }
        }
    }

    @Override
    public List<IChunk> getChunksInRange() {
        return this.chunksInRange;
    }

    @Override
    public int getCommandLevel() {
        if (this.world.isServer()) {
            INetHandler net = RockBottomAPI.getNet();
            int level = net.getCommandLevel(this);

            if (level < Constants.ADMIN_PERMISSION && this.isLocalPlayer()) {
                level = Constants.ADMIN_PERMISSION;

                net.setCommandLevel(this, level);
                net.saveServerSettings();

                RockBottomAPI.logger().info("Setting command level for server host " + this.getName() + " with id " + this.getUniqueId() + " to " + level + '!');
            }

            return level;
        } else {
            return 0;
        }
    }

    @Override
    public void setDead(boolean dead) {
        super.setDead(dead);

        if (!this.world.isClient() && dead) {
            int id = Util.RANDOM.nextInt(25) + 1;
            RockBottomAPI.getGame().getChatLog().broadcastMessage(new ChatComponentText(FormattingCode.RED.toString()).append(new ChatComponentTranslation(ResourceName.intern("death.flavor." + id), this.getName())));

            this.statistics.getOrInit(StatisticList.DEATHS, NumberStatistic.class).update();
        }
    }

    @Override
    public void onRemoveFromWorld() {
        if (!this.world.isClient()) {
            for (IChunk chunk : this.chunksInRange) {
                chunk.getPlayersInRange().remove(this);
                chunk.getPlayersLeftRange().remove(this);
                chunk.getLeftPlayerTimers().remove(this);
            }
        }
    }

    @Override
    public ItemContainer getInvContainer() {
        return this.inventoryContainer;
    }

    @Override
    public Inventory getInv() {
        return this.inv;
    }

    @Override
    public int getSelectedSlot() {
        return this.inv.selectedSlot;
    }

    @Override
    public void setSelectedSlot(int slot) {
        this.inv.selectedSlot = slot;

        if (this.world.isServer()) {
            RockBottomAPI.getNet().sendToAllPlayersExcept(this.world, new PacketActiveItem(this.getUniqueId(), slot, this.inv.get(slot)), this);
        }
    }

    @Override
    public String getChatColorFormat() {
        int color = this.design.getFavoriteColor();
        return Colors.toFormattingCode(color);
    }

    @Override
    public void sendMessageTo(IChatLog chat, ChatComponent message) {
        if (this.isLocalPlayer()) {
            chat.displayMessage(message);
        } else if (RockBottomAPI.getNet().isActive()) {
            this.sendPacket(new PacketChatMessage(message));
        }
    }

    @Override
    public String getName() {
        return this.design.getName();
    }

    @Override
    public int getColor() {
        return this.design.getFavoriteColor();
    }

    @Override
    public IPlayerDesign getDesign() {
        return this.design;
    }

    @Override
    public boolean isInRange(double x, double y, double maxDistance) {
        return Util.distanceSq(this.getX(), this.getY() + 1, x, y) <= maxDistance * maxDistance;
    }

    @Override
    public IKnowledgeManager getKnowledge() {
        return this.knowledge;
    }

    @Override
    public IStatistics getStatistics() {
        return this.statistics;
    }

    @Override
    public double getMoveSpeed() {
        double speed = MOVE_SPEED;

        if (this.hasEffect(GameContent.EFFECT_SPEED)) {
            speed += 0.07D;
        }

        return speed;
    }

    @Override
    public double getClimbSpeed() {
        return CLIMB_SPEED;
    }

    @Override
    public double getJumpHeight() {
        double height = 0.29D;

        if (this.hasEffect(GameContent.EFFECT_JUMP_HEIGHT)) {
            height += 0.125D;
        }

        return height;
    }

    @Override
    public boolean isLocalPlayer() {
        return this.world.isLocalPlayer(this);
    }

    public void resetAndSpawn(IGameInstance game, double x, double y) {
        this.respawnTimer = 0;
        this.dead = false;
        this.motionX = 0;
        this.motionY = 0;
        this.isFalling = false;
        this.fallStartY = 0;
        this.setHealth(this.getMaxHealth());
        this.setBreath(this.getMaxBreath());

        if (this.isLocalPlayer()) {
            if (game.getGuiManager() != null) {
                game.getGuiManager().closeGui();
            }
        }

        this.setPos(x, y);

        if (this.world.isServer()) {
            RockBottomAPI.getNet().sendToAllPlayers(this.world, new PacketRespawn(this.getUniqueId(), x, y));
        }
    }

    @Override
    public void resetAndSpawn(IGameInstance game) {
        int tries = 0;
        double x;
        double y;

        do {
            x = this.world.getSpawnX() + Util.RANDOM.nextInt(33) - 16 + 0.5;
            y = this.world.getChunkHeight(TileLayer.MAIN, Util.floor(x), 0) + 1;

            tries++;
            if (tries >= 50) {
                RockBottomAPI.logger().warning("Couldn't spawn the player at a valid position, spawning them at " + x + ", " + y + " instead - Is there any space at spawn?");
                break;
            }
        }
        while (y <= 1);

        this.resetAndSpawn(game, x, y);
    }

    @Override
    public boolean move(int type) {
        if (type == 0) {
            this.motionX -= this.getMoveSpeed();
            this.facing = Direction.LEFT;
            return true;
        } else if (type == 1) {
            this.motionX += this.getMoveSpeed();
            this.facing = Direction.RIGHT;
            return true;
        } else if (type == 2) {
            if (this.canSwim) {
                this.motionY = 0.075;
            } else {
                this.jump(this.getJumpHeight());
            }
            return true;
        } else if (type == 3) {
            if (this.canClimb) {
                this.motionY += this.getClimbSpeed();
                this.facing = Direction.UP;
                return true;
            }
        } else if (type == 4) {
            if (this.canClimb) {
                this.motionY -= this.getClimbSpeed();
                this.facing = Direction.DOWN;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldStartClimbing(int x, int y, TileLayer layer, TileState state, BoundBox entityBox, BoundBox entityBoxMotion, List<BoundBox> tileBoxes) {
        return true;
    }

    @Override
    public float getWidth() {
        return 0.83F;
    }

    @Override
    public float getHeight() {
        return 1.85F;
    }

    @Override
    public void onIntersectWithTile(int x, int y, TileLayer layer, TileState state, BoundBox entityBox, BoundBox entityBoxMotion, List<BoundBox> tileBoxes) {
        super.onIntersectWithTile(x, y, layer, state, entityBox, entityBoxMotion, tileBoxes);

        if (!this.canSwim) {
            if (this.collidedHor || y >= this.getY() - 1) {
                Tile tile = state.getTile();
                if (tile instanceof TileWater) {
                    IntProp levels = ((TileWater) tile).level;
                    if (state.get(levels) > levels.getVariants() / 2) {
                        for (BoundBox box : tileBoxes) {
                            if (entityBox.intersects(box)) {
                                this.canSwim = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public double getEyeHeight() {
        return this.getHeight() * 0.75D;
    }
}
