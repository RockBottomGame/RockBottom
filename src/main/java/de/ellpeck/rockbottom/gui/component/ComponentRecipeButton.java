package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.assets.font.FormattingCode;
import de.ellpeck.rockbottom.construction.IRecipe;
import de.ellpeck.rockbottom.gui.GuiContainer;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.render.item.IItemRenderer;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

public class ComponentRecipeButton extends ComponentButton{

    private static final Color TRANSPARENT = new Color(1F, 1F, 1F, 0.5F);

    public final EntityPlayer player;
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
    public void render(RockBottom game, AssetManager manager, Graphics g){
        super.render(game, manager, g);

        List<ItemInstance> outputs = this.recipe.getOutputs();
        ItemInstance instance = outputs.get(0);
        IItemRenderer.renderItemInGui(game, manager, g, instance, this.x, this.y, 1F, this.canConstruct ? Color.white : TRANSPARENT);
    }

    @Override
    protected String[] getHover(){
        AssetManager manager = RockBottom.get().assetManager;

        List<ItemInstance> inputs = this.recipe.getInputs();
        List<ItemInstance> outputs = this.recipe.getOutputs();

        List<String> hover = new ArrayList<>();

        hover.add(manager.localize("info.constructs")+":");
        for(ItemInstance inst : outputs){
            hover.add(FormattingCode.YELLOW+" "+inst.getDisplayName()+" x"+inst.getAmount());
        }

        hover.add(manager.localize("info.uses")+":");
        for(ItemInstance inst : inputs){
            FormattingCode code;

            if(!this.canConstruct && !this.player.inv.containsItem(inst)){
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
