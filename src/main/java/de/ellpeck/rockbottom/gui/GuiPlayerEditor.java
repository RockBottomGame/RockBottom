package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.*;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.init.RockBottom;
import de.ellpeck.rockbottom.render.design.PlayerDesign;
import de.ellpeck.rockbottom.render.entity.PlayerEntityRenderer;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.function.Consumer;
import java.util.logging.Level;

public class GuiPlayerEditor extends Gui {

    private static final String WIIV_IS_COOL = "wiiv is cool";
    private int wiivIndex;

    private int previewType = 2;

    public GuiPlayerEditor(Gui parent) {
        super(196, 174, parent);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        IPlayerDesign design = game.getPlayerDesign();
        IAssetManager assetManager = game.getAssetManager();
        int x = this.width / 2;
        int y = 0;
        int colorX = x + 82;

        this.components.add(new Slider(this, x, y, design.getBase(), IPlayerDesign.BASE.size() - 1, design::setBase, assetManager.localize(ResourceName.intern("button.player_design.base"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getEyeColor(), (design::setEyeColor)));
        y += 14;
        this.components.add(new Slider(this, x, y, design.getEyebrows(), IPlayerDesign.EYEBROWS.size() - 1, design::setEyebrows, assetManager.localize(ResourceName.intern("button.player_design.eyebrows"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getEyebrowsColor(), design::setEyebrowsColor));
        y += 14;
        this.components.add(new Slider(this, x, y, design.getMouth(), IPlayerDesign.MOUTH.size() - 1, design::setMouth, assetManager.localize(ResourceName.intern("button.player_design.mouth"))));
        y += 14;
        this.components.add(new Slider(this, x, y, design.getHair(), IPlayerDesign.HAIR.size() - 1, design::setHair, assetManager.localize(ResourceName.intern("button.player_design.hair"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getHairColor(), design::setHairColor));
        y += 14;
        this.components.add(new Slider(this, x, y, design.getShirt(), IPlayerDesign.SHIRT.size() - 1, design::setShirt, assetManager.localize(ResourceName.intern("button.player_design.shirt"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getShirtColor(), design::setShirtColor));
        y += 14;
        this.components.add(new Slider(this, x, y, design.getSleeves(), IPlayerDesign.SLEEVES.size() - 1, design::setSleeves, assetManager.localize(ResourceName.intern("button.player_design.sleeves"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getSleevesColor(), design::setSleevesColor));
        y += 14;
        this.components.add(new Slider(this, x, y, design.getPants(), IPlayerDesign.PANTS.size() - 1, design::setPants, assetManager.localize(ResourceName.intern("button.player_design.pants"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getPantsColor(), design::setPantsColor));
        y += 14;
        this.components.add(new Slider(this, x, y, design.getFootwear(), IPlayerDesign.FOOTWEAR.size() - 1, design::setFootwear, assetManager.localize(ResourceName.intern("button.player_design.footwear"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getFootwearColor(), design::setFootwearColor));
        y += 14;
        this.components.add(new Slider(this, x, y, design.getAccessory(), IPlayerDesign.ACCESSORIES.size() - 1, design::setAccessory, assetManager.localize(ResourceName.intern("button.player_design.accessory"))));
        y += 14;
        this.components.add(new Slider(this, x, y, design.getBeard(), IPlayerDesign.BEARD.size() - 1, design::setBeard, assetManager.localize(ResourceName.intern("button.player_design.beard"))));
        this.components.add(new ColorPicker(this, colorX, y, design.getBeardColor(), design::setBeardColor));

        ComponentInputField nameField = new ComponentInputField(this, x - 98, 0, 80, 12, true, true, false, 24, true,
                (name) -> { if (!name.isEmpty()) design.setName(name); }) {
            @Override
            public String getDisplayText() {
                return Colors.toFormattingCode(design.getFavoriteColor()) + super.getDisplayText();
            }
        };
        nameField.setText(design.getName());
        this.components.add(nameField);
        this.components.add(new ComponentColorPicker(this, colorX - 98, 0, 12, 12, design.getFavoriteColor(), ((color, aBoolean) -> design.setFavoriteColor(color)), true));

        this.components.add(new ComponentToggleButton(this, x - 98, 14, 80, 12, design.isFemale(), () -> {
            design.setFemale(!design.isFemale());
            return true;
        }, "button.player_design.sex"));

        this.components.add(new ComponentSlider(this, x - 98, 126, 80, 12, this.previewType + 1, 1, 4, ((integer, aBoolean) -> this.previewType = integer - 1), assetManager.localize(ResourceName.intern("button.player_design.preview"))));

        this.components.add(new ComponentFancyButton(this, this.width / 2 - 16, this.height - 34, 14, 14, () -> {
            this.components.add(new ComponentConfirmationPopup(this, this.width / 2 - 16 + 7, this.height - 38 + 7, aBoolean -> {
                if (aBoolean) {
                    PlayerDesign.randomizeDesign(design);
                    this.init(game);
                }
            }));
            this.sortComponents();
            return true;
        }, ResourceName.intern("gui.randomize"), assetManager.localize(ResourceName.intern("info.randomize"))));

        this.components.add(new ComponentButton(this, x - 98, this.height - 34, 80, 14, () -> {
            String data = Util.GSON.toJson(design);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(data), null);
            return true;
        }, assetManager.localize(ResourceName.intern("button.copy_design")), assetManager.localize(ResourceName.intern("info.copy_design"))));

        this.components.add(new ComponentButton(this, x, this.height - 34, 80, 14, () -> {
            this.components.add(new ComponentConfirmationPopup(this, x + 40, this.height - 38 + 7, aBoolean -> {
                if (aBoolean) {
                    try {
                        String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                        game.setPlayerDesign(data);
                        game.getGuiManager().updateDimensions();
                    } catch (Exception e) {
                        RockBottomAPI.logger().log(Level.WARNING, "Couldn't paste player design from clipboard", e);
                    }
                }
            }));
            this.sortComponents();
            return true;
        }, assetManager.localize(ResourceName.intern("button.paste_design")), assetManager.localize(ResourceName.intern("info.paste_design"))));

        this.components.add(new ComponentButton(this, this.width / 2 - 40, this.height - 16, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, assetManager.localize(ResourceName.intern("button.back"))));
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        super.render(game, manager, g);

        int x = (int) g.getWidthInGui() / 2 - 84;
        PlayerEntityRenderer.renderPlayer(null, game, manager, g, game.getPlayerDesign(), x, this.y + 32, 45F, this.previewType, Colors.WHITE);
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
                            IPlayerDesign desgin = game.getPlayerDesign();
                            desgin.setBase(-1);

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
    public void onClosed(IGameInstance game) {
        super.onClosed(game);
        IPlayerDesign design = game.getPlayerDesign();
        RockBottom.savePlayerDesign(game, design);
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("player_editor");
    }

    private static class Slider extends ComponentSlider {

        public Slider(Gui gui, int x, int y, int initialNumber, int max, Consumer<Integer> consumer, String text, String... hover) {
            super(gui, x, y, 80, 12, initialNumber, 0, max, ((integer, aBoolean) -> consumer.accept(integer)), text, hover);
        }

        @Override
        protected String getText() {
            return this.text + ": " + (this.number + 1);
        }
    }

    private static class ColorPicker extends ComponentColorPicker {

        public ColorPicker(Gui gui, int x, int y, int defaultColor, Consumer<Integer> consumer) {
            super(gui, x, y, 12, 12, defaultColor, (color, aBoolean) -> consumer.accept(color), true);
        }
    }
}
