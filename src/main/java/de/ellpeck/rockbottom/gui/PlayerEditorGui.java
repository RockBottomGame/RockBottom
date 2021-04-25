package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.*;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.auth.ManagementServer;
import de.ellpeck.rockbottom.auth.ManagementServerUtil;
import de.ellpeck.rockbottom.gui.menu.account.ResponseGui;
import de.ellpeck.rockbottom.init.RockBottom;
import de.ellpeck.rockbottom.render.design.PlayerDesign;
import de.ellpeck.rockbottom.render.entity.PlayerEntityRenderer;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

public class PlayerEditorGui extends Gui {

    private static final String WIIV_IS_COOL = "wiiv is cool";
    private int wiivIndex;

    private int previewType;

    private IPlayerDesign design;
    private boolean edited;

    private int hue;
    private int saturation;
    private int lightness;
    private int color;

    private SliderComponent hueSlider;
    private SliderComponent saturationSlider;
    private SliderComponent lightnessSlider;
    private ColorPickerComponent colorPicker;

    private String editName;
    private Consumer<Integer> editFunc;

    public PlayerEditorGui(Gui parent, IPlayerDesign design, int previewType) {
        super(parent);
        this.previewType = previewType;
        this.design = design.clone();// Util.GSON.fromJson(Util.GSON.toJson(design), PlayerDesign.class);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        IAssetManager assetManager = game.getAssetManager();
        int x = this.width / 2 - 78;
        int minY = 7;
        int y = minY;

        // TODO Make every setter change the `edited` field instead of doing it on click
        // Design
        this.components.add(new DesignComponentSlider(this, x, y, this.design.getBase(), IPlayerDesign.BASE.size() - 1, this.design::setBase, this.design::setEyeColor, this.design::getEyeColor, assetManager.localize(ResourceName.intern("button.player_design.base"))));
        //this.components.add(new ColorPickerComponent(this, colorX, y, this.design.getEyeColor(), (this.design::setEyeColor)));
        y += 14;
        this.components.add(new DesignComponentSlider(this, x, y, this.design.getHair(), IPlayerDesign.HAIR.size() - 1, this.design::setHair, this.design::setHairColor, this.design::getHairColor, assetManager.localize(ResourceName.intern("button.player_design.hair"))));
        //this.components.add(new ColorPickerComponent(this, colorX, y, this.design.getHairColor(), this.design::setHairColor));
        y += 14;
        this.components.add(new DesignComponentSlider(this, x, y, this.design.getEyebrows(), IPlayerDesign.EYEBROWS.size() - 1, this.design::setEyebrows, this.design::setEyebrowsColor, this.design::getEyebrowsColor, assetManager.localize(ResourceName.intern("button.player_design.eyebrows"))));
        //this.components.add(new ColorPickerComponent(this, colorX, y, this.design.getEyebrowsColor(), this.design::setEyebrowsColor));
        y += 14;
        // TODO Change favourite color setter thing
        this.components.add(new DesignComponentSlider(this, x, y, this.design.getMouth(), IPlayerDesign.MOUTH.size() - 1, this.design::setMouth, this.design::setFavoriteColor, this.design::getFavoriteColor, assetManager.localize(ResourceName.intern("button.player_design.mouth"))));
        y += 14;
        this.components.add(new DesignComponentSlider(this, x, y, this.design.getBeard(), IPlayerDesign.BEARD.size() - 1, this.design::setBeard, this.design::setBeardColor, this.design::getBeardColor, assetManager.localize(ResourceName.intern("button.player_design.beard"))));
        //this.components.add(new ColorPickerComponent(this, colorX, y, this.design.getBeardColor(), this.design::setBeardColor));
        y = minY;
        x += 80;
        this.components.add(new DesignComponentSlider(this, x, y, this.design.getShirt(), IPlayerDesign.SHIRT.size() - 1, this.design::setShirt, this.design::setShirtColor, this.design::getShirtColor, assetManager.localize(ResourceName.intern("button.player_design.shirt"))));
        //this.components.add(new ColorPickerComponent(this, colorX, y, this.design.getShirtColor(), this.design::setShirtColor));
        y += 14;
        this.components.add(new DesignComponentSlider(this, x, y, this.design.getSleeves(), IPlayerDesign.SLEEVES.size() - 1, this.design::setSleeves, this.design::setSleevesColor, this.design::getSleevesColor, assetManager.localize(ResourceName.intern("button.player_design.sleeves"))));
        //this.components.add(new ColorPickerComponent(this, colorX, y, this.design.getSleevesColor(), this.design::setSleevesColor));
        y += 14;
        this.components.add(new DesignComponentSlider(this, x, y, this.design.getPants(), IPlayerDesign.PANTS.size() - 1, this.design::setPants, this.design::setPantsColor, this.design::getPantsColor, assetManager.localize(ResourceName.intern("button.player_design.pants"))));
        //this.components.add(new ColorPickerComponent(this, colorX, y, this.design.getPantsColor(), this.design::setPantsColor));
        y += 14;
        this.components.add(new DesignComponentSlider(this, x, y, this.design.getFootwear(), IPlayerDesign.FOOTWEAR.size() - 1, this.design::setFootwear, this.design::setFootwearColor, this.design::getFootwearColor, assetManager.localize(ResourceName.intern("button.player_design.footwear"))));
        //this.components.add(new ColorPickerComponent(this, colorX, y, this.design.getFootwearColor(), this.design::setFootwearColor));
        y += 14;
        // TODO Change favourite color setter thing
        this.components.add(new DesignComponentSlider(this, x, y, this.design.getAccessory(), IPlayerDesign.ACCESSORIES.size() - 1, this.design::setAccessory, this.design::setFavoriteColor, this.design::getFavoriteColor, assetManager.localize(ResourceName.intern("button.player_design.accessory"))));

        /*
        InputFieldComponent nameField = new InputFieldComponent(this, x - 98, 0, 80, 12, true, true, false, 24, true,
                (name) -> { if (!name.isEmpty()) this.design.setName(name); }) {
            @Override
            public String getDisplayText() {
                return Colors.toFormattingCode(design.getFavoriteColor()) + super.getDisplayText();
            }
        };
        nameField.setText(design.getName());
        this.components.add(nameField);
         */

        // Fav Color
        /*
        this.components.add(new de.ellpeck.rockbottom.api.gui.component.ColorPickerComponent(this, colorX - 98, 0, 12, 12, this.design.getFavoriteColor(), ((color, aBoolean) -> this.design.setFavoriteColor(color)), true));
         */

        // Sex
        this.components.add(new ToggleButtonComponent(this, 14, minY, 65, 12, this.design.isFemale(), () -> {
            this.design.setFemale(!design.isFemale());
            return true;
        }, "button.player_design.sex"));

        // Color Slider
        this.components.add(this.hueSlider = new SliderComponent(this, this.width / 2 - 80, 91, 75, 12, 180, 0, 360, (integer, aBoolean) -> this.setHue(integer), assetManager.localize(ResourceName.intern("button.player_design.hue"))));
        this.hueSlider.setActive(false);
        this.components.add(this.saturationSlider = new SliderComponent(this, this.width / 2 - 80, 105, 75, 12, 128, 0, 255, (integer, aBoolean) -> this.setSaturation(integer), assetManager.localize(ResourceName.intern("button.player_design.saturation"))));
        this.saturationSlider.setActive(false);
        this.components.add(this.lightnessSlider = new SliderComponent(this, this.width / 2 - 80, 119, 75, 12, 128, 0, 255, (integer, aBoolean) -> this.setLightness(integer), assetManager.localize(ResourceName.intern("button.player_design.lightness"))));
        this.lightnessSlider.setActive(false);
        this.components.add(this.colorPicker = new ColorPickerComponent(this, this.width / 2 + 13, 83, 0, this::setColor));
        this.colorPicker.setActive(false);

        // Preview
        this.components.add(new SliderComponent(this, 14, 119, 65, 12, this.previewType + 1, 1, 4, ((integer, aBoolean) -> this.previewType = integer - 1), assetManager.localize(ResourceName.intern("button.player_design.preview"))));

        // Randomize
        this.components.add(new FancyButtonComponent(this, this.width / 2 - 7, this.height - 41, 14, 14, () -> {
            this.components.add(new ConfirmationPopupComponent(this, this.width / 2, this.height - 45 + 7, aBoolean -> {
                if (aBoolean) {
                    PlayerDesign.randomizeDesign(this.design);
                    this.init(game);
                }
            }));
            this.sortComponents();
            return true;
        }, ResourceName.intern("gui.randomize"), assetManager.localize(ResourceName.intern("info.randomize"))));

        // Copy
        this.components.add(new ButtonComponent(this, this.width / 2 - 88, this.height - 41, 80, 14, () -> {
            String data = Util.GSON.toJson(this.design);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(data), null);
            return true;
        }, assetManager.localize(ResourceName.intern("button.copy_design")), assetManager.localize(ResourceName.intern("info.copy_design"))));

