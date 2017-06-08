package de.ellpeck.rockbottom.game.world.tile.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.construction.ConstructionRegistry;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.game.net.NetHandler;
import io.netty.buffer.ByteBuf;

public abstract class TileEntityFueled extends TileEntity{

    protected int coalTime;
    protected int maxCoalTime;

    private int lastCoal;
    private boolean lastActive;

    public TileEntityFueled(IWorld world, int x, int y){
        super(world, x, y);
    }

    @Override
    public void update(IGameInstance game){
        super.update(game);

        if(!NetHandler.isClient()){
            boolean smelted = this.tryTickAction();

            if(this.coalTime > 0){
                this.coalTime--;
            }

            if(smelted){
                if(this.coalTime <= 0){
                    ItemInstance inst = this.getFuel();
                    if(inst != null){
                        int amount = (int)(ConstructionRegistry.getFuelValue(inst)*this.getFuelModifier());
                        if(amount > 0){
                            this.maxCoalTime = amount;
                            this.coalTime = amount;

                            this.removeFuel();
                        }
                    }
                }
            }
        }

        boolean active = this.isActive();
        if(this.lastActive != active){
            this.lastActive = active;

            this.onActiveChange(active);
        }
    }

    @Override
    protected boolean needsSync(){
        return this.lastCoal != this.coalTime;
    }

    @Override
    protected void onSync(){
        this.lastCoal = this.coalTime;
    }

    public boolean isActive(){
        return this.coalTime > 0;
    }

    public float getFuelPercentage(){
        return (float)this.coalTime/(float)this.maxCoalTime;
    }

    protected abstract boolean tryTickAction();

    protected abstract float getFuelModifier();

    protected abstract ItemInstance getFuel();

    protected abstract void removeFuel();

    protected abstract void onActiveChange(boolean active);

    @Override
    public void save(DataSet set){
        set.addInt("coal", this.coalTime);
        set.addInt("max_coal", this.maxCoalTime);
    }

    @Override
    public void load(DataSet set){
        this.coalTime = set.getInt("coal");
        this.maxCoalTime = set.getInt("max_coal");
    }

    @Override
    public void toBuf(ByteBuf buf){
        buf.writeInt(this.coalTime);
        buf.writeInt(this.maxCoalTime);
    }

    @Override
    public void fromBuf(ByteBuf buf){
        this.coalTime = buf.readInt();
        this.maxCoalTime = buf.readInt();
    }
}
