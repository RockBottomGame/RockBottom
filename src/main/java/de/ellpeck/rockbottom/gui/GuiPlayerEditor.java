package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.init.AbstractGame;
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

    private static final String WIIV_IS_COOL = "wiiv is cool";
    private int wiivIndex;

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
        int y = 5;
        int colorX = x+82;

        this.components.add(new Slider(this, 0, x, y, design.getBase(), IPlayerDesign.BASE.size()-1, design:: setBase, assetManager.localize(AbstractGame.internalRes("button.player_design.base"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getEyeColor(), (design:: setEyeColor)));
        y += 14;
        this.components.add(new Slider(this, 1, x, y, design.getEyebrows(), IPlayerDesign.EYEBROWS.size()-1, design:: setEyebrows, assetManager.localize(AbstractGame.internalRes("button.player_design.eyebrows"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getEyebrowsColor(), design:: setEyebrowsColor));
        y += 14;
        this.components.add(new Slider(this, 2, x, y, design.getMouth(), IPlayerDesign.MOUTH.size()-1, design:: setMouth, assetManager.localize(AbstractGame.internalRes("button.player_design.mouth"))));
        y += 14;
        this.components.add(new Slider(this, 3, x, y, design.getHair(), IPlayerDesign.HAIR.size()-1, design:: setHair, assetManager.localize(AbstractGame.internalRes("button.player_design.hair"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getHairColor(), design:: setHairColor));
        y += 14;
        this.components.add(new Slider(this, 4, x, y, design.getShirt(), IPlayerDesign.SHIRT.size()-1, design:: setShirt, assetManager.localize(AbstractGame.internalRes("button.player_design.shirt"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getShirtColor(), design:: setShirtColor));
        y += 14;
        this.components.add(new Slider(this, 5, x, y, design.getSleeves(), IPlayerDesign.SLEEVES.size()-1, design:: setSleeves, assetManager.localize(AbstractGame.internalRes("button.player_design.sleeves"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getSleevesColor(), design:: setSleevesColor));
        y += 14;
        this.components.add(new Slider(this, 6, x, y, design.getPants(), IPlayerDesign.PANTS.size()-1, design:: setPants, assetManager.localize(AbstractGame.internalRes("button.player_design.pants"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getPantsColor(), design:: setPantsColor));
        y += 14;
        this.components.add(new Slider(this, 7, x, y, design.getFootwear(), IPlayerDesign.FOOTWEAR.size()-1, design:: setFootwear, assetManager.localize(AbstractGame.internalRes("button.player_design.footwear"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getFootwearColor(), design:: setFootwearColor));
        y += 14;
        this.components.add(new Slider(this, 8, x, y, design.getAccessory(), IPlayerDesign.ACCESSORIES.size()-1, design:: setAccessory, assetManager.localize(AbstractGame.internalRes("button.player_design.accessory"))));

        this.nameField = new ComponentInputField(this, x, 131, 80, 16, true, true, false, 24, true){
            @Override
            public String getDisplayText(){
                return Util.colorToFormattingCode(design.getFavoriteColor())+super.getDisplayText();
            }
        };
        this.nameField.setText(design.getName());
        this.components.add(this.nameField);
        this.components.add(new ComponentColorPicker(this, colorX, 131, 16, 16, design.getFavoriteColor(), new ComponentColorPicker.ICallback(){
            @Override
            public void onChange(float mouseX, float mouseY, Color color){
                design.setFavoriteColor(color);
            }
        }));

        this.components.add(new ComponentSlider(this, -2, (int)game.getWidthInGui()/2-98, 131, 80, 16, this.previewType+1, 1, 6, new ComponentSlider.ICallback(){
            @Override
            public void onNumberChange(float mouseX, float mouseY, int min, int max, int number){
                GuiPlayerEditor.this.previewType = number-1;
            }
        }, assetManager.localize(AbstractGame.internalRes("button.player_design.preview"))));

        this.components.add(new ComponentButton(this, -3, this.guiLeft+this.sizeX/2+33, (int)game.getHeightInGui()-20, 16, 16, "?", assetManager.localize(AbstractGame.internalRes("info.randomize"))));
        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2-49, (int)game.getHeightInGui()-20, 80, 16, assetManager.localize(AbstractGame.internalRes("button.back"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        super.render(game, manager, g);

        int x = (int)game.getWidthInGui()/2-88;
        PlayerEntityRenderer.renderPlayer(manager, game.getPlayerDesign(), x, 5, 60F, this.previewType, game.getTotalTicks(), ".hanging", Color.white);
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
            super(gui, id, x, y, 80, 12, initialNumber, 0, max, new ICallback(){
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
            super(gui, x, y, 12, 12, defaultColor, new ICallback(){
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
