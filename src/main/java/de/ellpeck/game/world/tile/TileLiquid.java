package de.ellpeck.game.world.tile;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.item.ItemInstance;
import de.ellpeck.game.render.tile.ITileRenderer;
import de.ellpeck.game.render.tile.LiquidTileRenderer;
import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.world.IWorld;
import de.ellpeck.game.world.TileLayer;
import de.ellpeck.game.world.World;

public class TileLiquid extends TileBasic{

    private static final int MAX_META = 8;

    private final int speed;

    public TileLiquid(int id, String name, int speed){
        super(id, name);
        this.speed = speed;
    }

    @Override
    protected ITileRenderer createRenderer(String name){
        return new LiquidTileRenderer(name);
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer){
        return layer == TileLayer.MAIN;
    }

    @Override
    public int getPlacementMeta(World world, int x, int y, TileLayer layer, ItemInstance instance){
        return MAX_META;
    }

    @Override
    public void onAdded(World world, int x, int y, TileLayer layer){
        world.scheduleUpdate(x, y, TileLayer.MAIN, this.speed);
    }

    @Override
    public void onChangeAround(World world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        if(changedLayer == layer){
            if(changedY <= y){
                world.scheduleUpdate(x, y, TileLayer.MAIN, this.speed);
            }
        }
    }

    @Override
    public void onScheduledUpdate(World world, int x, int y, TileLayer layer){
        if(!tryCombine(world, x, y, x, y-1, this, Integer.MAX_VALUE)){
            trySpread(world, x, y, this);
        }
    }

    public static void trySpread(World world, int originX, int originY, TileLiquid liquid){
        int meta = world.getMeta(originX, originY);
        if(meta >= 3){
            int metaThird = meta/3;

            if(canLiquidCover(world, originX+1, originY, liquid)){
                tryCombine(world, originX, originY, originX+1, originY, liquid, metaThird);
            }

            if(canLiquidCover(world, originX-1, originY, liquid)){
                tryCombine(world, originX, originY, originX-1, originY, liquid, metaThird);
            }
        }
    }

    public static boolean tryCombine(World world, int originX, int originY, int destX, int destY, TileLiquid liquid, int max){
        if(canLiquidCover(world, destX, destY, liquid)){
            int originMeta = world.getMeta(originX, originY);
            if(originMeta > 0){
                int destMeta = world.getMeta(destX, destY);
                boolean isDestLiquid = world.getTile(destX, destY) == liquid;

                if(destY < originY || destMeta == 0 || !isDestLiquid || originMeta-destMeta >= 2){
                    int possible = MAX_META-destMeta;
                    if(possible > 0){
                        if(!isDestLiquid){
                            world.setTile(destX, destY, liquid);
                        }

                        int toAdd = Math.min(possible, Math.min(originMeta, max));
                        world.setMeta(destX, destY, destMeta+toAdd);

                        int left = originMeta-toAdd;
                        if(left > 0){
                            world.setMeta(originX, originY, left);
                        }
                        else{
                            world.setTile(originX, originY, ContentRegistry.TILE_AIR);
                        }

                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean canLiquidCover(World world, int x, int y, TileLiquid liquid){
        Tile tile = world.getTile(x, y);
        return tile.isAir() || tile.canReplace(world, x, y, TileLayer.MAIN, liquid);
    }

    @Override
    public boolean canReplace(World world, int x, int y, TileLayer layer, Tile replacementTile){
        return true;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    public boolean canBreak(World world, int x, int y, TileLayer layer){
        return false;
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return null;
    }

    @Override
    public float getTranslucentModifier(World world, int x, int y, TileLayer layer){
        return 0.95F;
    }
}
