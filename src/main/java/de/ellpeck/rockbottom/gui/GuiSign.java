package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.api.gui.component.ComponentToggleButton;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySign;
import org.newdawn.slick.Input;

import java.util.ArrayList;
import java.util.List;

public class GuiSign extends Gui{

    private final List<ComponentInputField> inputFields = new ArrayList<>();
    private final TileEntitySign tile;
    private boolean isEditing;

    public GuiSign(TileEntitySign tile){
        super(210, 100);
        this.tile = tile;
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);

        for(int i = 0; i < this.tile.text.length; i++){
            int finalI = i;

            ComponentInputField field = new ComponentInputField(this, this.width/2-100, 5+i*14, 200, 12, true, true, i == 0, 35, true, strg -> {
                this.tile.text[finalI] = strg;
                this.tile.world.setDirty(this.tile.x, this.tile.y);
            });
            field.setText(this.tile.text[i]);
            this.inputFields.add(field);
        }
        this.components.addAll(this.inputFields);
        this.updateInputFields();

        this.components.add(new ComponentToggleButton(this, this.width/2-40, this.height-16, 80, 16, this.isEditing, () -> {
            this.isEditing = !this.isEditing;
            this.updateInputFields();
            return true;
        }, "button.edit"));
    }

    @Override
    public boolean onKeyboardAction(IGameInstance game, int button, char character){
        if(button == Input.KEY_UP){
            for(int i = 0; i < this.inputFields.size(); i++){
                ComponentInputField field = this.inputFields.get(i);
                if(field.isSelected()){
                    int nextIndex = i-1;

                    if(nextIndex < 0){
                        nextIndex = this.inputFields.size()-1;
                    }

                    field.setSelected(false);
                    this.inputFields.get(nextIndex).setSelected(true);
                    break;
                }
            }
            return true;
        }
        else if(button == Input.KEY_DOWN || button == Input.KEY_ENTER || button == Input.KEY_TAB){
            for(int i = 0; i < this.inputFields.size(); i++){
                ComponentInputField field = this.inputFields.get(i);
                if(field.isSelected()){
                    int nextIndex = i+1;

                    if(nextIndex >= this.inputFields.size()){
                        nextIndex = 0;
                    }

                    field.setSelected(false);
                    this.inputFields.get(nextIndex).setSelected(true);
                    break;
                }
            }
            return true;
        }
        else{
            return super.onKeyboardAction(game, button, character);
        }
    }

    @Override
    public boolean canCloseWithInvKey(){
        return true;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IGraphics g){
        drawSign(manager, this.tile.text, !this.isEditing, this.x, this.y);
        super.render(game, manager, g);
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("sign");
    }

    public static void drawSign(IAssetManager manager, String[] text, boolean drawText, float x, float y){
        manager.getTexture(RockBottomAPI.createInternalRes("gui.sign")).draw(x, y, 210, 78);

        if(drawText){
            IFont font = manager.getFont();
            for(int i = 0; i < text.length; i++){
                font.drawString(x+8, y+11-font.getHeight(0.35F)/2F+i*14, text[i], 0.35F);
            }
        }
    }

    private void updateInputFields(){
        for(ComponentInputField field : this.inputFields){
            field.setActive(this.isEditing);
        }
    }
}
