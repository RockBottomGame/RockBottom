package de.ellpeck.game.world.entity.player;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.gui.GuiManager;
import de.ellpeck.game.inventory.InventoryPlayer;
import de.ellpeck.game.render.entity.IEntityRenderer;
import de.ellpeck.game.render.entity.PlayerEntityRenderer;
import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.util.Direction;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.entity.Entity;
import de.ellpeck.game.world.entity.EntityItem;
import de.ellpeck.game.world.entity.EntityLiving;
import org.newdawn.slick.Graphics;

import java.util.List;
import java.util.UUID;

public class EntityPlayer extends EntityLiving{

    private final BoundBox boundingBox = new BoundBox(-0.5, -0.5, 0.5, 1.5);

    private final IEntityRenderer renderer;
    public final InventoryPlayer inv = new InventoryPlayer();

    private final UUID uniqueId;
    public final GuiManager guiManager = new GuiManager(this);

    public EntityPlayer(World world, UUID uniqueId){
        super(world);
        this.renderer = new PlayerEntityRenderer();
        this.facing = Direction.RIGHT;
        this.uniqueId = uniqueId;
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
                    if(this.inv.add(item.item, true)){
                        this.inv.add(item.item, false);
                        item.setDead();
                    }
                }
            }
        }

        this.guiManager.update(game);
    }

    @Override
    public BoundBox getBoundingBox(){
        return this.boundingBox;
    }

    @Override
    public void save(DataSet set){
        super.save(set);
        this.inv.save(set);
    }

    @Override
    public void load(DataSet set){
        super.load(set);
        this.inv.load(set);
    }

    public UUID getUniqueId(){
        return this.uniqueId;
    }
}
