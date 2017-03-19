package de.ellpeck.game.world.entity.player;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.Game;
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

public class EntityPlayer extends EntityLiving{

    private final IEntityRenderer renderer;

    private final BoundBox boundingBox = new BoundBox(-0.5, -0.5, 0.5, 1.5);

    public final InventoryBasic playerInventory = new InventoryBasic(32);

    private Gui gui;

    public EntityPlayer(World world){
        super(world);
        this.renderer = new PlayerEntityRenderer();

        this.facing = Direction.RIGHT;
        this.openGui(null);

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
}
