package de.ellpeck.rockbottom.apiimpl;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IApiHandler;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.entity.AbstractEntityItem;
import de.ellpeck.rockbottom.api.inventory.Inventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.log.Logging;
import de.ellpeck.rockbottom.render.WorldRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

public class ApiHandler implements IApiHandler{

    @Override
    public int[] interpolateLight(IWorld world, int x, int y){
        if(false){
            int light = Constants.MAX_LIGHT;
            return new int[]{light, light, light, light};
        }
        else if(!RockBottomAPI.getGame().getSettings().smoothLighting){
            int light = world.getCombinedVisualLight(x, y);
            return new int[]{light, light, light, light};
        }
        else{
            Direction[] dirs = Direction.SURROUNDING_INCLUDING_NONE;
            byte[] lightAround = new byte[dirs.length];
            for(int i = 0; i < dirs.length; i++){
                Direction dir = dirs[i];
                if(world.isPosLoaded(x+dir.x, y+dir.y)){
                    lightAround[i] = world.getCombinedVisualLight(x+dir.x, y+dir.y);
                }
            }

            int[] light = new int[4];
            light[ITexture.TOP_LEFT] = (lightAround[0]+lightAround[8]+lightAround[1]+lightAround[2])/4;
            light[ITexture.TOP_RIGHT] = (lightAround[0]+lightAround[2]+lightAround[3]+lightAround[4])/4;
            light[ITexture.BOTTOM_RIGHT] = (lightAround[0]+lightAround[4]+lightAround[5]+lightAround[6])/4;
            light[ITexture.BOTTOM_LEFT] = (lightAround[0]+lightAround[6]+lightAround[7]+lightAround[8])/4;
            return light;
        }
    }

    @Override
    public int[] interpolateWorldColor(int[] interpolatedLight, TileLayer layer){
        int[] colors = new int[interpolatedLight.length];
        for(int i = 0; i < colors.length; i++){
            colors[i] = this.getColorByLight(interpolatedLight[i], layer);
        }
        return colors;
    }

    @Override
    public void construct(IWorld world, double x, double y, Inventory inventory, IRecipe recipe, int amount, List<IUseInfo> inputs, Function<List<ItemInstance>, List<ItemInstance>> outputGetter){
        for(int a = 0; a < amount; a++){
            if(recipe.canConstruct(inventory)){
                List<ItemInstance> usedInputs = new ArrayList<>();

                for(IUseInfo input : inputs){
                    for(int i = 0; i < inventory.getSlotAmount(); i++){
                        ItemInstance inv = inventory.get(i);

                        if(inv != null && input.containsItem(inv) && inv.getAmount() >= input.getAmount()){
                            usedInputs.add(inv.copy().setAmount(input.getAmount()));
                            inventory.remove(i, input.getAmount());
                            break;
                        }
                    }
                }

                for(ItemInstance output : outputGetter.apply(usedInputs)){
                    ItemInstance left = inventory.addExistingFirst(output, false);
                    if(left != null){
                        AbstractEntityItem.spawn(world, left, x, y, 0F, 0F);
                    }
                }
            }
            else{
                break;
            }
        }
    }

    @Override
    public int getColorByLight(int light, TileLayer layer){
        return Colors.multiply(WorldRenderer.MAIN_COLORS[light], layer.getRenderLightModifier());
    }

    @Override
    public INoiseGen makeSimplexNoise(long seed){
        return new SimplexNoise(seed);
    }

    @Override
    public Logger createLogger(String name){
        return Logging.createLogger(name);
    }

    @Override
    public Logger logger(){
        return Logging.mainLogger;
    }
}