        // Paste
        this.components.add(new ButtonComponent(this, this.width / 2 + 8, this.height - 41, 80, 14, () -> {
            this.components.add(new ConfirmationPopupComponent(this, this.width / 2 + 48, this.height - 45 + 7, aBoolean -> {
                if (aBoolean) {
                    try {
                        String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                        this.design = Util.GSON.fromJson(data, PlayerDesign.class);
                        game.getGuiManager().updateDimensions();
                    } catch (Exception e) {
                        RockBottomAPI.logger().log(Level.WARNING, "Couldn't paste player design from clipboard", e);
                    }
                }
            }));
            this.sortComponents();
            return true;
        }, assetManager.localize(ResourceName.intern("button.paste_design")), assetManager.localize(ResourceName.intern("info.paste_design"))));

        // Back
        this.components.add(new ButtonComponent(this, this.width / 2 - 82, this.height - 23, 80, 16, () -> {
            if (this.edited) {
                this.components.add(new ConfirmationPopupComponent(this, this.width / 2 - 42, this.height - 27 + 7, aBoolean -> {
                    if (aBoolean) {
                        game.getGuiManager().openGui(this.parent);
                    }
                }));
                this.sortComponents();
            } else  {
                game.getGuiManager().openGui(this.parent);
            }
            return true;
        }, assetManager.localize(ResourceName.intern("button.back"))));

