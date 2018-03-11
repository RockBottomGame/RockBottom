package de.ellpeck.rockbottom.content;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.content.IContentLoader;
import de.ellpeck.rockbottom.api.content.pack.ContentPack;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileProp;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.gen.IStructure;
import de.ellpeck.rockbottom.world.gen.Structure;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StructureLoader implements IContentLoader<IStructure>{

    private final Set<IResourceName> disabled = new HashSet<>();
    private final Map<String, Map<Character, TileState>> tileCache = new HashMap<>();

    @Override
    public IResourceName getContentIdentifier(){
        return IStructure.ID;
    }

    @Override
    public void loadContent(IGameInstance game, IResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod, ContentPack pack) throws Exception{
        if(!this.disabled.contains(resourceName)){
            JsonArray array = element.getAsJsonArray();
            String structurePath = path+array.get(0).getAsString();
            String tilePath = path+array.get(1).getAsString();

            InputStreamReader tileReader = new InputStreamReader(ContentManager.getResourceAsStream(tilePath), Charsets.UTF_8);
            JsonElement tileElement = Util.JSON_PARSER.parse(tileReader);
            tileReader.close();

            Map<Character, TileState> states = this.tileCache.get(tilePath);
            if(states == null){
                states = new HashMap<>();

                JsonObject tiles = tileElement.getAsJsonObject().get("tiles").getAsJsonObject();
                for(Map.Entry<String, JsonElement> entry : tiles.entrySet()){
                    char key = entry.getKey().charAt(0);

                    JsonObject object = entry.getValue().getAsJsonObject();

                    String tileName = object.get("tile").getAsString();
                    Tile tile = RockBottomAPI.TILE_REGISTRY.get(RockBottomAPI.createRes(tileName));
                    if(tile == null){
                        throw new IllegalArgumentException("Tile with name "+tileName+" doesn't exist!");
                    }

                    TileState state = tile.getDefState();

                    if(object.has("props")){
                        JsonObject props = object.get("props").getAsJsonObject();
                        for(Map.Entry<String, JsonElement> propEntry : props.entrySet()){
                            String propName = propEntry.getKey();
                            String propVal = propEntry.getValue().getAsString();

                            outer:
                            for(TileProp prop : tile.getProps()){
                                if(propName.equals(prop.getName())){
                                    for(int i = 0; i < prop.getVariants(); i++){
                                        Comparable val = prop.getValue(i);
                                        if(val.toString().equals(propVal)){
                                            state = state.prop(prop, val);
                                            break outer;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    states.put(key, state);
                }

                this.tileCache.put(tilePath, states);
            }

            InputStreamReader structureReader = new InputStreamReader(ContentManager.getResourceAsStream(structurePath), Charsets.UTF_8);
            JsonElement structureElement = Util.JSON_PARSER.parse(structureReader);
            structureReader.close();

            JsonArray structure = structureElement.getAsJsonObject().get("structure").getAsJsonArray();
            TileState[][] tileGrid = null;
            int width = -1;

            for(int y = 0; y < structure.size(); y++){
                String row = structure.get(y).getAsString();
                if(width < 0){
                    width = row.length();
                }
                else if(row.length() != width){
                    throw new IllegalArgumentException("Can't create a structure that isn't rectangular!");
                }

                for(int x = 0; x < row.length(); x++){
                    char c = row.charAt(x);

                    if(tileGrid == null){
                        tileGrid = new TileState[width][structure.size()];
                    }

                    tileGrid[x][y] = states.getOrDefault(c, GameContent.TILE_AIR.getDefState());
                }
            }

            if(RockBottomAPI.STRUCTURE_REGISTRY.get(resourceName) != null){
                RockBottomAPI.logger().info("Structure with name "+resourceName+" already exists, not adding structure for mod "+loadingMod.getDisplayName()+" with content pack "+pack.getName());
            }
            else{
                RockBottomAPI.STRUCTURE_REGISTRY.register(resourceName, new Structure(tileGrid, width, structure.size()));

                RockBottomAPI.logger().config("Loaded structure "+resourceName+" for mod "+loadingMod.getDisplayName()+" with content pack "+pack.getName());
            }
        }
        else{
            RockBottomAPI.logger().info("Structure "+resourceName+" will not be loaded for mod "+loadingMod.getDisplayName()+" with content pack "+pack.getName()+" because it was disabled by another content pack!");
        }
    }

    @Override
    public void disableContent(IGameInstance game, IResourceName resourceName){
        this.disabled.add(resourceName);
    }

    @Override
    public void finalize(IGameInstance game){
        this.tileCache.clear();
    }
}
