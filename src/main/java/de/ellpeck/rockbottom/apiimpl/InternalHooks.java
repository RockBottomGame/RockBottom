package de.ellpeck.rockbottom.apiimpl;

import com.google.common.collect.Table;
import de.ellpeck.rockbottom.api.*;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FontProp;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.construction.compendium.PlayerCompendiumRecipe;
import de.ellpeck.rockbottom.api.construction.compendium.smithing.SmithingRecipe;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.effect.ActiveEffect;
import de.ellpeck.rockbottom.api.effect.IEffect;
import de.ellpeck.rockbottom.api.entity.AbstractEntityItem;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.MovableWorldObject;
import de.ellpeck.rockbottom.api.entity.ai.AITask;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.entity.player.statistics.ItemStatistic;
import de.ellpeck.rockbottom.api.entity.player.statistics.NumberStatistic;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.PlaceTileEvent;
import de.ellpeck.rockbottom.api.event.impl.WorldObjectCollisionEvent;
import de.ellpeck.rockbottom.api.gui.AbstractStatGui;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.component.*;
import de.ellpeck.rockbottom.api.gui.container.ContainerSlot;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.internal.IInternalHooks;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.TileLiquid;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.state.IStateHandler;
import de.ellpeck.rockbottom.api.tile.state.TileProp;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.*;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.construction.criteria.CriteriaBreakTile;
import de.ellpeck.rockbottom.gui.GuiSmithing;
import de.ellpeck.rockbottom.log.Logging;
import de.ellpeck.rockbottom.net.packet.toclient.*;
import de.ellpeck.rockbottom.net.packet.toserver.PacketDrop;
import de.ellpeck.rockbottom.net.packet.toserver.PacketSetOrPickHolding;
import de.ellpeck.rockbottom.net.packet.toserver.PacketShiftClick;
import de.ellpeck.rockbottom.world.entity.EntityItem;
import de.ellpeck.rockbottom.world.entity.player.InteractionManager;
import de.ellpeck.rockbottom.world.entity.player.statistics.StatisticList;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

public class InternalHooks implements IInternalHooks {

    public static boolean setOrPickUpHolding(AbstractEntityPlayer player, ItemContainer container, int slotId, boolean half) {
        if (player.world.isClient()) {
            RockBottomAPI.getNet().sendToServer(new PacketSetOrPickHolding(player.getUniqueId(), slotId, half));
        }

        ContainerSlot slot = container.getSlot(slotId);
        ItemInstance slotInst = slot.get();

        if (half) {
            if (container.holdingInst == null) {
                if (slotInst != null) {
                    int halfAmount = Util.ceil(slot.get().getAmount() / 2D);
                    if (slot.canRemove(halfAmount)) {
                        container.holdingInst = slotInst.copy().setAmount(halfAmount);
                        slot.set(slotInst.removeAmount(halfAmount).nullIfEmpty());
                        return true;
                    }
                }
            } else {
                boolean should = false;

                if (slotInst != null) {
                    if (slotInst.isEffectivelyEqual(container.holdingInst) && slotInst.getAmount() < slotInst.getMaxAmount()) {
                        should = true;

                        ItemInstance newInst = slotInst.addAmount(1);
                        if (slot.canPlace(newInst)) {
                            slot.set(newInst);
                        }
                    }
                } else {
                    should = true;

                    ItemInstance newInst = container.holdingInst.copy().setAmount(1);
                    if (slot.canPlace(newInst)) {
                        slot.set(newInst);
                    }
                }

                if (should) {
                    container.holdingInst = container.holdingInst.removeAmount(1).nullIfEmpty();
                    return true;
                }
            }
        } else {
            if (container.holdingInst == null) {
                if (slotInst != null && slot.canRemove(slotInst.getAmount())) {
                    container.holdingInst = slotInst;
                    slot.set(null);
                    return true;
                }
            } else {
                int removeAmount = 0;

                if (slotInst != null) {
                    if (slotInst.isEffectivelyEqual(container.holdingInst)) {
                        int possibleAdd = Math.min(slotInst.getMaxAmount() - slotInst.getAmount(), container.holdingInst.getAmount());
                        if (possibleAdd > 0) {
                            ItemInstance newInst = slotInst.copy().addAmount(possibleAdd);
                            if (slot.canPlace(newInst)) {
                                removeAmount = possibleAdd;
                                slot.set(newInst);
                            }
                        }
                    } else if (slot.canRemove(slotInst.getAmount()) && slot.canPlace(container.holdingInst)) {
                        ItemInstance slotCopy = slotInst.copy();
                        slot.set(container.holdingInst);
                        container.holdingInst = slotCopy;
                        return true;
                    }
                } else if (slot.canPlace(container.holdingInst)) {
                    removeAmount = container.holdingInst.getAmount();
                    slot.set(container.holdingInst.copy());
                }

                if (removeAmount > 0) {
                    container.holdingInst = container.holdingInst.removeAmount(removeAmount).nullIfEmpty();
                    return true;
                }
            }
        }
        return false;
    }

    public static int shiftClick(AbstractEntityPlayer player, ItemContainer container, int from, int into) {
        if (player.world.isClient()) {
            RockBottomAPI.getNet().sendToServer(new PacketShiftClick(player.getUniqueId(), from, into));
        }

        ContainerSlot slotFrom = container.getSlot(from);
        ContainerSlot slotInto = container.getSlot(into);

        ItemInstance fromInst = slotFrom.get();
        ItemInstance intoInst = slotInto.get();

        if (intoInst == null) {
            if (slotInto.canPlace(fromInst) && slotFrom.canRemove(fromInst.getAmount())) {
                slotInto.set(fromInst);
                slotFrom.set(null);
                return 1;
            }
        } else if (intoInst.isEffectivelyEqual(fromInst)) {
            int possible = Math.min(intoInst.getMaxAmount() - intoInst.getAmount(), fromInst.getAmount());
            if (possible > 0 && slotFrom.canRemove(possible)) {
                ItemInstance newInto = intoInst.copy().addAmount(possible);
                if (slotInto.canPlace(newInto)) {
                    int remaining = fromInst.getAmount() - possible;
                    slotInto.set(newInto);
                    if (remaining <= 0) {
                        slotFrom.set(null);
                        return 1;
                    } else {
                        slotFrom.set(fromInst.copy().setAmount(remaining));
                        return 2;
                    }
                }
            }
        }
        return 0;
    }