        // Save
        this.components.add(new ButtonComponent(this, this.width / 2 + 2, this.height - 23, 80, 16, () -> {
            if (this.edited) {
                this.components.add(new ConfirmationPopupComponent(this, this.width / 2 + 42, this.height - 27 + 7, aBoolean -> {
                    if (aBoolean) {
                        ResponseGui responseGui = new ResponseGui(this.parent);
                        game.getGuiManager().openGui(responseGui);
                        ManagementServerUtil.setPlayerDesign(ManagementServer.getServer().getApiToken(), this.design,
                                msg ->  {
                                    game.getAccount().setPlayerDesign(this.design);
                                    game.getGuiManager().openGui(this.parent);
                                },
                                msg -> responseGui.displayResponse(assetManager.localize(ResourceName.intern(msg))));
                    }
                }));
                this.sortComponents();
            }
            return true;
        }, assetManager.localize(ResourceName.intern("button.save"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer renderer) {
        super.render(game, manager, renderer);

        IFont font = manager.getFont();
        float scale = 0.35F;
        PlayerEntityRenderer.renderPlayer(null, game, manager, renderer, this.design, 24, 18 + font.getHeight(scale), 45F, this.previewType, Colors.WHITE);
        if (this.editName != null) {
            font.drawCenteredString(this.width / 2f - 40, 91 - font.getHeight(scale) - 1, this.editName, scale, false);
        }
    }

    private void updateHSLSliders(int rgb) {
        if (!this.hueSlider.isActive()) {
            this.hueSlider.setActive(true);
            this.saturationSlider.setActive(true);
            this.lightnessSlider.setActive(true);
            this.colorPicker.setActive(true);
        }
        int[] hsl = Colors.rgbToHsl(rgb);
        this.hue = hsl[0];
        this.saturation = hsl[1];
        this.lightness = hsl[2];
        this.hueSlider.setNumber(this.hue);
        this.saturationSlider.setNumber(this.saturation);
        this.lightnessSlider.setNumber(this.lightness);
    }

    private void setHue(int hue) {
        this.hue = hue;
        this.updateColor();
    }

    private void setSaturation(int saturation) {
        this.saturation = saturation;
        this.updateColor();
    }

    private void setLightness(int lightness) {
        this.lightness = lightness;
        this.updateColor();
    }

    private void setColor(int color) {
        this.updateHSLSliders(color);
        this.updateColor();
    }

    private void updateColor() {
        this.color = Colors.hsl(this.hue, this.saturation, this.lightness);
        if (this.editFunc != null) {
            this.editFunc.accept(this.color);
        }
    }

    @Override
    public boolean onCharInput(IGameInstance game, int codePoint, char[] characters) {
        if (!super.onCharInput(game, codePoint, characters)) {
            boolean did = false;
            for (char c : characters) {
                if (this.wiivIndex < WIIV_IS_COOL.length()) {
                    if (WIIV_IS_COOL.charAt(this.wiivIndex) == c) {
                        this.wiivIndex++;

                        if (this.wiivIndex >= WIIV_IS_COOL.length()) {
                            this.design.setBase(-1);

                            this.wiivIndex = 0;
                            this.init(game);
                        }

                        did = true;
                    } else {
                        this.wiivIndex = 0;
                    }
                }
            }
            return did;
        } else {
            return true;
        }
    }

    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y) {
        if (!super.onMouseAction(game, button, x, y)) {
            this.hueSlider.setActive(false);
            this.saturationSlider.setActive(false);
            this.lightnessSlider.setActive(false);
            this.colorPicker.setActive(false);
            this.editFunc = null;
            this.editName = null;
            return false;
        }

        this.edited = true;
        return true;
    }

    @Override
    public void onClosed(IGameInstance game) {
        super.onClosed(game);
        IPlayerDesign design = game.getPlayerDesign();
        RockBottom.savePlayerDesign(game, design);
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("player_editor");
    }

    private class DesignComponentSlider extends SliderComponent {

        private final Supplier<Integer> getColor;
        private final Consumer<Integer> editColor;

        public DesignComponentSlider(Gui gui, int x, int y, int initialNumber, int max, Consumer<Integer> editComponent, Consumer<Integer> editColor, Supplier<Integer> getColor, String text, String... hover) {
            super(gui, x, y, 75, 12, initialNumber, 0, max, ((integer, aBoolean) -> editComponent.accept(integer)), text, hover);
            this.editColor = editColor;
            this.getColor = getColor;
        }

        @Override
        protected String getText() {
            return this.text + ": " + (this.number + 1);
        }

        @Override
        public boolean onMouseAction(IGameInstance game, int button, float x, float y) {
            if (super.onMouseAction(game, button, x, y)) {
                PlayerEditorGui.this.editName = this.text;
                PlayerEditorGui.this.editFunc = this.editColor;
                PlayerEditorGui.this.updateHSLSliders(this.getColor.get());
                return true;
            }

            return false;
        }
    }

    private static class ColorPickerComponent extends de.ellpeck.rockbottom.api.gui.component.ColorPickerComponent {

        public ColorPickerComponent(Gui gui, int x, int y, int defaultColor, Consumer<Integer> consumer) {
            super(gui, x, y, 48, 48, defaultColor, (color, aBoolean) -> consumer.accept(color), false);
        }
    }
}
