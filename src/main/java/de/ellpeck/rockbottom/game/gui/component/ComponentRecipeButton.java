package de.ellpeck.rockbottom.game.gui.component;

import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.game.assets.font.FormattingCode;
import de.ellpeck.rockbottom.game.construction.IRecipe;
import de.ellpeck.rockbottom.game.gui.GuiContainer;
import de.ellpeck.rockbottom.game.item.ItemInstance;
import de.ellpeck.rockbottom.game.util.Util;
import de.ellpeck.rockbottom.game.world.entity.player.EntityPlayer;
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
        Util.renderItemInGui(game, manager, g, instance, this.x+2F, this.y+2F, 1F, this.canConstruct ? Color.white : TRANSPARENT);
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
