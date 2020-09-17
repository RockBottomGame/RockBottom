package de.ellpeck.rockbottom.net.chat.command;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class CommandSpawnEntity extends Command {

    private final List<String> entityAutocomplete = new ArrayList<>();

    public CommandSpawnEntity() {
        super(ResourceName.intern("spawn_entity"), "Spawns an item into the player's inventory. Params: <mod_id/item_name> [amount] [meta]", 5, "spawn_entity", "spawn");

        for (ResourceName name : Registries.ENTITY_REGISTRY.keySet()) {
            this.entityAutocomplete.add(name.toString());
        }
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat) {
        if (sender instanceof AbstractEntityPlayer) {
            AbstractEntityPlayer player = (AbstractEntityPlayer) sender;

            Entity.IFactory entityFactory;
            if (args.length > 0) {
                try {
                    ResourceName name = new ResourceName(args[0]);
                    entityFactory = Registries.ENTITY_REGISTRY.get(name);
                } catch (Exception e) {
                    return new ChatComponentText(FormattingCode.RED + "'" + args[0] + "' isn't a valid entity name!");
                }
            } else {
                return new ChatComponentText(FormattingCode.RED + "Specify an entity!");
            }

            double x = player.getX();
            double y = player.getY();
            if (args.length > 2) {
                try {
                    x = Double.parseDouble(args[1]);
                    y = Double.parseDouble(args[2]);
                } catch (Exception ignored) {
                }
            }

            if (entityFactory != null) {
                try {
                    Entity entity = entityFactory.create(player.world);
                    entity.setPos(x, y);
                    player.world.addEntity(entity);

                    return new ChatComponentText(FormattingCode.GREEN + "Spawned entity at " + x + ", " + y + '!');
                } catch (Exception e) {
                    RockBottomAPI.logger().log(Level.WARNING, "Trying to spawn entity " + args[0] + " using default constructor failed!", e);
                    return new ChatComponentText(FormattingCode.RED + "The entity '" + args[0] + "' cannot be spawned!");
                }
            } else {
                return new ChatComponentText(FormattingCode.RED + "An entity with the name '" + args[0] + "' doesn't exist!");
            }
        } else {
            return new ChatComponentText(FormattingCode.RED + "Only players can spawn entities!");
        }
    }

    @Override
    public List<String> getAutocompleteSuggestions(String[] args, int argNumber, ICommandSender sender, IGameInstance game, IChatLog chat) {
        if (argNumber == 0) {
            return this.entityAutocomplete;
        } else {
            return Collections.emptyList();
        }
    }
}
