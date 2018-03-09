package de.ellpeck.rockbottom.content;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.BasicRecipe;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.construction.KnowledgeBasedRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.construction.resource.ItemUseInfo;
import de.ellpeck.rockbottom.api.construction.resource.ResUseInfo;
import de.ellpeck.rockbottom.api.content.IContentLoader;
import de.ellpeck.rockbottom.api.content.pack.ContentPack;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipeLoader implements IContentLoader<IRecipe>{

    private final Set<IResourceName> disabled = new HashSet<>();

    @Override
    public IResourceName getContentIdentifier(){
        return IRecipe.ID;
    }

    @Override
    public void loadContent(IGameInstance game, IResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod, ContentPack pack) throws Exception{
        if(!this.disabled.contains(resourceName)){
            String resPath = path+element.getAsString();

            InputStreamReader reader = new InputStreamReader(ContentManager.getResourceAsStream(resPath), Charsets.UTF_8);
            JsonElement recipeElement = Util.JSON_PARSER.parse(reader);
            reader.close();

            JsonObject object = recipeElement.getAsJsonObject();
            String type = object.get("type").getAsString();

            List<IUseInfo> inputList = new ArrayList<>();
            List<ItemInstance> outputList = new ArrayList<>();

            JsonArray outputs = object.get("outputs").getAsJsonArray();
            for(JsonElement output : outputs){
                JsonObject out = output.getAsJsonObject();

                Item item = RockBottomAPI.ITEM_REGISTRY.get(RockBottomAPI.createRes(out.get("name").getAsString()));
                int amount = out.has("amount") ? out.get("amount").getAsInt() : 1;
                int meta = out.has("meta") ? out.get("meta").getAsInt() : 0;

                outputList.add(new ItemInstance(item, amount, meta));
            }

            JsonArray inputs = object.get("inputs").getAsJsonArray();
            for(JsonElement input : inputs){
                JsonObject in = input.getAsJsonObject();

                String name = in.get("name").getAsString();
                int amount = in.has("amount") ? in.get("amount").getAsInt() : 1;

                if(Util.isResourceName(name)){
                    int meta = in.has("meta") ? in.get("meta").getAsInt() : 0;
                    inputList.add(new ItemUseInfo(RockBottomAPI.ITEM_REGISTRY.get(RockBottomAPI.createRes(name)), amount, meta));
                }
                else{
                    inputList.add(new ResUseInfo(name, amount));
                }
            }

            if(IRecipe.forName(resourceName) != null){
                RockBottomAPI.logger().info("Recipe with name "+resourceName+" already exists, not adding recipe for mod "+loadingMod.getDisplayName()+" with content pack "+pack.getName());
            }
            else{
                if("manual".equals(type)){
                    new BasicRecipe(resourceName, inputList, outputList).registerManual();
                }
                else if("manual_knowledge".equals(type)){
                    new KnowledgeBasedRecipe(resourceName, inputList, outputList).registerManual();
                }
                else{
                    throw new IllegalArgumentException("Invalid recipe type "+type+" for recipe "+resourceName);
                }

                RockBottomAPI.logger().config("Loaded recipe "+resourceName+" for mod "+loadingMod.getDisplayName()+" with type "+type+", inputs "+inputList+" and outputs "+outputList);
            }
        }
        else{
            RockBottomAPI.logger().info("Recipe "+resourceName+" will not be loaded for mod "+loadingMod.getDisplayName()+" with content pack "+pack.getName()+" because it was disabled by another content pack!");
        }
    }


    @Override
    public void disableContent(IGameInstance game, IResourceName resourceName){
        this.disabled.add(resourceName);
    }
}
