package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.api.gui.component.ComponentSlider;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.gui.component.ComponentColorPicker;
import de.ellpeck.rockbottom.render.PlayerDesign;
import de.ellpeck.rockbottom.render.entity.PlayerEntityRenderer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class GuiPlayerEditor extends Gui{

    private int previewType;
    private ComponentInputField nameField;

    public GuiPlayerEditor(Gui parent){
        super(100, 100, parent);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        IPlayerDesign design = game.getPlayerDesign();
        IAssetManager assetManager = game.getAssetManager();
        int x = (int)game.getWidthInGui()/2;
        int colorX = x+82;

        this.components.add(new Slider(this, 0, x, 5, design.getBase(), IPlayerDesign.BASE.size()-1, design:: setBase, assetManager.localize(RockBottom.internalRes("button.player_design.base"))));
        this.components.add(new ColorPicker(this, colorX, 5, design.getEyeColor(), (design:: setEyeColor)));

        this.components.add(new Slider(this, 1, x, 23, design.getHair(), IPlayerDesign.HAIR.size()-1, design:: setHair, assetManager.localize(RockBottom.internalRes("button.player_design.hair"))));
        this.components.add(new ColorPicker(this, colorX, 23, design.getHairColor(), design:: setHairColor));

        this.components.add(new Slider(this, 2, x, 41, design.getShirt(), IPlayerDesign.SHIRT.size()-1, design:: setShirt, assetManager.localize(RockBottom.internalRes("button.player_design.shirt"))));
        this.components.add(new ColorPicker(this, colorX, 41, design.getShirtColor(), design:: setShirtColor));

        this.components.add(new Slider(this, 3, x, 59, design.getSleeves(), IPlayerDesign.SLEEVES.size()-1, design:: setSleeves, assetManager.localize(RockBottom.internalRes("button.player_design.sleeves"))));
        this.components.add(new ColorPicker(this, colorX, 59, design.getSleevesColor(), design:: setSleevesColor));

        this.components.add(new Slider(this, 4, x, 77, design.getPants(), IPlayerDesign.PANTS.size()-1, design:: setPants, assetManager.localize(RockBottom.internalRes("button.player_design.pants"))));
        this.components.add(new ColorPicker(this, colorX, 77, design.getPantsColor(), design:: setPantsColor));

        this.components.add(new Slider(this, 5, x, 95, design.getFootwear(), IPlayerDesign.FOOTWEAR.size()-1, design:: setFootwear, assetManager.localize(RockBottom.internalRes("button.player_design.footwear"))));
        this.components.add(new ColorPicker(this, colorX, 95, design.getFootwearColor(), design:: setFootwearColor));

        this.components.add(new Slider(this, 6, x, 113, design.getAccessory(), IPlayerDesign.ACCESSORIES.size()-1, design:: setAccessory, assetManager.localize(RockBottom.internalRes("button.player_design.accessory"))));

        this.nameField = new ComponentInputField(this, x, 131, 80, 16, true, true, false, 24, true){
            @Override
            public String getDisplayText(){
                return Util.colorToFormattingCode(design.getFavoriteColor())+super.getDisplayText();
            }
        };
        this.nameField.setText(design.getName());
        this.components.add(this.nameField);
        this.components.add(new ColorPicker(this, colorX, 131, design.getFavoriteColor(), design:: setFavoriteColor));

        this.components.add(new ComponentSlider(this, -2, (int)game.getWidthInGui()/2-98, 131, 80, 16, this.previewType+1, 1, 4, new ComponentSlider.ICallback(){
            @Override
            public void onNumberChange(float mouseX, float mouseY, int min, int max, int number){
                GuiPlayerEditor.this.previewType = number-1;
            }
        }, assetManager.localize(RockBottom.internalRes("button.player_design.preview"))));

        this.components.add(new ComponentButton(this, -3, this.guiLeft+this.sizeX/2+33, (int)game.getHeightInGui()-20, 16, 16, "?", assetManager.localize(RockBottom.internalRes("info.randomize"))));
        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2-49, (int)game.getHeightInGui()-20, 80, 16, assetManager.localize(RockBottom.internalRes("button.back"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        super.render(game, manager, g);

        int x = (int)game.getWidthInGui()/2-88;
        PlayerEntityRenderer.renderPlayer(manager, game.getPlayerDesign(), x, 5, 60F, this.previewType, game.getTotalTicks(), ".hanging", Color.white);
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
    public boolean onButtonActivated(IGameInstance game, int button){
        IGuiManager guiManager = game.getGuiManager();

        if(button == -1){
            guiManager.openGui(this.parent);
            return true;
        }
        else if(button == -3){
            IPlayerDesign design = game.getPlayerDesign();
            PlayerDesign.randomizeDesign(design);

            this.initGui(game);
            return true;
        }
        else{
            return false;
        }
    }

    private static class Slider extends ComponentSlider{

        public Slider(Gui gui, int id, int x, int y, int initialNumber, int max, IChange callback, String text, String... hover){
            super(gui, id, x, y, 80, 16, initialNumber, 0, max, new ICallback(){
                @Override
                public void onNumberChange(float mouseX, float mouseY, int min, int max, int number){
                    callback.onNumberChange(number);
                }
            }, text, hover);
        }

        @Override
        protected String getText(){
            return this.text+": "+(this.number+1);
        }

        private interface IChange{

            void onNumberChange(int number);
        }
    }

    private static class ColorPicker extends ComponentColorPicker{

        public ColorPicker(Gui gui, int x, int y, Color defaultColor, IChange callback){
            super(gui, x, y, 16, 16, defaultColor, new ICallback(){
                @Override
                public void onChange(float mouseX, float mouseY, Color color){
                    callback.onChange(color);
                }
            });
        }

        private interface IChange{

            void onChange(Color color);
        }
    }
}
