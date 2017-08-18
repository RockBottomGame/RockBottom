package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentConfirmationPopup;
import de.ellpeck.rockbottom.api.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.api.gui.component.ComponentSlider;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.component.ComponentColorPicker;
import de.ellpeck.rockbottom.gui.component.ComponentToggleButton;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.render.PlayerDesign;
import de.ellpeck.rockbottom.render.entity.PlayerEntityRenderer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.util.function.Consumer;

public class GuiPlayerEditor extends Gui{

    private static final String WIIV_IS_COOL = "wiiv is cool";
    private int wiivIndex;

    private int previewType;
    private ComponentInputField nameField;

    public GuiPlayerEditor(Gui parent){
        super(100, 160, parent);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        IPlayerDesign design = game.getPlayerDesign();
        IAssetManager assetManager = game.getAssetManager();
        int x = (int)game.getWidthInGui()/2;
        int y = this.guiTop;
        int colorX = x+82;

        this.components.add(new Slider(this, x, y, design.getBase(), IPlayerDesign.BASE.size()-1, design:: setBase, assetManager.localize(AbstractGame.internalRes("button.player_design.base"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getEyeColor(), (design:: setEyeColor)));
        y += 14;
        this.components.add(new Slider(this, x, y, design.getEyebrows(), IPlayerDesign.EYEBROWS.size()-1, design:: setEyebrows, assetManager.localize(AbstractGame.internalRes("button.player_design.eyebrows"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getEyebrowsColor(), design:: setEyebrowsColor));
        y += 14;
        this.components.add(new Slider(this, x, y, design.getMouth(), IPlayerDesign.MOUTH.size()-1, design:: setMouth, assetManager.localize(AbstractGame.internalRes("button.player_design.mouth"))));
        y += 14;
        this.components.add(new Slider(this, x, y, design.getHair(), IPlayerDesign.HAIR.size()-1, design:: setHair, assetManager.localize(AbstractGame.internalRes("button.player_design.hair"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getHairColor(), design:: setHairColor));
        y += 14;
        this.components.add(new Slider(this, x, y, design.getShirt(), IPlayerDesign.SHIRT.size()-1, design:: setShirt, assetManager.localize(AbstractGame.internalRes("button.player_design.shirt"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getShirtColor(), design:: setShirtColor));
        y += 14;
        this.components.add(new Slider(this, x, y, design.getSleeves(), IPlayerDesign.SLEEVES.size()-1, design:: setSleeves, assetManager.localize(AbstractGame.internalRes("button.player_design.sleeves"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getSleevesColor(), design:: setSleevesColor));
        y += 14;
        this.components.add(new Slider(this, x, y, design.getPants(), IPlayerDesign.PANTS.size()-1, design:: setPants, assetManager.localize(AbstractGame.internalRes("button.player_design.pants"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getPantsColor(), design:: setPantsColor));
        y += 14;
        this.components.add(new Slider(this, x, y, design.getFootwear(), IPlayerDesign.FOOTWEAR.size()-1, design:: setFootwear, assetManager.localize(AbstractGame.internalRes("button.player_design.footwear"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getFootwearColor(), design:: setFootwearColor));
        y += 14;
        this.components.add(new Slider(this, x, y, design.getAccessory(), IPlayerDesign.ACCESSORIES.size()-1, design:: setAccessory, assetManager.localize(AbstractGame.internalRes("button.player_design.accessory"))));
        y += 14;
        this.components.add(new Slider(this, x, y, design.getBeard(), IPlayerDesign.BEARD.size()-1, design:: setBeard, assetManager.localize(AbstractGame.internalRes("button.player_design.beard"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getBeardColor(), design:: setBeardColor));

        this.nameField = new ComponentInputField(this, x-98, this.guiTop, 80, 12, true, true, false, 24, true){
            @Override
            public String getDisplayText(){
                return Util.colorToFormattingCode(design.getFavoriteColor())+super.getDisplayText();
            }
        };
        this.nameField.setText(design.getName());
        this.components.add(this.nameField);
        this.components.add(new ComponentColorPicker(this, colorX-98, this.guiTop, 12, 12, design.getFavoriteColor(), ((color, aBoolean) -> design.setFavoriteColor(color)), true));

        this.components.add(new ComponentToggleButton(this, x-98, this.guiTop+14, 80, 12, design.isFemale(), () -> {
            design.setFemale(!design.isFemale());
            return true;
        }, "button.player_design.sex"));

        this.components.add(new ComponentSlider(this, x-98, this.guiTop+126, 80, 12, this.previewType+1, 1, 6, ((integer, aBoolean) -> this.previewType = integer-1), assetManager.localize(AbstractGame.internalRes("button.player_design.preview"))));

        this.components.add(new ComponentButton(this, this.guiLeft+this.sizeX/2+33, (int)game.getHeightInGui()-20, 16, 16, () -> {
            this.components.add(0, new ComponentConfirmationPopup(this, this.guiLeft+this.sizeX/2+41, (int)game.getHeightInGui()-12, aBoolean -> {
                if(aBoolean){
                    PlayerDesign.randomizeDesign(design);
                    this.initGui(game);
                }
            }));
            return true;
        }, "?", assetManager.localize(AbstractGame.internalRes("info.randomize"))));
        this.components.add(new ComponentButton(this, this.guiLeft+this.sizeX/2-49, (int)game.getHeightInGui()-20, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, assetManager.localize(AbstractGame.internalRes("button.back"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        super.render(game, manager, g);

        int x = (int)game.getWidthInGui()/2-84;
        PlayerEntityRenderer.renderPlayer(manager, game.getPlayerDesign(), x, this.guiTop+32, 45F, this.previewType, ".hanging", Color.white);
    }

    @Override
    public boolean onKeyboardAction(IGameInstance game, int button, char character){
        if(!super.onKeyboardAction(game, button, character)){
            if(this.wiivIndex < WIIV_IS_COOL.length()){
                if(WIIV_IS_COOL.charAt(this.wiivIndex) == character){
                    this.wiivIndex++;

                    if(this.wiivIndex >= WIIV_IS_COOL.length()){
                        IPlayerDesign desgin = game.getPlayerDesign();
                        desgin.setBase(-1);

                        this.wiivIndex = 0;
                        this.initGui(game);
                    }

                    return true;
                }
                else{
                    this.wiivIndex = 0;
                }
            }
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public void onClosed(IGameInstance game){
        super.onClosed(game);

        IPlayerDesign design = game.getPlayerDesign();

        String text = this.nameField.getText().trim();
        if(!text.isEmpty()){
            design.setName(text);
        }

        design.saveToFile();
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("player_editor");
    }

    private static class Slider extends ComponentSlider{

        public Slider(Gui gui, int x, int y, int initialNumber, int max, Consumer<Integer> consumer, String text, String... hover){
            super(gui, x, y, 80, 12, initialNumber, 0, max, ((integer, aBoolean) -> consumer.accept(integer)), text, hover);
        }

        @Override
        protected String getText(){
            return this.text+": "+(this.number+1);
        }
    }

    private static class ColorPicker extends ComponentColorPicker{

        public ColorPicker(Gui gui, int x, int y, Color defaultColor, Consumer<Color> consumer){
            super(gui, x, y, 12, 12, defaultColor, (color, aBoolean) -> consumer.accept(color), true);
        }
    }
}
