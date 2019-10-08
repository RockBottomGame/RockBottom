package de.ellpeck.rockbottom.world.entity.player.knowledge;

import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.compendium.PlayerCompendiumRecipe;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.data.set.part.PartDataSet;
import de.ellpeck.rockbottom.api.entity.player.knowledge.IKnowledgeManager;
import de.ellpeck.rockbottom.api.entity.player.knowledge.Information;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.RecipeLearnEvent;
import de.ellpeck.rockbottom.api.toast.IToast;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.net.packet.toclient.PacketKnowledge;
import de.ellpeck.rockbottom.net.packet.toclient.PacketRecipesToast;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class KnowledgeManager implements IKnowledgeManager {

    private final Map<ResourceName, Information> information = new HashMap<>();
    private final EntityPlayer player;

    public KnowledgeManager(EntityPlayer player) {
        this.player = player;
    }

    public static void saveInformation(DataSet set, IKnowledgeManager manager, Information information) {
        information.save(set, manager);

        if (!set.isEmpty()) {
            set.addString("reg_name", information.getRegistryName().toString());
            set.addString("name", information.getName().toString());
        }
    }

    public static Information loadInformation(DataSet set, IKnowledgeManager manager) {
        ResourceName regName = new ResourceName(set.getString("reg_name"));
        ResourceName name = new ResourceName(set.getString("name"));

        Information information = loadInformation(regName, name);
        if (information != null) {
            information.load(set, manager);
        } else {
            RockBottomAPI.logger().warning("Couldn't load information with registry name " + regName + " and name " + name);
        }
        return information;
    }

    private static Information loadInformation(ResourceName regName, ResourceName name) {
        Class<? extends Information> infoClass = Registries.INFORMATION_REGISTRY.get(regName);

        try {
            return infoClass.getConstructor(ResourceName.class).newInstance(name);
        } catch (Exception e) {
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't initialize information with registry name " + regName + " and name " + name, e);
            return null;
        }
    }

    public void save(DataSet set) {
        List<PartDataSet> list = new ArrayList<>();
        for (Information information : this.information.values()) {
            DataSet sub = new DataSet();
            saveInformation(sub, this, information);

            if (!sub.isEmpty()) {
                list.add(new PartDataSet(sub));
            }
        }
        set.addList("knowledge", list);
    }

    public void load(DataSet set) {
        this.information.clear();

        List<PartDataSet> list = set.getList("knowledge");
        for (PartDataSet part : list) {
            Information information = loadInformation(part.get(), this);
            if (information != null) {
                this.information.put(information.getName(), information);
            }
        }
    }

    @Override
    public boolean knowsRecipe(PlayerCompendiumRecipe recipe) {
        return this.knowsInformation(recipe.getKnowledgeInformationName());
    }

    @Override
    public boolean knowsInformation(ResourceName name) {
        return this.information.containsKey(name);
    }

    @Override
    public Information getInformation(ResourceName name) {
        return this.information.get(name);
    }

    @Override
    public <T extends Information> T getInformation(ResourceName name, Class<T> infoClass) {
        Information info = this.getInformation(name);
        if (info != null && infoClass.isAssignableFrom(info.getClass())) {
            return (T) info;
        } else {
            return null;
        }
    }

    @Override
    public boolean teachRecipe(PlayerCompendiumRecipe recipe, boolean announce) {
        if (!recipe.isKnown(this.player)) {
            RecipeLearnEvent event = new RecipeLearnEvent(this.player, recipe, announce);
            if (RockBottomAPI.getEventHandler().fireEvent(event) == EventResult.CANCELLED)
                return false;
            recipe = event.recipe;
            announce = event.announce;
            RecipeInformation information = new RecipeInformation(recipe);
            this.teachInformation(information, announce);
            return true;
        }
        return false;
    }

    @Override
    public void teachRecipes(List<PlayerCompendiumRecipe> recipes) {
        if (recipes == null) return;
        List<PlayerCompendiumRecipe> newRecipes = new ArrayList<>();
        for (PlayerCompendiumRecipe recipe : recipes) {
            if (player.getKnowledge().teachRecipe(recipe, false)) {
                newRecipes.add(recipe);
            }
        }

        PacketRecipesToast packet = new PacketRecipesToast(newRecipes);
        if (player.isLocalPlayer()) {
            packet.handle(RockBottomAPI.getGame(), null);
        } else if (player.world.isServer()) {
            player.sendPacket(packet);
        }
    }

    @Override
    public boolean teachRecipe(PlayerCompendiumRecipe recipe) {
        return this.teachRecipe(recipe, true);
    }

    @Override
    public void teachInformation(Information information, boolean announce) {
        if (!this.information.containsKey(information.getName())) {
            this.information.put(information.getName(), information);

            if (this.player.isLocalPlayer()) {
                if (announce) {
                    IToast toast = information.announceTeach();
                    if (toast != null) {
                        RockBottomAPI.getGame().getToaster().displayToast(toast);
                    }
                }
            } else if (this.player.world.isServer()) {
                this.player.sendPacket(new PacketKnowledge(this.player, information, announce, false));
            }
        }
    }

    @Override
    public void teachInformation(Information information) {
        this.teachInformation(information, true);
    }

    @Override
    public void forgetRecipe(PlayerCompendiumRecipe recipe, boolean announce) {
        this.forgetInformation(recipe.getKnowledgeInformationName(), announce);
    }

    @Override
    public void forgetRecipe(PlayerCompendiumRecipe recipe) {
        this.forgetRecipe(recipe, true);
    }

    @Override
    public void forgetInformation(ResourceName name, boolean announce) {
        Information info = this.information.get(name);
        if (info != null) {
            this.information.remove(name);

            if (this.player.isLocalPlayer()) {
                IToast toast = info.announceForget();
                if (toast != null) {
                    RockBottomAPI.getGame().getToaster().displayToast(toast);
                }
            } else if (this.player.world.isServer()) {
                this.player.sendPacket(new PacketKnowledge(this.player, info, announce, true));
            }
        }
    }

    @Override
    public void forgetInformation(ResourceName name) {
        this.forgetInformation(name, true);
    }
}
