package de.ellpeck.rockbottom.world.tile.entity;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;
import de.ellpeck.rockbottom.data.set.DataSet;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.world.World;
import io.netty.buffer.ByteBuf;

public abstract class TileEntityFueled extends TileEntity{

    protected int coalTime;
    protected int maxCoalTime;

    private int lastCoal;
    private boolean lastActive;

    public TileEntityFueled(World world, int x, int y){
        super(world, x, y);
    }

    @Override
    public void update(RockBottom game){
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

            boolean active = this.isActive();
            if(this.lastActive != active){
                this.lastActive = active;

                this.onActiveChange(active);
            }
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
