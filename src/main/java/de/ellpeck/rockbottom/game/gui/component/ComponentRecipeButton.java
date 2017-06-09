package de.ellpeck.rockbottom.game.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.construction.IRecipe;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

public class ComponentRecipeButton extends ComponentButton{

    private static final Color TRANSPARENT = new Color(1F, 1F, 1F, 0.5F);
    private static final IResourceName LOC_CONSTRUCTS = RockBottom.internalRes("info.constructs");
    private static final IResourceName LOC_USES = RockBottom.internalRes("info.uses");

    public final AbstractEntityPlayer player;
    public final IRecipe recipe;
    public final int recipeId;
    public final boolean canConstruct;

    public ComponentRecipeButton(GuiContainer gui, int id, int x, int y, int sizeX, int sizeY, IRecipe recipe, int recipeId, boolean canConstruct){
        super(gui, id, x, y, sizeX, sizeY, null);
        this.player = gui.player;
        this.recipe = recipe;
        this.recipeId = recipeId;
        this.canConstruct = canConstruct;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        super.render(game, manager, g);

        List<ItemInstance> outputs = this.recipe.getOutputs();
        ItemInstance instance = outputs.get(0);
        RockBottomAPI.getApiHandler().renderItemInGui(game, manager, g, instance, this.x+2F, this.y+2F, 1F, this.canConstruct ? Color.white : TRANSPARENT);
    }

    @Override
    protected String[] getHover(){
        IAssetManager manager = RockBottom.get().getAssetManager();

        List<ItemInstance> inputs = this.recipe.getInputs();
        List<ItemInstance> outputs = this.recipe.getOutputs();

        List<String> hover = new ArrayList<>();

        hover.add(manager.localize(LOC_CONSTRUCTS)+":");
        for(ItemInstance inst : outputs){
            hover.add(FormattingCode.YELLOW+" "+inst.getDisplayName()+" x"+inst.getAmount());
        }

        hover.add(manager.localize(LOC_USES)+":");
        for(ItemInstance inst : inputs){
            FormattingCode code;

            if(!this.canConstruct && !this.player.getInv().containsItem(inst)){
                code = FormattingCode.RED;
            }
            else{
                code = FormattingCode.GREEN;
            }

            hover.add(code+" "+inst.getDisplayName()+" x"+inst.getAmount());
        }

        return hover.toArray(new String[hover.size()]);
    }
}
