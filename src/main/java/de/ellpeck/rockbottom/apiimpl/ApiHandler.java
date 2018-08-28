package de.ellpeck.rockbottom.apiimpl;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IApiHandler;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.data.set.AbstractDataSet;
import de.ellpeck.rockbottom.api.data.set.part.DataPart;
import de.ellpeck.rockbottom.api.data.set.part.IPartFactory;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.ConstructEvent;
import de.ellpeck.rockbottom.api.inventory.Inventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.log.Logging;
import de.ellpeck.rockbottom.render.WorldRenderer;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ApiHandler implements IApiHandler {

    private final List<IPartFactory> sortedPartFactories = new ArrayList<>();

    @Override
    public void writeDataSet(AbstractDataSet set, File file, boolean asJson) {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            if (asJson) {
                JsonObject object = new JsonObject();
                this.writeDataSet(object, set);

                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8);
                Util.GSON.toJson(object, writer);
                writer.close();
            } else {
                DataOutputStream stream = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
                this.writeDataSet(stream, set);
                stream.close();
            }
        } catch (Exception e) {
            RockBottomAPI.logger().log(Level.SEVERE, "Exception saving a data set to disk!", e);
        }
    }

    @Override
    public void readDataSet(AbstractDataSet set, File file, boolean asJson) {
        if (!set.isEmpty()) {
            set.clear();
        }

        try {
            if (file.exists()) {
                if (asJson) {
                    InputStreamReader reader = new InputStreamReader(new FileInputStream(file), Charsets.UTF_8);
                    JsonObject object = Util.JSON_PARSER.parse(reader).getAsJsonObject();
                    reader.close();

                    this.readDataSet(object, set);
                } else {
                    DataInputStream stream = new DataInputStream(new GZIPInputStream(new FileInputStream(file)));
                    this.readDataSet(stream, set);
                    stream.close();
                }
            }
        } catch (Exception e) {
            RockBottomAPI.logger().log(Level.SEVERE, "Exception loading a data set from disk!", e);
        }
    }

    @Override
    public void writeDataSet(DataOutput stream, AbstractDataSet set) throws Exception {
        stream.writeInt(set.size());

        for (Map.Entry<String, DataPart> entry : set) {
            DataPart part = entry.getValue();
            stream.writeByte(Registries.PART_REGISTRY.getId(part.getFactory()));
            stream.writeUTF(entry.getKey());
            part.write(stream);
        }
    }

    @Override
    public void readDataSet(DataInput stream, AbstractDataSet set) throws Exception {
        int amount = stream.readInt();

        for (int i = 0; i < amount; i++) {
            int id = stream.readByte();
            String name = stream.readUTF();
            IPartFactory factory = Registries.PART_REGISTRY.get(id);
            set.addPart(name, factory.parse(stream));
        }
    }

    @Override
    public void writeDataSet(JsonObject main, AbstractDataSet set) throws Exception {
        for (Map.Entry<String, DataPart> entry : set) {
            main.add(entry.getKey(), entry.getValue().write());
        }
    }

    @Override
    public void readDataSet(JsonObject main, AbstractDataSet set) throws Exception {
        for (Map.Entry<String, JsonElement> entry : main.entrySet()) {
            DataPart part = this.readDataPart(entry.getValue());
            set.addPart(entry.getKey(), part);
        }
    }

    @Override
    public DataPart readDataPart(JsonElement element) throws Exception {
        if (this.sortedPartFactories.isEmpty()) {
            this.sortedPartFactories.addAll(Registries.PART_REGISTRY.values());
            this.sortedPartFactories.sort(Comparator.comparingInt((ToIntFunction<IPartFactory>) IPartFactory::getPriority).reversed());
        }

        for (IPartFactory factory : this.sortedPartFactories) {
            DataPart part = factory.parse(element);
            if (part != null) {
                return part;
            }
        }
        return null;
    }

    @Override
    public int[] interpolateLight(IWorld world, int x, int y) {
        if (false) {
            int light = Constants.MAX_LIGHT;
            return new int[]{light, light, light, light};
        } else if (!RockBottomAPI.getGame().getSettings().smoothLighting) {
            int light = world.getCombinedVisualLight(x, y);
            return new int[]{light, light, light, light};
        } else {
            Direction[] dirs = Direction.SURROUNDING_INCLUDING_NONE;
            byte[] lightAround = new byte[dirs.length];
            for (int i = 0; i < dirs.length; i++) {
                Direction dir = dirs[i];
                if (world.isPosLoaded(x + dir.x, y + dir.y)) {
                    lightAround[i] = world.getCombinedVisualLight(x + dir.x, y + dir.y);
                }
            }

            int[] light = new int[4];
            light[ITexture.TOP_LEFT] = (lightAround[0] + lightAround[8] + lightAround[1] + lightAround[2]) / 4;
            light[ITexture.TOP_RIGHT] = (lightAround[0] + lightAround[2] + lightAround[3] + lightAround[4]) / 4;
            light[ITexture.BOTTOM_RIGHT] = (lightAround[0] + lightAround[4] + lightAround[5] + lightAround[6]) / 4;
            light[ITexture.BOTTOM_LEFT] = (lightAround[0] + lightAround[6] + lightAround[7] + lightAround[8]) / 4;
            return light;
        }
    }

    @Override
    public int[] interpolateWorldColor(int[] interpolatedLight, TileLayer layer) {
        int[] colors = new int[interpolatedLight.length];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = this.getColorByLight(interpolatedLight[i], layer);
        }
        return colors;
    }

    @Override
    public List<ItemInstance> construct(AbstractEntityPlayer player, Inventory inputInventory, Inventory outputInventory, IRecipe recipe, int amount, List<IUseInfo> inputs, Function<List<ItemInstance>, List<ItemInstance>> outputGetter, float skillReward) {
        List<ItemInstance> remains = new ArrayList<>();

        ConstructEvent event = new ConstructEvent(player, inputInventory, outputInventory, recipe, amount, inputs, outputGetter, skillReward);
        if (RockBottomAPI.getEventHandler().fireEvent(event) != EventResult.CANCELLED) {
            inputInventory = event.inputInventory;
            outputInventory = event.outputInventory;
            recipe = event.recipe;
            amount = event.amount;
            inputs = event.inputs;
            outputGetter = event.outputGetter;
            skillReward = event.skillReward;

            for (int a = 0; a < amount; a++) {
                if (recipe.canConstruct(inputInventory, outputInventory)) {
                    List<ItemInstance> usedInputs = new ArrayList<>();

                    for (IUseInfo input : inputs) {
                        for (int i = 0; i < inputInventory.getSlotAmount(); i++) {
                            ItemInstance inv = inputInventory.get(i);

                            if (inv != null && input.containsItem(inv) && inv.getAmount() >= input.getAmount()) {
                                usedInputs.add(inv.copy().setAmount(input.getAmount()));
                                inputInventory.remove(i, input.getAmount());
                                break;
                            }
                        }
                    }

                    for (ItemInstance output : outputGetter.apply(usedInputs)) {
                        ItemInstance left = outputInventory.addExistingFirst(output, false);
                        if (left != null) {
                            remains.add(left);
                        }
                    }

                    if (player != null) {
                        player.gainSkill(skillReward);
                    }
                } else {
                    break;
                }
            }
        }
        return remains;
    }

    @Override
    public int getColorByLight(int light, TileLayer layer) {
        return Colors.multiply(WorldRenderer.MAIN_COLORS[light], layer.getRenderLightModifier());
    }

    @Override
    public INoiseGen makeSimplexNoise(long seed) {
        return new SimplexNoise(seed);
    }

    @Override
    public Logger createLogger(String name) {
        return Logging.createLogger(name);
    }
}
