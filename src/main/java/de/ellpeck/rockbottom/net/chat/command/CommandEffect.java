package de.ellpeck.rockbottom.net.chat.command;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.effect.ActiveEffect;
import de.ellpeck.rockbottom.api.effect.IEffect;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.chat.Command;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandEffect extends Command{

    private final List<String> effectAutocomplete = new ArrayList<>();

    public CommandEffect(){
        super(ResourceName.intern("effect"), "Gives the player an effect. Params: <'add'/'remove'> <mod_id/effect_name> [time]", 4);

        for(ResourceName name : RockBottomAPI.EFFECT_REGISTRY.keySet()){
            this.effectAutocomplete.add(name.toString());
        }
    }

    @Override
    public ChatComponent execute(String[] args, ICommandSender sender, String playerName, IGameInstance game, IChatLog chat){
        if(sender instanceof AbstractEntityPlayer){
            AbstractEntityPlayer player = (AbstractEntityPlayer)sender;

            boolean remove;
            if(args.length > 0 && "add".equals(args[0])){
                remove = false;
            }
            else if(args.length > 0 && "remove".equals(args[0])){
                remove = true;
            }
            else{
                return new ChatComponentText(FormattingCode.RED+"Specify an action!");
            }

            IEffect effect;
            if(args.length > 1){
                try{
                    ResourceName name = new ResourceName(args[1]);
                    effect = RockBottomAPI.EFFECT_REGISTRY.get(name);
                }
                catch(Exception e){
                    return new ChatComponentText(FormattingCode.RED+"'"+args[1]+"' isn't a valid effect name!");
                }
            }
            else{
                return new ChatComponentText(FormattingCode.RED+"Specify an effect!");
            }

            int time = 0;
            if(args.length > 2){
                try{
                    time = Util.clamp(Integer.parseInt(args[2]), 0, effect.getMaxDuration(player));
                }
                catch(Exception ignored){
                }
            }

            if(effect != null){
                ActiveEffect active = new ActiveEffect(effect, time);

                if(remove){
                    player.removeEffect(effect);
                    return new ChatComponentText(FormattingCode.GREEN+"Removed effect ").append(new ChatComponentTranslation(effect.getUnlocalizedName(active, player)));
                }
                else{
                    player.addEffect(active);
                    return new ChatComponentText(FormattingCode.GREEN+"Added effect ").append(new ChatComponentTranslation(effect.getUnlocalizedName(active, player))).append(new ChatComponentText(" with a time of "+time));
                }
            }
            else{
                return new ChatComponentText(FormattingCode.RED+"An effect with the name '"+args[0]+"' doesn't exist!");
            }
        }
        else{
            return new ChatComponentText(FormattingCode.RED+"Only players can have effects!");
        }
    }

    @Override
    public List<String> getAutocompleteSuggestions(String[] args, int argNumber, ICommandSender sender, IGameInstance game, IChatLog chat){
        if(argNumber == 0){
            return Arrays.asList("add", "remove");
        }
        else if(argNumber == 1){
            return this.effectAutocomplete;
        }
        else{
            return Collections.emptyList();
        }
    }
}
