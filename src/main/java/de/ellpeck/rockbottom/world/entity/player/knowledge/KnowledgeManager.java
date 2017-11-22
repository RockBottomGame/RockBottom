package de.ellpeck.rockbottom.world.entity.player.knowledge;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.knowledge.IKnowledgeManager;
import de.ellpeck.rockbottom.api.entity.player.knowledge.Information;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.toast.Toast;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.net.packet.toclient.PacketKnowledge;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class KnowledgeManager implements IKnowledgeManager{

    private final Map<IResourceName, Information> information = new HashMap<>();
    private final EntityPlayer player;

    public KnowledgeManager(EntityPlayer player){
        this.player = player;
    }

    public void save(DataSet set){
        int counter = 0;
        for(Information information : this.information.values()){
            DataSet sub = new DataSet();
            saveInformation(sub, this, information);

            if(!sub.isEmpty()){
                set.addDataSet("info_"+counter, sub);
                counter++;
            }
        }
        set.addInt("info_amount", counter);
    }

    public void load(DataSet set){
        this.information.clear();

        int amount = set.getInt("info_amount");
        for(int i = 0; i < amount; i++){
            DataSet sub = set.getDataSet("info_"+i);

            Information information = loadInformation(sub, this);
            if(information != null){
                this.information.put(information.getName(), information);
            }
        }
    }

    public static void saveInformation(DataSet set, IKnowledgeManager manager, Information information){
        information.save(set, manager);

        if(!set.isEmpty()){
            set.addString("reg_name", information.getRegistryName().toString());
            set.addString("name", information.getName().toString());
        }
    }

    public static Information loadInformation(DataSet set, IKnowledgeManager manager){
        IResourceName regName = RockBottomAPI.createRes(set.getString("reg_name"));
        IResourceName name = RockBottomAPI.createRes(set.getString("name"));

        Information information = loadInformation(regName, name);
        if(information != null){
            information.load(set, manager);
        }
        else{
            RockBottomAPI.logger().warning("Couldn't load information with registry name "+regName+" and name "+name);
        }
        return information;
    }

    private static Information loadInformation(IResourceName regName, IResourceName name){
        Class<? extends Information> infoClass = RockBottomAPI.INFORMATION_REGISTRY.get(regName);

        try{
            return infoClass.getConstructor(IResourceName.class).newInstance(name);
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't initialize information with registry name "+regName+" and name "+name, e);
            return null;
        }
    }

    @Override
    public boolean knowsRecipe(IRecipe recipe){
        return this.knowsInformation(RecipeInformation.getInfoName(recipe));
    }

    @Override
    public boolean knowsIngredient(IRecipe recipe, IUseInfo info){
        RecipeInformation information = this.getInformation(RecipeInformation.getInfoName(recipe), RecipeInformation.class);
        return information != null && information.knownInputs.contains(info);
    }

    @Override
    public boolean knowsOutput(IRecipe recipe, ItemInstance instance){
        RecipeInformation information = this.getInformation(RecipeInformation.getInfoName(recipe), RecipeInformation.class);
        return information != null && information.knownOutputs.contains(instance);
    }

    @Override
    public boolean knowsInformation(IResourceName name){
        return this.information.containsKey(name);
    }

    @Override
    public Information getInformation(IResourceName name){
        return this.information.get(name);
    }

    @Override
    public <T extends Information> T getInformation(IResourceName name, Class<T> infoClass){
        Information info = this.getInformation(name);
        if(info != null && infoClass.isAssignableFrom(info.getClass())){
            return (T)info;
        }
        else{
            return null;
        }
    }

    @Override
    public void teachRecipe(IRecipe recipe, boolean teachAllParts, boolean announce){
        if(!this.knowsRecipe(recipe)){
            RecipeInformation information = new RecipeInformation(recipe);
            if(teachAllParts){
                information.knownInputs.addAll(recipe.getInputs());
                information.knownOutputs.addAll(recipe.getOutputs());
            }
            this.teachInformation(information, announce);
        }
    }

    @Override
    public void teachRecipe(IRecipe recipe, boolean teachAllParts){
        this.teachRecipe(recipe, teachAllParts, true);
    }

    @Override
    public void teachIngredient(IRecipe recipe, IUseInfo info, boolean announce){
        RecipeInformation information = this.getInformation(RecipeInformation.getInfoName(recipe), RecipeInformation.class);

        if(information == null){
            information = new RecipeInformation(recipe);
            this.teachInformation(information, announce);
        }

        information.knownInputs.add(info);
    }

    @Override
    public void teachIngredient(IRecipe recipe, IUseInfo info){
        this.teachIngredient(recipe, info, true);
    }

    @Override
    public void teachOutput(IRecipe recipe, ItemInstance instance, boolean announce){
        RecipeInformation information = this.getInformation(RecipeInformation.getInfoName(recipe), RecipeInformation.class);

        if(information == null){
            information = new RecipeInformation(recipe);
            this.teachInformation(information, announce);
        }

        information.knownOutputs.add(instance);
    }

    @Override
    public void teachOutput(IRecipe recipe, ItemInstance instance){
        this.teachOutput(recipe, instance, true);
    }

    @Override
    public void teachInformation(Information information, boolean announce){
        if(!this.information.containsKey(information.getName())){
            this.information.put(information.getName(), information);

            if(RockBottomAPI.getNet().isThePlayer(this.player)){
                Toast toast = information.announceTeach();
                if(toast != null){
                    RockBottomAPI.getGame().getToaster().displayToast(toast);
                }
            }
            else if(this.player.world.isServer()){
                this.player.sendPacket(new PacketKnowledge(this.player, information, announce, false));
            }
        }
    }

    @Override
    public void teachInformation(Information information){
        this.teachInformation(information, true);
    }

    @Override
    public void forgetRecipe(IRecipe recipe, boolean forgetAllParts, boolean announce){
        this.forgetInformation(RecipeInformation.getInfoName(recipe), announce);
    }

    @Override
    public void forgetRecipe(IRecipe recipe, boolean forgetAllParts){
        this.forgetRecipe(recipe, forgetAllParts, true);
    }

    @Override
    public void forgetIngredient(IRecipe recipe, IUseInfo info, boolean announce){
        RecipeInformation information = this.getInformation(RecipeInformation.getInfoName(recipe), RecipeInformation.class);
        if(information != null){
            information.knownInputs.remove(info);
        }
    }

    @Override
    public void forgetIngredient(IRecipe recipe, IUseInfo info){
        this.forgetIngredient(recipe, info, true);
    }

    @Override
    public void forgetOutput(IRecipe recipe, ItemInstance instance, boolean announce){
        RecipeInformation information = this.getInformation(RecipeInformation.getInfoName(recipe), RecipeInformation.class);
        if(information != null){
            information.knownOutputs.remove(instance);
        }
    }

    @Override
    public void forgetOutput(IRecipe recipe, ItemInstance instance){
        this.forgetOutput(recipe, instance, true);
    }

    @Override
    public void forgetInformation(IResourceName name, boolean announce){
        Information info = this.information.get(name);
        if(info != null){
            this.information.remove(name);

            if(RockBottomAPI.getNet().isThePlayer(this.player)){
                Toast toast = info.announceForget();
                if(toast != null){
                    RockBottomAPI.getGame().getToaster().displayToast(toast);
                }
            }
            else if(this.player.world.isServer()){
                this.player.sendPacket(new PacketKnowledge(this.player, info, announce, true));
            }
        }
    }

    @Override
    public void forgetInformation(IResourceName name){
        this.forgetInformation(name, true);
    }
}
