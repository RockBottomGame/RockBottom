package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.init.AbstractGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

public class ComponentRecipeButton extends ComponentButton{

    private static final Color TRANSPARENT = new Color(1F, 1F, 1F, 0.5F);
    private static final IResourceName LOC_CONSTRUCTS = AbstractGame.internalRes("info.constructs");
    private static final IResourceName LOC_USES = AbstractGame.internalRes("info.uses");

    public final AbstractEntityPlayer player;
    public final IRecipe recipe;
    public final int recipeId;
    public final boolean canConstruct;
    private final ComponentConstruction component;

    public ComponentRecipeButton(ComponentConstruction component, int x, int y, int sizeX, int sizeY, IRecipe recipe, int recipeId, boolean canConstruct){
        super(component.gui, x, y, sizeX, sizeY, null, null);
        this.player = component.gui.player;
        this.recipeId = recipeId;
        this.recipe = recipe;
        this.canConstruct = canConstruct;
        this.component = component;
    }


    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        if(this.recipe != null){
            super.render(game, manager, g);

            List<ItemInstance> outputs = this.recipe.getOutputs();
            ItemInstance instance = outputs.get(0);
            RockBottomAPI.getApiHandler().renderItemInGui(game, manager, g, instance, this.x+2F, this.y+2F, 1F, this.canConstruct ? Color.white : TRANSPARENT);
        }
    }

    @Override
    protected String[] getHover(){
        if(this.recipe != null){
            IGameInstance game = AbstractGame.get();
            IAssetManager manager = game.getAssetManager();

            List<IUseInfo> inputs = this.recipe.getInputs();
            List<ItemInstance> outputs = this.recipe.getOutputs();

            List<String> hover = new ArrayList<>();

            hover.add(manager.localize(LOC_CONSTRUCTS)+":");
            for(ItemInstance inst : outputs){
                hover.add(FormattingCode.YELLOW+" "+inst.getDisplayName()+" x"+inst.getAmount());
            }

            hover.add(manager.localize(LOC_USES)+":");
            for(IUseInfo info : inputs){
                FormattingCode code;

                if(!this.canConstruct && !this.player.getInv().containsResource(info)){
                    code = FormattingCode.RED;
                }
                else{
                    code = FormattingCode.GREEN;
                }

                ItemInstance inst;

                List<ItemInstance> items = info.getItems();
                if(items.size() > 1){
                    int index = (game.getTotalTicks()/Constants.TARGET_TPS)%(items.size());
                    inst = items.get(index);
                }
                else{
                    inst = items.get(0);
                }

                hover.add(code+" "+inst.getDisplayName()+" x"+inst.getAmount());
            }

            return hover.toArray(new String[hover.size()]);
        }
        else{
            return super.getHover();
        }
    }

    @Override
    public boolean onPressed(IGameInstance game){
        if(this.canConstruct){
            this.component.consumer.accept(this.recipe, this.recipeId);
            return true;
        }
        return false;
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("recipe_button");
    }
}