    public static ResourceName generateTileStateName(Tile tile, Map<String, Comparable> properties) {
        StringBuilder suffix = new StringBuilder();

        if (!properties.isEmpty()) {
            suffix.append(';');

            Iterator<Map.Entry<String, Comparable>> iterator = properties.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Comparable> entry = iterator.next();

                String append = entry.getKey() + '@' + entry.getValue();
                if (iterator.hasNext()) {
                    append += ",";
                }

                suffix.append(append);
            }
        }

        return tile.getName().addSuffix(suffix.toString());
    }

    @Override
    public void doDefaultEntityUpdate(IGameInstance game, Entity entity, List<ActiveEffect> effects, List<AITask> aiTasks) {
        double x = entity.getX();
        double y = entity.getY();

        entity.applyMotion();

        if (!entity.isDead()) {
            AITask currTask = entity.currentAiTask;
            if (!entity.world.isClient()) {
                boolean quitCurrentTask = true;
                int lowestFindPrio = Integer.MIN_VALUE;

                if (currTask != null) {
                    currTask.execute(game, entity);

                    if (!currTask.shouldEndExecution(entity)) {
                        quitCurrentTask = false;
                        lowestFindPrio = currTask.getPriority() + 1;
                    }
                }

                int newTaskId = -1;

                for (int i = 0; i < aiTasks.size(); i++) {
                    AITask task = aiTasks.get(i);
                    if (task.getPriority() >= lowestFindPrio && task.shouldStartExecution(entity)) {
                        newTaskId = i;
                        break;
                    }
                }

                if (quitCurrentTask || newTaskId >= 0) {
                    AITask newTask = entity.getTask(newTaskId);
                    if (currTask != null) {
                        newTask = currTask.getNextTask(newTask, entity);
                    }

                    PacketAITask.setNewTask(entity, currTask, newTask);

                    if (entity.world.isServer()) {
                        DataSet data = new DataSet();
                        if (newTask != null) {
                            newTask.save(data, true, entity);
                        }
                        RockBottomAPI.getNet().sendToAllPlayersWithLoadedPos(entity.world, new PacketAITask(entity.getUniqueId(), data, newTaskId), x, y);
                    }
                }
            } else {
                if (currTask != null) {
                    currTask.execute(game, entity);

                    if (currTask.shouldEndExecution(entity)) {
                        entity.currentAiTask = null;
                    }
                }
            }
        }

        entity.canClimb = false;
        entity.isClimbing = false;
        entity.submergedLiquid = null;
        entity.canBreathe = true;
        entity.canSwim = false;
        entity.move();

        x = entity.getX();
        y = entity.getY();

        if (entity.shouldBeFalling()) {
            if (entity.motionY < 0) {
                if (!entity.isFalling) {
                    entity.isFalling = true;
                    entity.fallStartY = y;
                }
            }
        } else {
            if (entity.onGround) {
                entity.motionY = 0;
            }

            if (entity.isFalling) {
                entity.onGroundHit(Math.max(0D, entity.fallStartY - y));

                entity.isFalling = false;
                entity.fallStartY = 0;
            }
        }

        if (!entity.isDead()) {
            for (int i = effects.size() - 1; i >= 0; i--) {
                ActiveEffect active = effects.get(i);

                IEffect effect = active.getEffect();
                if (!effect.isInstant(entity)) {
                    effect.updateLasting(active, entity);
                }

                active.removeTime(1);
                if (active.getTime() <= 0) {
                    effects.remove(i);
                    effect.onRemovedOrEnded(active, entity, true);
                }
            }
        }

        entity.ticksExisted++;

        if (entity.world.isServer()) {
            if (entity.doesSync()) {
                if (entity.ticksExisted % entity.getSyncFrequency() == 0) {
                    if (entity.lastSyncX != x || entity.lastSyncY != y) {
                        RockBottomAPI.getNet().sendToAllPlayersWithLoadedPosExcept(entity.world, new PacketEntityUpdate(entity.getUniqueId(), entity.getOriginX(), entity.getOriginY(), entity.motionX, entity.motionY, entity.facing), x, y, entity);

                        entity.lastSyncX = x;
                        entity.lastSyncY = y;
                    }
                }
            }
        }
    }

    @Override
    public void doWorldObjectMovement(MovableWorldObject object) {
        double motionX = object.motionX;
        double motionY = object.motionY;

        BoundBox ownBox = object.currentBounds;
        BoundBox ownBoxMotion = ownBox.copy().add(motionX, motionY);

        if (object.world.isPosLoaded(ownBoxMotion.getMinX(), ownBoxMotion.getMinY()) && object.world.isPosLoaded(ownBoxMotion.getMaxX(), ownBoxMotion.getMaxY())) {
            List<BoundBox> boxes = new ArrayList<>();

            for (int x = Util.floor(ownBoxMotion.getMinX()); x < Util.ceil(ownBoxMotion.getMaxX()); x++) {
                for (int y = Util.floor(ownBoxMotion.getMinY()); y < Util.ceil(ownBoxMotion.getMaxY()); y++) {
                    if (object.world.isPosLoaded(x, y)) {
                        for (TileLayer layer : TileLayer.getAllLayers()) {
                            TileState state = object.world.getState(layer, x, y);
                            List<BoundBox> tileBoxes = state.getTile().getBoundBoxes(object.world, state, x, y, layer, object, ownBox, ownBoxMotion);

                            if (layer.canCollide(object) && object.canCollideWithTile(state, x, y, layer)) {
                                object.onTileCollision(x, y, layer, state, ownBox, ownBoxMotion, tileBoxes);
                                boxes.addAll(tileBoxes);
                            }

                            object.onTileIntersection(x, y, layer, state, ownBox, ownBoxMotion, tileBoxes);
                        }
                    }
                }
            }

            List<Entity> entities = object.world.getEntities(ownBoxMotion, e -> e != object);
            for (Entity entity : entities) {
                BoundBox entityBox = entity.currentBounds;
                BoundBox entityBoxMotion = entityBox.copy().add(entity.motionX, entity.motionY);

                if (entity.canCollideWith(object, ownBox, ownBoxMotion)) {
                    object.onEntityCollision(entity, ownBox, ownBoxMotion, entityBox, entityBoxMotion);
                    boxes.add(entityBox);
                }

                object.onEntityIntersection(entity, ownBox, ownBoxMotion, entityBox, entityBoxMotion);
            }

            RockBottomAPI.getEventHandler().fireEvent(new WorldObjectCollisionEvent(object, ownBox, ownBoxMotion, boxes));

            motionX = object.motionX;
            motionY = object.motionY;

            if (motionY != 0) {
                if (!boxes.isEmpty()) {
                    for (BoundBox box : boxes) {
                        if (motionY != 0) {
                            if (!box.isEmpty()) {
                                if (ownBox.getMaxX() > box.getMinX() && ownBox.getMinX() < box.getMaxX()) {
                                    if (motionY > 0 && ownBox.getMaxY() <= box.getMinY()) {
                                        double diff = box.getMinY() - ownBox.getMaxY();
                                        if (diff < motionY) {
                                            motionY = diff;
                                        }
                                    } else if (motionY < 0 && ownBox.getMinY() >= box.getMaxY()) {
                                        double diff = box.getMaxY() - ownBox.getMinY();
                                        if (diff > motionY) {
                                            motionY = diff;
                                        }
                                    }
                                }
                            }
                        } else {
                            break;
                        }
                    }
                }

                ownBox.add(0D, motionY);
            }

            if (motionX != 0) {
                if (!boxes.isEmpty()) {
                    for (BoundBox box : boxes) {
                        if (motionX != 0) {
                            if (!box.isEmpty()) {
                                if (ownBox.getMaxY() > box.getMinY() && ownBox.getMinY() < box.getMaxY()) {
                                    if (motionX > 0 && ownBox.getMaxX() <= box.getMinX()) {
                                        double diff = box.getMinX() - ownBox.getMaxX();
                                        if (diff < motionX) {
                                            motionX = diff;
                                        }
                                    } else if (motionX < 0 && ownBox.getMinX() >= box.getMaxX()) {
                                        double diff = box.getMaxX() - ownBox.getMinX();
                                        if (diff > motionX) {
                                            motionX = diff;
                                        }
                                    }
                                }
                            }
                        } else {
                            break;
                        }
                    }
                }

                ownBox.add(motionX, 0D);
            }
        }

        if (object.world.isClient() && object instanceof Entity) {
            Entity entity = (Entity) object;
            if (entity.doesInterpolate() && !entity.world.isLocalPlayer(entity)) {
                double diff = 1F / entity.getSyncFrequency();
                double distX = entity.interpolationX - entity.getOriginX();
                double distY = entity.interpolationY - entity.getOriginY();
                entity.currentBounds.add(distX * diff, distY * diff);
            }
        }

        object.collidedHor = motionX != object.motionX;
        object.collidedVert = motionY != object.motionY;
        object.onGround = object.collidedVert && object.motionY < 0;
    }

    @Override
    public boolean doDefaultSlotMovement(IGameInstance game, int button, float x, float y, GuiContainer gui, ComponentSlot slot) {
        boolean isSecond = Settings.KEY_GUI_ACTION_2.isKey(button);
        if (isSecond || Settings.KEY_GUI_ACTION_1.isKey(button)) {
            ItemContainer container = gui.getContainer();
            return setOrPickUpHolding(gui.player, container, container.getIdForSlot(slot.slot), isSecond);
        }
        return false;
    }

    @Override
    public boolean doDefaultShiftClicking(IGameInstance game, int button, GuiContainer gui, ComponentSlot slot) {
        if (Settings.KEY_GUI_ACTION_1.isKey(button)) {
            ItemContainer container = gui.getContainer();
            ItemInstance remaining = slot.slot.get();

            if (remaining != null) {
                boolean modified = false;
                for (GuiContainer.ShiftClickBehavior behavior : gui.shiftClickBehaviors) {
                    if (behavior.slots.contains(slot.componentId)) {
                        for (int i = 0; i < 2; i++) {
                            for (int slotInto : behavior.slotsInto) {
                                GuiComponent comp = gui.getComponents().get(slotInto);
                                if (comp instanceof ComponentSlot) {
                                    ComponentSlot intoSlot = (ComponentSlot) comp;
                                    if (i == 1 || (intoSlot.slot.get() != null && intoSlot.slot.get().isEffectivelyEqual(remaining))) {
                                        if (behavior.condition == null || behavior.condition.apply(slot.slot, intoSlot.slot)) {
                                            int result = shiftClick(gui.player, container, container.getIdForSlot(slot.slot), container.getIdForSlot(intoSlot.slot));

                                            if (result == 1) {
                                                return true;
                                            } else if (result == 2) {
                                                modified = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return modified;
            }
        }
        return false;
    }

    @Override
    public boolean placeTile(int x, int y, TileLayer layer, AbstractEntityPlayer player, ItemInstance selected, Tile tile, boolean removeItem, boolean simulate) {
        List<BoundBox> tileBounds = tile.getBoundBoxes(player.world, tile.getPlacementState(player.world, x, y, layer, selected, player), x, y, layer, player, player.currentBounds.copy(), player.currentBounds.copy().add(player.motionX, player.motionY));
        if (layer != TileLayer.MAIN || player.world.getEntities(tileBounds, entity -> !(entity instanceof AbstractEntityItem)).isEmpty()) {
            if (layer.canTileBeInLayer(player.world, x, y, tile)) {
                Tile tileThere = player.world.getState(layer, x, y).getTile();
                if (tileThere != tile && tileThere.canReplace(player.world, x, y, layer)) {
                    if (InteractionManager.defaultTilePlacementCheck(player.world, x, y, layer, tile) && tile.canPlace(player.world, x, y, layer, player)) {
                        if (!simulate) {
                            PlaceTileEvent event = new PlaceTileEvent(player, selected, removeItem, layer, x, y);
                            if (RockBottomAPI.getEventHandler().fireEvent(event) != EventResult.CANCELLED) {
                                selected = event.instance;
                                layer = event.layer;
                                removeItem = event.removeItem;
                                x = event.x;
                                y = event.y;

                                tile.doPlace(player.world, x, y, layer, selected, player);

                                if (!player.world.isClient()) {
                                    Item item = tile.getItem();
                                    if (item != null) {
                                        player.getStatistics().getOrInit(StatisticList.TILES_PLACED, ItemStatistic.class).update(item);
                                    }

                                    if (removeItem) {
                                        player.getInv().set(player.getSelectedSlot(), selected.removeAmount(1).nullIfEmpty());
                                    }

                                    ResourceName sound = tile.getPlaceSound(player.world, x, y, layer, player, player.world.getState(layer, x, y));
                                    if (sound != null) {
                                        player.world.playSound(sound, x + 0.5, y + 0.5, layer.index(), 1F, 1F);
                                    }
                                }
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<PlayerCompendiumRecipe> getRecipesToLearnFrom(Tile tile) {
        List<PlayerCompendiumRecipe> recipes = CriteriaBreakTile.getRecipesFor(tile);
        return recipes == null ? null : Collections.unmodifiableList(recipes);
    }

    @Override
    public void doDefaultLiquidBehavior(IWorld world, int x, int y, TileLayer layer, TileLiquid tile) {
        TileState ourState = world.getState(layer, x, y);
        int ourLevel = ourState.get(tile.level) + 1;
        int startLevel = ourLevel;

        if (!world.isPosLoaded(x, y - 1)) {
            return;
        }
        // TODO Spread into chiseled
        // Check down
        if (world.getState(x, y - 1).getTile().canLiquidSpread(world, x, y - 1, tile, Direction.UP) && tile.canLiquidSpread(world, x, y, tile, Direction.DOWN)) {
            TileState beneathState = world.getState(layer, x, y - 1);
            if (beneathState.getTile() == tile) {
                // Liquid beneath us
                int otherLevel = beneathState.get(tile.level) + 1;
                int remaining = ourLevel - tile.getLevels() + otherLevel;
                if (remaining < tile.getLevels() && tile.getLevels() - otherLevel != 0) { // If more liquid can fit beneath us
                    if (remaining > 0) {
                        // Transfer one unit to the liquid beneath us
                        world.setState(layer, x, y, ourState.prop(tile.level, remaining - 1));
                        world.setState(layer, x, y - 1, beneathState.prop(tile.level, tile.getLevels() - 1));
                        return;
                    } else {
                        // Transfer our last unit to the liquid beneath us and remove this liquid
                        world.setState(layer, x, y, GameContent.TILE_AIR.getDefState());
                        world.setState(layer, x, y - 1, beneathState.prop(tile.level, otherLevel + ourLevel - 1));
                        return;
                    }
                } else {
                    if (!world.isPosLoaded(x + 1, y - 1) || !world.isPosLoaded(x - 1, y - 1)) {
                        return;
                    }
                    TileState leftState = world.getState(layer, x - 1, y - 1);
                    TileState rightState = world.getState(layer, x + 1, y - 1);
                    // Try to balance to the sides
                    if (Util.RANDOM.nextBoolean()) {
                        if (this.transferDown(world, layer, ourState, leftState, x, y, -1, tile)) {
                            return;
                        } else {
                            ourState = world.getState(layer, x, y);
                            if (this.transferDown(world, layer, ourState, rightState, x, y, 1, tile))
                                return;
                        }
                        ourState = world.getState(layer, x, y);
                        ourLevel = ourState.get(tile.level) + 1;
                        if (ourLevel != startLevel) return;
                    } else {
                        if (this.transferDown(world, layer, ourState, rightState, x, y, 1, tile)) {
                            return;
                        } else {
                            ourState = world.getState(layer, x, y);
                            if (this.transferDown(world, layer, ourState, leftState, x, y, -1, tile))
                                return;

                        }
                        ourState = world.getState(layer, x, y);
                        ourLevel = ourState.get(tile.level) + 1;
                        if (ourLevel != startLevel) return;
                    }
                }
            } else if (beneathState.getTile().isAir()) {
                // Nothing beneath us move down
                world.setState(layer, x, y - 1, ourState);
                world.setState(layer, x, y, GameContent.TILE_AIR.getDefState());
                return;
            }
            // Fall through to the balancing and spreading logic
        }

        // Balance and spread
        boolean leftDone = false;
        boolean rightDone = false;
        int i = 1;
        if (Util.RANDOM.nextBoolean()) {
            while ((!leftDone || !rightDone) && ourLevel == startLevel) {
                // Left first
                if (!leftDone) {
                    if (!world.isPosLoaded(x - i, y)) return;

                    TileState leftState = world.getState(layer, x - i, y);
                    leftDone = this.balanceAndSpread(world, layer, leftState, ourState, ourLevel, x, y, -i, tile);
                    ourState = world.getState(layer, x, y);
                    if (ourState.getTile() != tile) return;
                    ourLevel = ourState.get(tile.level) + 1;
                }

                // Right second
                if (!rightDone && ourLevel == startLevel) {
                    if (!world.isPosLoaded(x + i, y)) return;

                    TileState rightState = world.getState(layer, x + i, y);
                    rightDone = this.balanceAndSpread(world, layer, rightState, ourState, ourLevel, x, y, i, tile);
                    ourState = world.getState(layer, x, y);
                    if (ourState.getTile() != tile) return;
                    ourLevel = ourState.get(tile.level) + 1;
                }

                i++;
            }
        } else {
            while ((!rightDone || !leftDone) && ourLevel == startLevel) {
                // Right first
                if (!rightDone) {
                    if (!world.isPosLoaded(x + i, y)) return;

                    TileState rightState = world.getState(layer, x + i, y);
                    rightDone = this.balanceAndSpread(world, layer, rightState, ourState, ourLevel, x, y, i, tile);
                    ourState = world.getState(layer, x, y);
                    if (ourState.getTile() != tile) return;
                    ourLevel = ourState.get(tile.level) + 1;
                }

                // Left second
                if (!leftDone && ourLevel == startLevel) {
                    if (!world.isPosLoaded(x - i, y)) return;

                    TileState leftState = world.getState(layer, x - i, y);
                    leftDone = this.balanceAndSpread(world, layer, leftState, ourState, ourLevel, x, y, -i, tile);
                    ourState = world.getState(layer, x, y);
                    if (ourState.getTile() != tile) return;
                    ourLevel = ourState.get(tile.level) + 1;
                }

                i++;
            }
        }

    }

    private boolean balanceAndSpread(IWorld world, TileLayer layer, TileState otherState, TileState ourState, int ourLevel, int x, int y, int direction, TileLiquid tile) {
        Direction flowDirection = Direction.getHorizontal(direction);
        if (world.getState(x + direction, y).getTile().canLiquidSpread(world, x + direction, y, tile, flowDirection.getOpposite()) && ourState.getTile().canLiquidSpread(world, x + direction - 1, y, tile, flowDirection)) {
            if (otherState.getTile() == tile) {
                // Balance in direction
                int otherLevel = otherState.get(tile.level) + 1;
                if (otherLevel > ourLevel) {
                    if (otherLevel - ourLevel > 1) {
                        this.transfer(world, layer, ourLevel, otherState, ourState, x + direction, x, y, tile);
                        return true;
                    } else {
                        world.scheduleUpdate(x + direction, y, layer, tile.getFlowSpeed());
                        return true; // this tile will balance itself later
                    }
                } else if (otherLevel - ourLevel < -1) {
                    this.transfer(world, layer, otherLevel, ourState, otherState, x, x + direction, y, tile);
                    return true;
                } else {
                    return ourLevel == 1 && world.getState(layer, x + direction, y - 1).getTile() != tile;
                }
            } else {
                TileState belowState = world.getState(layer, x + direction, y - 1);
                if (belowState.getTile() == tile) {
                    if (belowState.get(tile.level) + 1 < tile.getLevels()) {
                        world.setState(layer, x + direction, y, ourState);
                        world.setState(layer, x, y, GameContent.TILE_AIR.getDefState());
                        return true;
                    } else if (ourLevel > 1) {
                        // Spread
                        this.spread(world, layer, ourLevel, ourState, x, y, direction, tile);
                        return true;
                    } else {
                        return false;
                    }
                } else if (ourLevel > 1) {
                    // Spread
                    this.spread(world, layer, ourLevel, ourState, x, y, direction, tile);
                    return true;
                } else {
                    return true;
                }
            }
        } else {
            return true;
        }
    }

    private void spread(IWorld world, TileLayer layer, int ourLevel, TileState ourState, int x, int y, int direction, TileLiquid tile) {
        world.setState(layer, x + direction, y, tile.getDefState()); // Place one unit
        world.setState(layer, x, y, ourState.prop(tile.level, ourLevel - 2)); // Decrease our level
    }

    private void transfer(IWorld world, TileLayer layer, int secondLevel, TileState firstState, TileState secondState, int x1, int x2, int y, TileLiquid tile) {
        world.setState(layer, x1, y, firstState.prop(tile.level, firstState.get(tile.level) - 1)); // Decrease first by one
        world.setState(layer, x2, y, secondState.prop(tile.level, secondLevel)); // Increase second by one
        world.scheduleUpdate(x2, y, layer, tile.getFlowSpeed());
    }

    private boolean transferDown(IWorld world, TileLayer layer, TileState firstState, TileState secondState, int x, int y, int direction, TileLiquid tile) {
        boolean empty = false;
        if (secondState.getTile() == tile) {
            int secondLevel = secondState.get(tile.level) + 1;
            if (secondLevel < tile.getLevels()) {
                if (firstState.get(tile.level) == 0) {
                    world.setState(layer, x, y, GameContent.TILE_AIR.getDefState());
                    empty = true;
                } else {
                    world.setState(layer, x, y, firstState.prop(tile.level, firstState.get(tile.level) - 1)); // Decrease first by one
                }
                world.setState(layer, x + direction, y - 1, secondState.prop(tile.level, secondLevel)); // Increase second by one
            }
        }
        return empty;
    }

    @Override
    public String getKeyOrMouseName(int key) {
        switch (key) {
            case GLFW.GLFW_MOUSE_BUTTON_1:
                return this.localizeKey("mouse.left");
            case GLFW.GLFW_MOUSE_BUTTON_2:
                return this.localizeKey("mouse.right");
            case GLFW.GLFW_MOUSE_BUTTON_3:
                return this.localizeKey("mouse.3");
            case GLFW.GLFW_MOUSE_BUTTON_4:
                return this.localizeKey("mouse.4");
            case GLFW.GLFW_MOUSE_BUTTON_5:
                return this.localizeKey("mouse.5");
            case GLFW.GLFW_MOUSE_BUTTON_6:
                return this.localizeKey("mouse.6");
            case GLFW.GLFW_MOUSE_BUTTON_7:
                return this.localizeKey("mouse.7");
            case GLFW.GLFW_MOUSE_BUTTON_8:
                return this.localizeKey("mouse.8");

            case GLFW.GLFW_KEY_A:
                return "A";
            case GLFW.GLFW_KEY_B:
                return "B";
            case GLFW.GLFW_KEY_C:
                return "C";
            case GLFW.GLFW_KEY_D:
                return "D";
            case GLFW.GLFW_KEY_E:
                return "E";
            case GLFW.GLFW_KEY_F:
                return "F";
            case GLFW.GLFW_KEY_G:
                return "G";
            case GLFW.GLFW_KEY_H:
                return "H";
            case GLFW.GLFW_KEY_I:
                return "I";
            case GLFW.GLFW_KEY_J:
                return "J";
            case GLFW.GLFW_KEY_K:
                return "K";
            case GLFW.GLFW_KEY_L:
                return "L";
            case GLFW.GLFW_KEY_M:
                return "M";
            case GLFW.GLFW_KEY_N:
                return "N";
            case GLFW.GLFW_KEY_O:
                return "O";
            case GLFW.GLFW_KEY_P:
                return "P";
            case GLFW.GLFW_KEY_Q:
                return "Q";
            case GLFW.GLFW_KEY_R:
                return "R";
            case GLFW.GLFW_KEY_S:
                return "S";
            case GLFW.GLFW_KEY_T:
                return "T";
            case GLFW.GLFW_KEY_U:
                return "U";
            case GLFW.GLFW_KEY_V:
                return "V";
            case GLFW.GLFW_KEY_W:
                return "W";
            case GLFW.GLFW_KEY_X:
                return "X";
            case GLFW.GLFW_KEY_Y:
                return "Y";
            case GLFW.GLFW_KEY_Z:
                return "Z";
            case GLFW.GLFW_KEY_1:
                return "1";
            case GLFW.GLFW_KEY_2:
                return "2";
            case GLFW.GLFW_KEY_3:
                return "3";
            case GLFW.GLFW_KEY_4:
                return "4";
            case GLFW.GLFW_KEY_5:
                return "5";
            case GLFW.GLFW_KEY_6:
                return "6";
            case GLFW.GLFW_KEY_7:
                return "7";
            case GLFW.GLFW_KEY_8:
                return "8";
            case GLFW.GLFW_KEY_9:
                return "9";
            case GLFW.GLFW_KEY_0:
                return "0";
            case GLFW.GLFW_KEY_SPACE:
                return this.localizeKey("space");
            case GLFW.GLFW_KEY_MINUS:
                return this.localizeKey("minus");
            case GLFW.GLFW_KEY_EQUAL:
                return this.localizeKey("equals");
            case GLFW.GLFW_KEY_LEFT_BRACKET:
                return this.localizeKey("left_bracket");
            case GLFW.GLFW_KEY_RIGHT_BRACKET:
                return this.localizeKey("right_bracket");
            case GLFW.GLFW_KEY_BACKSLASH:
                return this.localizeKey("backslash");
            case GLFW.GLFW_KEY_SEMICOLON:
                return this.localizeKey("semicolon");
            case GLFW.GLFW_KEY_APOSTROPHE:
                return this.localizeKey("apostrophe");
            case GLFW.GLFW_KEY_GRAVE_ACCENT:
                return this.localizeKey("grave");
            case GLFW.GLFW_KEY_COMMA:
                return this.localizeKey("comma");
            case GLFW.GLFW_KEY_PERIOD:
                return this.localizeKey("period");
            case GLFW.GLFW_KEY_SLASH:
                return this.localizeKey("slash");
            case GLFW.GLFW_KEY_WORLD_1:
                return this.localizeKey("world_1");
            case GLFW.GLFW_KEY_WORLD_2:
                return this.localizeKey("world_2");

            case GLFW.GLFW_KEY_ESCAPE:
                return this.localizeKey("escape");
            case GLFW.GLFW_KEY_F1:
                return "F1";
            case GLFW.GLFW_KEY_F2:
                return "F2";
            case GLFW.GLFW_KEY_F3:
                return "F3";
            case GLFW.GLFW_KEY_F4:
                return "F4";
            case GLFW.GLFW_KEY_F5:
                return "F5";
            case GLFW.GLFW_KEY_F6:
                return "F6";
            case GLFW.GLFW_KEY_F7:
                return "F7";
            case GLFW.GLFW_KEY_F8:
                return "F8";
            case GLFW.GLFW_KEY_F9:
                return "F9";
            case GLFW.GLFW_KEY_F10:
                return "F10";
            case GLFW.GLFW_KEY_F11:
                return "F11";
            case GLFW.GLFW_KEY_F12:
                return "F12";
            case GLFW.GLFW_KEY_F13:
                return "F13";
            case GLFW.GLFW_KEY_F14:
                return "F14";
            case GLFW.GLFW_KEY_F15:
                return "F15";
            case GLFW.GLFW_KEY_F16:
                return "F16";
            case GLFW.GLFW_KEY_F17:
                return "F17";
            case GLFW.GLFW_KEY_F18:
                return "F18";
            case GLFW.GLFW_KEY_F19:
                return "F19";
            case GLFW.GLFW_KEY_F20:
                return "F20";
            case GLFW.GLFW_KEY_F21:
                return "F21";
            case GLFW.GLFW_KEY_F22:
                return "F22";
            case GLFW.GLFW_KEY_F23:
                return "F23";
            case GLFW.GLFW_KEY_F24:
                return "F24";
            case GLFW.GLFW_KEY_F25:
                return "F25";
            case GLFW.GLFW_KEY_UP:
                return this.localizeKey("up");
            case GLFW.GLFW_KEY_DOWN:
                return this.localizeKey("down");
            case GLFW.GLFW_KEY_LEFT:
                return this.localizeKey("left");
            case GLFW.GLFW_KEY_RIGHT:
                return this.localizeKey("right");
            case GLFW.GLFW_KEY_LEFT_SHIFT:
                return this.localizeKey("left_shift");
            case GLFW.GLFW_KEY_RIGHT_SHIFT:
                return this.localizeKey("right_shift");
            case GLFW.GLFW_KEY_LEFT_CONTROL:
                return this.localizeKey("left_control");
            case GLFW.GLFW_KEY_RIGHT_CONTROL:
                return this.localizeKey("right_control");
            case GLFW.GLFW_KEY_LEFT_ALT:
                return this.localizeKey("left_alt");
            case GLFW.GLFW_KEY_RIGHT_ALT:
                return this.localizeKey("right_alt");
            case GLFW.GLFW_KEY_TAB:
                return this.localizeKey("tab");
            case GLFW.GLFW_KEY_ENTER:
                return this.localizeKey("enter");
            case GLFW.GLFW_KEY_BACKSPACE:
                return this.localizeKey("backspace");
            case GLFW.GLFW_KEY_INSERT:
                return this.localizeKey("insert");
            case GLFW.GLFW_KEY_DELETE:
                return this.localizeKey("delete");
            case GLFW.GLFW_KEY_PAGE_UP:
                return this.localizeKey("page_up");
            case GLFW.GLFW_KEY_PAGE_DOWN:
                return this.localizeKey("page_down");
            case GLFW.GLFW_KEY_HOME:
                return this.localizeKey("home");
            case GLFW.GLFW_KEY_END:
                return this.localizeKey("end");
            case GLFW.GLFW_KEY_KP_0:
                return this.localizeKey("keypad.0");
            case GLFW.GLFW_KEY_KP_1:
                return this.localizeKey("keypad.1");
            case GLFW.GLFW_KEY_KP_2:
                return this.localizeKey("keypad.2");
            case GLFW.GLFW_KEY_KP_3:
                return this.localizeKey("keypad.3");
            case GLFW.GLFW_KEY_KP_4:
                return this.localizeKey("keypad.4");
            case GLFW.GLFW_KEY_KP_5:
                return this.localizeKey("keypad.5");
            case GLFW.GLFW_KEY_KP_6:
                return this.localizeKey("keypad.6");
            case GLFW.GLFW_KEY_KP_7:
                return this.localizeKey("keypad.7");
            case GLFW.GLFW_KEY_KP_8:
                return this.localizeKey("keypad.8");
            case GLFW.GLFW_KEY_KP_9:
                return this.localizeKey("keypad.9");
            case GLFW.GLFW_KEY_KP_DIVIDE:
                return this.localizeKey("keypad.divide");
            case GLFW.GLFW_KEY_KP_MULTIPLY:
                return this.localizeKey("keypad.multiply");
            case GLFW.GLFW_KEY_KP_SUBTRACT:
                return this.localizeKey("keypad.subtract");
            case GLFW.GLFW_KEY_KP_ADD:
                return this.localizeKey("keypad.add");
            case GLFW.GLFW_KEY_KP_DECIMAL:
                return this.localizeKey("keypad.decimal");
            case GLFW.GLFW_KEY_KP_EQUAL:
                return this.localizeKey("keypad.equals");
            case GLFW.GLFW_KEY_KP_ENTER:
                return this.localizeKey("keypad.enter");
            case GLFW.GLFW_KEY_PRINT_SCREEN:
                return this.localizeKey("print");
            case GLFW.GLFW_KEY_NUM_LOCK:
                return this.localizeKey("num_lock");
            case GLFW.GLFW_KEY_CAPS_LOCK:
                return this.localizeKey("caps_lock");
            case GLFW.GLFW_KEY_SCROLL_LOCK:
                return this.localizeKey("scroll_lock");
            case GLFW.GLFW_KEY_PAUSE:
                return this.localizeKey("pause");
            case GLFW.GLFW_KEY_LEFT_SUPER:
                return this.localizeKey("left_super");
            case GLFW.GLFW_KEY_RIGHT_SUPER:
                return this.localizeKey("right_super");
            case GLFW.GLFW_KEY_MENU:
                return this.localizeKey("menu");

            default:
                return this.localizeKey("unknown");
        }
    }

    private String localizeKey(String name) {
        return RockBottomAPI.getGame().getAssetManager().localize(ResourceName.intern("key_name." + name));
    }

    @Override
    public boolean doInputFieldKeyPress(IGameInstance game, int button, ComponentInputField field) {
        if (field.isSelected()) {
            if (button == GLFW.GLFW_KEY_BACKSPACE) {
                if (!field.getText().isEmpty()) {
                    field.setText(field.getText().substring(0, field.getText().length() - 1));
                }
                return true;
            } else if (button == GLFW.GLFW_KEY_ESCAPE) {
                if (field.selectable) {
                    field.setSelected(false);
                    return true;
                }
            } else {
                IInputHandler input = game.getInput();
                if (input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || input.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL)) {
                    if (button == GLFW.GLFW_KEY_V) {
                        try {
                            String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);

                            int space = this.getTextSpace(game, field);
                            if (space < data.length()) {
                                data = data.substring(0, space);
                            }

                            field.setText(field.getText() + data);

                            return true;
                        } catch (Exception ignored) {
                        }
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean doInputFieldCharInput(IGameInstance game, char[] characters, ComponentInputField field) {
        boolean did = false;
        if (field.isSelected()) {
            for (char character : characters) {
                if (character >= 32 && character <= 254) {
                    if (this.getTextSpace(game, field) > 0) {
                        field.setText(field.getText() + character);
                        did = true;
                    }
                }
            }
        }
        return did;
    }

    @Override
    public void doInputFieldRender(IGameInstance game, IAssetManager manager, IRenderer g, int x, int y, ComponentInputField field) {
        if (field.renderBox) {
            g.addFilledRect(x, y, field.getWidth(), field.getHeight(), field.isMouseOverPrioritized(game) ? GuiComponent.getElementColor() : GuiComponent.getUnselectedElementColor());
            g.addEmptyRect(x, y, field.getWidth(), field.getHeight(), GuiComponent.getElementOutlineColor());
        }

        IFont font = manager.getFont();

        String text = field.getDisplayText();
        if (field.isCensored()) {
            char[] chars = new char[text.length()];
            Arrays.fill(chars, '*');
            text = new String(chars);
        }

        String display = text + (field.isSelected() ? ((game.getTotalTicks() / 15) % 2 == 0 ? "|" : " ") : "");
        font.drawCutOffString(x + 3, y + field.getHeight() / 2F - font.getHeight(0.35F) / 2F, display, 0.35F, field.getWidth() - 6, true, false);

        if (field.displaxMaxLength) {
            int space = this.getTextSpace(game, field);
            FormattingCode format = space <= 0 ? FormattingCode.RED : (space <= field.maxLength / 8 ? FormattingCode.ORANGE : (space <= field.maxLength / 4 ? FormattingCode.YELLOW : FormattingCode.NONE));
            font.drawStringFromRight(x + field.getWidth() - 1, y + field.getHeight() - font.getHeight(0.2F), format.toString() + (field.maxLength - space) + '/' + field.maxLength, 0.2F);
        }
    }

    private int getTextSpace(IGameInstance game, ComponentInputField field) {
        return field.maxLength - game.getAssetManager().getFont().removeFormatting(field.getText()).length();
    }

    @Override
    public void doTileStateInit(TileState thisState, ResourceName name, Tile tile, Map<String, Comparable> properties, Table<String, Comparable, TileState> subStates) {
        Registries.TILE_STATE_REGISTRY.register(name, thisState);

        for (TileProp prop : tile.getProps()) {
            String propName = prop.getName();
            for (int i = 0; i < prop.getVariants(); i++) {
                Comparable value = prop.getValue(i);
                if (!properties.get(propName).equals(value)) {
                    Map<String, Comparable> subProps = new TreeMap<>(properties);
                    subProps.put(propName, value);

                    ResourceName subName = generateTileStateName(tile, subProps);
                    if (tile.hasState(subName, subProps)) {
                        TileState state = Registries.TILE_STATE_REGISTRY.get(subName);

                        if (state == null) {
                            state = new TileState(subName, tile, subProps);
                        }

                        subStates.put(propName, value, state);
                    }
                }
            }
        }
    }

    @Override
    public IStateHandler makeStateHandler(Tile tile) {
        return new StateHandler(tile);
    }

    @Override
    public FormattingCode getFormattingCode(String s, int index, Map<Character, FormattingCode> defaults) {
        if (s.length() > index + 1 && s.charAt(index) == '&') {
            char formatChar = s.charAt(index + 1);

            if (formatChar == '(') {
                int closingIndex = s.indexOf(")", index + 2);
                if (closingIndex > index + 2) {
                    String code = s.substring(index + 2, closingIndex);
                    String[] colors = code.split(",");

                    if (colors.length == 3) {
                        try {
                            return new FormattingCode(' ', Colors.rgb(Float.parseFloat(colors[0]), Float.parseFloat(colors[1]), Float.parseFloat(colors[2])), FontProp.NONE, code.length() + 3, "&(" + code + ')');
                        } catch (Exception ignored) {
                        }
                    }
                }
            } else if (formatChar == 'r') {
                return new FormattingCode('r', Colors.rainbow((Util.getTimeMillis() / 10) % 256));
            } else {
                FormattingCode def = defaults.get(formatChar);
                if (def != null) {
                    return def;
                }
            }
        }
        return FormattingCode.NONE;
    }

    @Override
    public AbstractEntityItem makeItem(IWorld world, ItemInstance inst, double x, double y, double motionX, double motionY) {
        return new EntityItem(world, inst);
    }

    @Override
    public List<ComponentStatistic> makeItemStatComponents(IGameInstance game, ItemStatistic.Stat stat, Map<Item, Counter> statMap, AbstractStatGui gui, ComponentMenu menu, ResourceName textureLocation) {
        return Collections.singletonList(new ComponentStatistic(gui, () -> game.getAssetManager().localize(stat.getInitializer().getName().addPrefix("stat.")), () -> String.valueOf(stat.getTotal()), stat.getTotal(), () -> {
            List<ComponentStatistic> list = new ArrayList<>();

            for (Map.Entry<Item, Counter> entry : statMap.entrySet()) {
                Item item = entry.getKey();
                Counter value = entry.getValue();

                ItemInstance instance = new ItemInstance(item);
                String statName = game.getAssetManager().localize(stat.getInitializer().getName().addPrefix("stat.").addSuffix("_per_tile"));

                list.add(new ComponentStatistic(gui, () -> {
                    instance.setMeta((game.getTotalTicks() / Constants.TARGET_TPS) % (item.getHighestPossibleMeta() + 1));
                    return String.format(statName, instance.getDisplayName());
                }, value::toString, value.get(), null) {
                    @Override
                    public void renderStatGraphic(IGameInstance game, IAssetManager manager, IRenderer g, int x, int y) {
                        IItemRenderer renderer = item.getRenderer();
                        if (renderer != null) {
                            renderer.render(game, manager, g, item, instance, x + 1, y + 1, 12F, Colors.WHITE);
                        }
                    }
                });
            }

            game.getGuiManager().openGui(gui.makeSubGui(list));

            return true;
        }) {
            @Override
            public void renderStatGraphic(IGameInstance game, IAssetManager manager, IRenderer g, int x, int y) {
                manager.getTexture(textureLocation).draw(x + 1, y + 1, 12F, 12F);
            }
        });
    }

    @Override
    public Logger logger() {
        return Logging.mainLogger;
    }

    @Override
    public void onToolBroken(IWorld world, AbstractEntityPlayer player, ItemInstance instance) {
        if (world.isServer()) {
            RockBottomAPI.getNet().sendToAllPlayersWithLoadedPos(world, new PacketToolBreak(player.getUniqueId(), instance), player.getX(), player.getY());
        }

        if (!world.isDedicatedServer()) {
            RockBottomAPI.getGame().getParticleManager().addItemParticles(world, player.getX(), player.getY(), instance);
        }

        player.getStatistics().getOrInit(StatisticList.TOOLS_BROKEN, NumberStatistic.class).update();
    }

    @Override
    public void dropHeldItem(AbstractEntityPlayer player, ItemContainer container) {
        PacketDrop.dropHeldItem(player, container);
    }

    @Override
    public void packetDamage(IWorld world, double x, double y, UUID entityId, int damage) {
        RockBottomAPI.getNet().sendToAllPlayersWithLoadedPos(world, new PacketDamage(entityId, damage), x, y);
    }

    @Override
    public void packetDeath(IWorld world, double x, double y, UUID entityId) {
        RockBottomAPI.getNet().sendToAllPlayersWithLoadedPos(world, new PacketDeath(entityId), x, y);
    }

    @Override
    public void packetTileEntityData(TileEntity tile) {
        RockBottomAPI.getNet().sendToAllPlayersWithLoadedPos(tile.world, new PacketTileEntityData(tile.x, tile.y, tile.layer, tile), tile.x, tile.y);
    }

    @Override
    public void packetEntityData(Entity entity) {
        RockBottomAPI.getNet().sendToAllPlayersWithLoadedPos(entity.world, new PacketEntityChange(entity, false), entity.getX(), entity.getY());
    }

	@Override
	public void smithingConstruct(AbstractEntityPlayer player, TileEntity tile, SmithingRecipe recipe, List<ItemInstance> actualInputs) {
		GuiSmithing gui = new GuiSmithing(player, tile, recipe, actualInputs);
		player.openGui(gui);
	}
}
