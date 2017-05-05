package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.util.Pos2;
import de.ellpeck.rockbottom.world.TileLayer;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

public abstract class MultiTile extends TileBasic{

    protected final boolean[][] structure;

    public MultiTile(int id, String name){
        super(id, name);

        this.structure = this.makeStructure();

        if(!this.areDimensionsValid()){
            throw new RuntimeException("MultiTile with id "+id+" and name "+name+" has invalid structure dimensions!");
        }
    }

    protected abstract boolean[][] makeStructure();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int getMainX();

    public abstract int getMainY();

    public boolean isStructurePart(int x, int y){
        return this.structure[y][x];
    }

    @Override
    public boolean canPlace(World world, int x, int y, TileLayer layer){
        if(super.canPlace(world, x, y, layer)){
            Pos2 start = this.getBottomLeft(x, y, world.getMeta(layer, x, y));

            for(int addX = 0; addX < this.getWidth(); addX++){
                for(int addY = 0; addY < this.getHeight(); addY++){
                    int theX = start.getX()+addX;
                    int theY = start.getY()+addY;

                    if(!world.getTile(layer, theX, theY).canReplace(world, theX, theY, layer, this)){
                        return false;
                    }
                }
            }
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void doPlace(World world, int x, int y, TileLayer layer, ItemInstance instance, EntityPlayer placer){
        int startX = x-this.getMainX();
        int startY = y-this.getMainY();

        for(int addX = 0; addX < this.getWidth(); addX++){
            for(int addY = 0; addY < this.getHeight(); addY++){
                if(this.isStructurePart(addX, addY)){
                    int meta = this.getMeta(addX, addY);
                    world.setTile(startX+addX, startY+addY, this, meta);
                }
            }
        }
    }

    @Override
    public void doBreak(World world, int x, int y, TileLayer layer, EntityPlayer breaker, boolean isRightTool){
        Pos2 start = this.getBottomLeft(x, y, world.getMeta(x, y));

        for(int addX = 0; addX < this.getWidth(); addX++){
            for(int addY = 0; addY < this.getHeight(); addY++){
                if(this.isStructurePart(addX, addY)){
                    boolean isMain = addX == this.getMainX() && addY == this.getMainY();
                    world.destroyTile(start.getX()+addX, start.getY()+addY, layer, breaker, isMain && (this.forceDrop || isRightTool));
                }
            }
        }
    }

    public boolean isMainPos(int x, int y, int meta){
        Pos2 main = this.getMainPos(x, y, meta);
        return main.getX() == x && main.getY() == y;
    }

    public Pos2 getInnerCoord(int meta){
        int y = meta/this.getWidth();
        int x = meta-y;

        return new Pos2(x, y);
    }

    public int getMeta(Pos2 coord){
        return this.getMeta(coord.getX(), coord.getY());
    }

    public int getMeta(int x, int y){
        return x*this.getWidth()+y;
    }

    public Pos2 getMainPos(int x, int y, int meta){
        return this.getBottomLeft(x, y, meta).add(this.getMainX(), this.getMainY());
    }

    public Pos2 getBottomLeft(int x, int y, int meta){
        Pos2 inner = this.getInnerCoord(meta);
        return inner.set(x-inner.getX(), y-inner.getY());
    }

    private boolean areDimensionsValid(){
        if(this.structure.length != this.getHeight()){
            return false;
        }
        else{
            for(boolean[] row : this.structure){
                if(row.length != this.getWidth()){
                    return false;
                }
            }
        }
        return this.isStructurePart(this.getMainX(), this.getMainY());
    }
}
