package de.ellpeck.rockbottom.apiimpl;

import com.google.common.base.Charsets;
import com.google.common.collect.ListMultimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.*;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.construction.compendium.PlayerCompendiumRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.data.set.AbstractDataSet;
import de.ellpeck.rockbottom.api.data.set.part.DataPart;
import de.ellpeck.rockbottom.api.data.set.part.IPartFactory;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.MovableWorldObject;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.ConstructEvent;
import de.ellpeck.rockbottom.api.gui.container.ItemContainer;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.inventory.Inventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.*;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.BiomeGen;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.biome.Biome;
import de.ellpeck.rockbottom.api.world.gen.biome.level.BiomeLevel;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.gui.GuiExtendedInventory;
import de.ellpeck.rockbottom.gui.GuiInventory;
import de.ellpeck.rockbottom.gui.container.ContainerExtendedInventory;
import de.ellpeck.rockbottom.log.Logging;
import de.ellpeck.rockbottom.net.packet.toserver.PacketConstruction;
import de.ellpeck.rockbottom.render.WorldRenderer;
import de.ellpeck.rockbottom.render.entity.PlayerEntityRenderer;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
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
            light[ITexture.BOTTOM_LEFT] = (lightAround[0] + lightAround[6] + lightAround[7] + lightAround[8]) / 4;
            light[ITexture.BOTTOM_RIGHT] = (lightAround[0] + lightAround[4] + lightAround[5] + lightAround[6]) / 4;
            light[ITexture.TOP_RIGHT] = (lightAround[0] + lightAround[2] + lightAround[3] + lightAround[4]) / 4;
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
    public List<BoundBox> getDefaultPlatformBounds(IWorld world, int x, int y, TileLayer layer, double tileWidth, double tileHeight, TileState state, MovableWorldObject object, BoundBox objectBox) {
        if (object instanceof Entity && ((Entity) object).isDropping) {
            return Collections.emptyList();
        }

        if (objectBox.getMinY() >= y + tileHeight) {
            return Collections.singletonList(new BoundBox((Util.ceil(tileWidth) - tileWidth)/2, tileHeight - 1/12d, (Util.ceil(tileWidth) + tileWidth)/2, tileHeight).add(x, y));
        }

        return Collections.emptyList();
    }

    @Override
	public boolean collectItems(IInventory inventory, List<IUseInfo> inputs, boolean simulate, List<ItemInstance> out) {
    	int[] outSlots = new int[inputs.size()];
		for (int i = 0; i < inputs.size(); i++) {
			IUseInfo input = inputs.get(i);
			for (int slot = 0; slot < inventory.getSlotAmount(); slot++) {
				ItemInstance item = inventory.get(slot);

				if (item != null && input.containsItem(item) && item.getAmount() >= input.getAmount()) {
					out.add(item.copy().setAmount(input.getAmount()));
					outSlots[i] = slot;
					break;
				}
			}
		}
		if (out.size() != inputs.size()) {
			return false;
		}
		if (!simulate) {
			for (int i = 0; i < out.size(); i++) {
				inventory.remove(outSlots[i], out.get(i).getAmount());
			}
		}
		return true;
	}

	@Override
	public void defaultConstruct(AbstractEntityPlayer player, PlayerCompendiumRecipe recipe, TileEntity machine) {
		if (RockBottomAPI.getNet().isClient()) {
			RockBottomAPI.getNet().sendToServer(new PacketConstruction(player.getUniqueId(), Registries.ALL_RECIPES.getId(recipe), machine, 1));
		} else {
			if (recipe.isKnown(player)) {
				recipe.playerConstruct(player, machine, 1);
			}
		}
	}

    @Override
    public List<ItemInstance> construct(AbstractEntityPlayer player, Inventory inputInventory, Inventory outputInventory, PlayerCompendiumRecipe recipe, TileEntity machine, int amount, List<IUseInfo> recipeInputs, List<ItemInstance> actualInputs, Function<List<ItemInstance>, List<ItemInstance>> outputGetter, float skillReward) {
        List<ItemInstance> remains = new ArrayList<>();
        if (actualInputs == null) {
        	actualInputs = new ArrayList<>();
        	this.collectItems(inputInventory, recipeInputs, true, actualInputs);
		}

        ConstructEvent event = new ConstructEvent(player, inputInventory, outputInventory, recipe, machine, amount, recipeInputs, actualInputs, outputGetter, skillReward);
        if (RockBottomAPI.getEventHandler().fireEvent(event) != EventResult.CANCELLED) {
            inputInventory = event.inputInventory;
            outputInventory = event.outputInventory;
            recipe = event.recipe;
            machine = event.machine;
            amount = event.amount;
            recipeInputs = event.recipeInputs;
            actualInputs = event.actualInputs;
            outputGetter = event.outputGetter;
            skillReward = event.skillReward;

            for (int a = 0; a < amount; a++) {
                if (recipe.canConstruct(inputInventory, outputInventory)) {
                    if (recipe.handleRecipe(player, inputInventory, outputInventory, machine, recipeInputs, actualInputs, outputGetter, skillReward)) {
                    	for (ItemInstance input : actualInputs) {
                    		inputInventory.remove(inputInventory.getItemIndex(input), input.getAmount());
						}

                        for (ItemInstance output : outputGetter.apply(actualInputs)) {
                            ItemInstance left = outputInventory.addExistingFirst(output, false);
                            if (left != null) {
                                remains.add(left);
                            }
                        }

                        if (player != null && skillReward > 0F) {
                            player.gainSkill(skillReward);
                        }
                    } else {
                        break;
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

    @Override
    public void renderPlayer(AbstractEntityPlayer player, IGameInstance game, IAssetManager manager, IRenderer g, IPlayerDesign design, float x, float y, float scale, int row, int light) {
        PlayerEntityRenderer.renderPlayer(player, game, manager, g, design, x, y, scale, row, light);
    }

    @Override
    public int generateBasicHeight(IWorld world, TileLayer layer, int x, INoiseGen noiseGen, int minHeight, int maxHeight, int maxMountainHeight) {
        double z = x / 3125D;

        double noise = 0.23D * noiseGen.make2dNoise(2D * z, 0D);
        noise += 0.17D * noiseGen.make2dNoise(4D * z, 0D);
        noise += 1D * noiseGen.make2dNoise(16D * z, 0D);
        noise /= 1.4D;
        noise = 1.8D * noise * noise * noise * noise * noise * noise;

        int height = (int) (noise * maxMountainHeight);
        noise = noiseGen.make2dNoise(x / 100D, 0D);
        noise += noiseGen.make2dNoise(x / 20D, 0D) * 2D;
        height = Math.min(maxMountainHeight, Math.max(minHeight, height + (int) (noise / 3.5D * (double) (maxHeight - minHeight)) + minHeight));

        if (layer == TileLayer.BACKGROUND) {
            height -= Util.ceil(noiseGen.make2dNoise(x / 10D, 0D) * 3D);
        }

        return height;

    }

    @Override
    public void initBiomeGen(IWorld world, int seedScramble, int blobSize, long[] layerSeeds, ListMultimap<BiomeLevel, Biome> biomesPerLevel, Map<BiomeLevel, Integer> totalWeights, BiomeGen gen) {
        Random rand = new Random(Util.scrambleSeed(seedScramble, world.getSeed()));
        for (int i = 0; i < blobSize; i++) {
            layerSeeds[i] = rand.nextLong();
        }

        for (Biome biome : gen.getBiomesToGen(world)) {
            if (biome.shouldGenerateInWorld(world)) {
                List<BiomeLevel> levels = biome.getGenerationLevels(world);
                for (BiomeLevel level : gen.getLevelsToGen(world)) {
                    if (level.shouldGenerateInWorld(world)) {
                        if (levels.contains(level) || level.getAdditionalGenBiomes(world).contains(biome)) {
                            biomesPerLevel.put(level, biome);
                        }
                    }
                }
            }
        }

        for (BiomeLevel level : biomesPerLevel.keySet()) {
            int total = 0;
            for (Biome biome : biomesPerLevel.get(level)) {
                total += biome.getWeight(world);
            }
            totalWeights.put(level, total);
        }
    }

    @Override
    public void generateBiomeGen(IWorld world, IChunk chunk, BiomeGen gen, Map<Biome, INoiseGen> biomeNoiseGens) {
        for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
            Map<TileLayer, Integer> heights = new HashMap<>();
            for (TileLayer layer : TileLayer.getAllLayers()) {
                heights.put(layer, world.getExpectedSurfaceHeight(layer, chunk.getX() + x));
            }

            for (int y = 0; y < Constants.CHUNK_SIZE; y++) {
                Biome biome = gen.getBiome(world, chunk.getX() + x, chunk.getY() + y, heights.get(TileLayer.MAIN));
                chunk.setBiomeInner(x, y, biome);

                INoiseGen noise = this.getBiomeNoise(world, biome, biomeNoiseGens);
                for (TileLayer layer : TileLayer.getAllLayers()) {
                    chunk.setStateInner(layer, x, y, biome.getState(world, chunk, x, y, layer, noise, heights.get(layer)));
                }
            }
        }
    }

    @Override
    public Biome getBiome(IWorld world, int x, int y, int height, Map<BiomeLevel, Integer> totalWeights, ListMultimap<BiomeLevel, Biome> biomesPerLevel, Random biomeRandom, int blobSize, long[] layerSeeds, INoiseGen levelHeightNoise, int levelTransition, int biomeTransition) {
        BiomeLevel level = this.getSmoothedLevelForPos(world, x, y, height, levelTransition, biomesPerLevel, levelHeightNoise);

        biomeRandom.setSeed(Util.scrambleSeed(x, y, world.getSeed()));
        int addX = biomeRandom.nextInt(biomeTransition) - biomeTransition / 2;

        if (level.isForcedSideBySide()) {
            return this.getBiomeFromWeightPercentage(world, x, y, x + addX, 0, level, height, totalWeights, biomesPerLevel, biomeRandom, blobSize, layerSeeds);
        } else {
            int addY = biomeRandom.nextInt(biomeTransition) - biomeTransition / 2;
            return this.getBiomeFromWeightPercentage(world, x, y, x + addX, y + addY, level, height, totalWeights, biomesPerLevel, biomeRandom, blobSize, layerSeeds);
        }
    }

    private Biome getBiomeFromWeightPercentage(IWorld world, int x, int y, int percentageX, int percentageY, BiomeLevel level, int height, Map<BiomeLevel, Integer> totalWeights, ListMultimap<BiomeLevel, Biome> biomesPerLevel, Random biomeRandom, int blobSize, long[] layerSeeds) {
        int totalWeight = totalWeights.get(level);
        int chosenWeight = Util.ceil(totalWeight * this.getBiomePercentage(world, percentageX, percentageY, blobSize, layerSeeds, biomeRandom));

        Biome chosen = null;

        int weightCounter = 0;
        for (Biome biome : biomesPerLevel.get(level)) {
            weightCounter += biome.getWeight(world);

            if (weightCounter >= chosenWeight) {
                chosen = biome;
                break;
            }
        }

        if (chosen == null) {
            RockBottomAPI.logger().warning("Couldn't find a biome to generate for " + x + ", " + y + " with level " + level.getName());
            chosen = GameContent.BIOME_SKY;
        }

        return chosen.getVariationToGenerate(world, x, y, height, biomeRandom);
    }

    @Override
    public BiomeLevel getSmoothedLevelForPos(IWorld world, int x, int y, int height, int levelTransition, ListMultimap<BiomeLevel, Biome> biomesPerLevel, INoiseGen levelHeightNoise) {
        BiomeLevel level = this.getLevelForPos(world, x, y, height, biomesPerLevel);

        int maxY = level.getMaxY(world, x, y, height);
        if (Math.abs(maxY - y) <= levelTransition) {
            int changeHeight = Util.floor(levelTransition * levelHeightNoise.make2dNoise(x / 10D, maxY));
            if (y >= maxY - changeHeight + Util.ceil(levelTransition / 2D)) {
                return this.getLevelForPos(world, x, maxY + 1, height, biomesPerLevel);
            }
        } else {
            int minY = level.getMinY(world, x, y, height);
            if (Math.abs(minY - y) <= levelTransition) {
                int changeHeight = Util.ceil(levelTransition * (1D - levelHeightNoise.make2dNoise(x / 10D, minY)));
                if (y <= minY + changeHeight - Util.floor(levelTransition / 2D)) {
                    return this.getLevelForPos(world, x, minY - 1, height, biomesPerLevel);
                }
            }
        }
        return level;
    }

    private BiomeLevel getLevelForPos(IWorld world, int x, int y, int height, ListMultimap<BiomeLevel, Biome> biomesPerLevel) {
        BiomeLevel chosen = null;
        for (BiomeLevel level : biomesPerLevel.keySet()) {
            if (y >= level.getMinY(world, x, y, height) && y <= level.getMaxY(world, x, y, height)) {
                if (chosen == null || level.getPriority() >= chosen.getPriority()) {
                    chosen = level;
                }
            }
        }
        return chosen;
    }

    private double getBiomePercentage(IWorld world, int x, int y, int blobSize, long[] layerSeeds, Random biomeRandom) {
        Pos2 blobPos = this.getBlobPos(x, y, world, blobSize, layerSeeds, biomeRandom);
        biomeRandom.setSeed(Util.scrambleSeed(blobPos.getX(), blobPos.getY(), world.getSeed()) + world.getSeed());
        return biomeRandom.nextDouble();
    }

    private Pos2 getBlobPos(int x, int y, IWorld world, int blobSize, long[] layerSeeds, Random biomeRandom) {
        Pos2 offset = new Pos2(x, y);
        for (int i = 0; i < blobSize; i++) {
            offset = this.zoomFromPos(offset, layerSeeds[i], world, biomeRandom);
        }
        return offset;
    }

    private Pos2 zoomFromPos(Pos2 pos, long seed, IWorld world, Random biomeRandom) {
        boolean xEven = (pos.getX() & 1) == 0;
        boolean yEven = (pos.getY() & 1) == 0;

        int halfX = pos.getX() / 2;
        int halfY = pos.getY() / 2;

        if (xEven && yEven) {
            return new Pos2(halfX, halfY);
        } else {
            biomeRandom.setSeed(Util.scrambleSeed(pos.getX(), pos.getY(), world.getSeed()) + seed);
            int offX = biomeRandom.nextBoolean() ? (pos.getX() < 0 ? -1 : 1) : 0;
            int offY = biomeRandom.nextBoolean() ? (pos.getY() < 0 ? -1 : 1) : 0;

            if (xEven) {
                return new Pos2(halfX, halfY + offY);
            } else if (yEven) {
                return new Pos2(halfX + offX, halfY);
            } else {
                return new Pos2(halfX + offX, halfY + offY);
            }
        }
    }

    public INoiseGen getBiomeNoise(IWorld world, Biome biome, Map<Biome, INoiseGen> biomeNoiseGens) {
        return biomeNoiseGens.computeIfAbsent(biome, b -> RockBottomAPI.getApiHandler().makeSimplexNoise(b.getBiomeSeed(world)));
    }

    @Override
    public void openPlayerInventory(AbstractEntityPlayer player) {
        player.openGuiContainer(new GuiInventory(player), player.getInvContainer());
    }

    @Override
    public void openExtendedPlayerInventory(AbstractEntityPlayer player, IInventory inventory, int containerWidth, Consumer<IInventory> onClosed, ItemContainer.ISlotCallback slotCallback) {
        int containerHeight = 1 + inventory.getSlotAmount() / containerWidth;
        player.openGuiContainer(
                new GuiExtendedInventory(player, inventory, containerWidth, containerHeight),
                new ContainerExtendedInventory(player, inventory, containerWidth, containerHeight, onClosed, slotCallback)
        );
    }
}
