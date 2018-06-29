package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentFormatSelector;
import de.ellpeck.rockbottom.api.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.api.gui.component.ComponentToggleButton;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.net.packet.toserver.PacketSignText;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySign;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class GuiSign extends Gui {

    private final List<ComponentInputField> inputFields = new ArrayList<>();
    private final TileEntitySign tile;
    private boolean isEditing;
    private ComponentFormatSelector selector;

    public GuiSign(TileEntitySign tile) {
        super(210, 100);
        this.tile = tile;
    }

    public static void drawSign(IAssetManager manager, String[] text, boolean drawText, float x, float y) {
        manager.getTexture(ResourceName.intern("gui.sign")).draw(x, y, 210, 78);

        if (drawText) {
            IFont font = manager.getFont();
            for (int i = 0; i < text.length; i++) {
                font.drawString(x + 8, y + 11 - font.getHeight(0.35F) / 2F + i * 14, text[i], 0.35F);
            }
        }
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("sign");
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        for (int i = 0; i < this.tile.text.length; i++) {
            int finalI = i;

            ComponentInputField field = new ComponentInputField(this, this.width / 2 - 100, 5 + i * 14, 200, 12, true, true, i == 0, 35, true, strg -> this.tile.text[finalI] = strg);
            field.setText(this.tile.text[i]);
            this.inputFields.add(field);
        }
        this.components.addAll(this.inputFields);

        this.components.add(new ComponentToggleButton(this, this.width / 2 - 40, this.height - 16, 80, 16, this.isEditing, () -> {
            this.isEditing = !this.isEditing;
            this.updateInputFields();
            return true;
        }, "button.edit"));

        ComponentInputField[] fields = this.inputFields.toArray(new ComponentInputField[0]);
        this.selector = new ComponentFormatSelector(this, this.width / 2 + 42, this.height - 16, fields);
        this.components.add(this.selector);

        this.updateInputFields();
    }

    @Override
    public boolean onKeyPressed(IGameInstance game, int button) {
        if (button == GLFW.GLFW_KEY_UP) {
            for (int i = 0; i < this.inputFields.size(); i++) {
                ComponentInputField field = this.inputFields.get(i);
                if (field.isSelected()) {
                    int nextIndex = i - 1;

                    if (nextIndex < 0) {
                        nextIndex = this.inputFields.size() - 1;
                    }

                    field.setSelected(false);
                    this.inputFields.get(nextIndex).setSelected(true);
                    break;
                }
            }
            return true;
        } else if (button == GLFW.GLFW_KEY_DOWN || button == GLFW.GLFW_KEY_ENTER || button == GLFW.GLFW_KEY_TAB) {
            for (int i = 0; i < this.inputFields.size(); i++) {
                ComponentInputField field = this.inputFields.get(i);
                if (field.isSelected()) {
                    int nextIndex = i + 1;

                    if (nextIndex >= this.inputFields.size()) {
                        nextIndex = 0;
                    }

                    field.setSelected(false);
                    this.inputFields.get(nextIndex).setSelected(true);
                    break;
                }
            }
            return true;
        } else {
            return super.onKeyPressed(game, button);
        }
    }

    private void updateInputFields() {
        for (ComponentInputField field : this.inputFields) {
            field.setActive(this.isEditing);
        }
        this.selector.setActive(this.isEditing);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        drawSign(manager, this.tile.text, !this.isEditing, this.x, this.y);
        super.render(game, manager, g);
    }

    @Override
    public boolean canCloseWithInvKey() {
        return true;
    }

    @Override
    public void onClosed(IGameInstance game) {
        super.onClosed(game);
        this.tile.world.setDirty(this.tile.x, this.tile.y);

        if (this.tile.world.isServer()) {
            this.tile.sendToClients();
        } else if (this.tile.world.isClient()) {
            RockBottomAPI.getNet().sendToServer(new PacketSignText(this.tile.x, this.tile.y, this.tile.text));
        }
    }
}
