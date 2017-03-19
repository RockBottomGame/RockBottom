package de.ellpeck.game.world.entity.player;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.Game;
import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.gui.Gui;
import de.ellpeck.game.inventory.InventoryBasic;
import de.ellpeck.game.item.ItemInstance;
import de.ellpeck.game.render.entity.IEntityRenderer;
import de.ellpeck.game.render.entity.PlayerEntityRenderer;
import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.util.Direction;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.entity.Entity;
import de.ellpeck.game.world.entity.EntityItem;
import de.ellpeck.game.world.entity.EntityLiving;

import java.util.List;
import java.util.UUID;

public class EntityPlayer extends EntityLiving{

    private final BoundBox boundingBox = new BoundBox(-0.5, -0.5, 0.5, 1.5);

    private final IEntityRenderer renderer;
    public final InventoryBasic playerInventory = new InventoryBasic(32);
    private Gui gui;

    private UUID uniqueId;

    public EntityPlayer(World world){
        super(world);
        this.renderer = new PlayerEntityRenderer();
        this.facing = Direction.RIGHT;
        this.uniqueId = UUID.randomUUID();

        this.playerInventory.add(new ItemInstance(ContentRegistry.TILE_DIRT, 500), false);
    }

    @Override
    public IEntityRenderer getRenderer(){
        return this.renderer;
    }

    @Override
    public void update(Game game){
        super.update(game);

        List<Entity> entities = this.world.getEntities(this.getBoundingBox().copy().add(this.x, this.y));
        for(Entity entity : entities){
            if(entity instanceof EntityItem){
                EntityItem item = (EntityItem)entity;
                if(item.canPickUp()){
                    if(this.playerInventory.add(item.item, true)){
                        this.playerInventory.add(item.item, false);
                        item.setDead();
                    }
                }
            }
        }

        if(this.gui != null){
            this.gui.update(game);
        }
    }

    @Override
    public BoundBox getBoundingBox(){
        return this.boundingBox;
    }

    public void openGui(Gui gui){
        Game game = Game.get();

        if(this.gui != null){
            this.gui.onClosed(game);
        }

        this.gui = gui;

        if(this.gui != null){
            this.gui.initGui(game);
        }
    }

    public void closeGui(){
        this.openGui(null);
    }

    public Gui getGui(){
        return this.gui;
    }

    @Override
    public void save(DataSet set){
        super.save(set);
        this.playerInventory.save(set);
    }

    @Override
    public void load(DataSet set){
        super.load(set);
        this.playerInventory.load(set);
    }

    public UUID getUniqueId(){
        return this.uniqueId;
    }

    public void setUniqueId(UUID id){
        this.uniqueId = id;
    }
}
