package de.ellpeck.rockbottom.content;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.construction.compendium.mortar.MortarRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.construction.resource.ItemUseInfo;
import de.ellpeck.rockbottom.api.construction.resource.ResUseInfo;
import de.ellpeck.rockbottom.api.content.IContentLoader;
import de.ellpeck.rockbottom.api.content.pack.ContentPack;
import de.ellpeck.rockbottom.api.data.set.ModBasedDataSet;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MortarRecipeLoader implements IContentLoader<MortarRecipe> {

    private final Set<ResourceName> disabled = new HashSet<>();

    @Override
    public ResourceName getContentIdentifier() {
        return MortarRecipe.ID;
    }

    @Override
    public void loadContent(IGameInstance game, ResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod, ContentPack pack) throws Exception {
        if (!this.disabled.contains(resourceName)) {
            if (MortarRecipe.forName(resourceName) != null) {
                RockBottomAPI.logger().info("Mortar recipe with name " + resourceName + " already exists, not adding recipe for mod " + loadingMod.getDisplayName() + " with content pack " + pack.getName());
            } else {
                String resPath = path + element.getAsString();

                InputStreamReader reader = new InputStreamReader(ContentManager.getResourceAsStream(resPath), Charsets.UTF_8);
                JsonElement recipeElement = Util.JSON_PARSER.parse(reader);
                reader.close();

                JsonObject object = recipeElement.getAsJsonObject();
                int time = object.get("time").getAsInt();

                List<ItemInstance> output = new ArrayList<>();
                for (JsonElement e : object.get("output").getAsJsonArray()) {
                    JsonObject out = e.getAsJsonObject();
                    Item outItem = Registries.ITEM_REGISTRY.get(new ResourceName(out.get("name").getAsString()));
                    int outAmount = out.has("amount") ? out.get("amount").getAsInt() : 1;
                    int outMeta = out.has("meta") ? out.get("meta").getAsInt() : 0;

                    ItemInstance instance = new ItemInstance(outItem, outAmount, outMeta);
                    if (out.has("data")) {
                        ModBasedDataSet set = instance.getOrCreateAdditionalData();
                        RockBottomAPI.getApiHandler().readDataSet(out.get("data").getAsJsonObject(), set);
                    }
                    output.add(instance);
                }

                List<IUseInfo> input = new ArrayList<>();
                for (JsonElement e : object.get("input").getAsJsonArray()) {
                    JsonObject in = e.getAsJsonObject();
                    String name = in.get("name").getAsString();

                    if (Util.isResourceName(name)) {
                        int meta = in.has("meta") ? in.get("meta").getAsInt() : 0;
                        input.add(new ItemUseInfo(Registries.ITEM_REGISTRY.get(new ResourceName(name)), 1, meta));
                    } else {
                        input.add(new ResUseInfo(name));
                    }
                }

                new MortarRecipe(resourceName, input, output, time).register();

                RockBottomAPI.logger().config("Loaded mortar recipe " + resourceName + " for mod " + loadingMod.getDisplayName() + " with time " + time + ", input " + input + " and output " + output + " with content pack " + pack.getName());
            }
        } else {
            RockBottomAPI.logger().info("Mortar recipe " + resourceName + " will not be loaded for mod " + loadingMod.getDisplayName() + " with content pack " + pack.getName() + " because it was disabled by another content pack!");
        }
    }


    @Override
    public void disableContent(IGameInstance game, ResourceName resourceName) {
        this.disabled.add(resourceName);
    }
}
