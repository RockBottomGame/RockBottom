package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.Arrays;
import java.util.List;

public class StartNoteGui extends Gui {

    private static final ResourceName RES = ResourceName.intern("gui.start_note");
    private static final ResourceName DAY_RES = ResourceName.intern("lore.start_note.day");

    private final int variation;
    private final int[] maxLengths;

    public StartNoteGui(int variation) {
        super(36 * 3, 48 * 3);
        this.variation = variation;

        this.maxLengths = new int[25];
        Arrays.fill(this.maxLengths, 0, 17, this.width - 17);
        Arrays.fill(this.maxLengths, 17, 25, this.width / 2);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        super.render(game, manager, g);

        ITexture tex = manager.getTexture(RES);
        tex.draw(this.x, this.y, this.width, this.height);

        IFont font = manager.getFont();

        String day = manager.localize(DAY_RES);
        font.drawString(this.x + this.width / 2 - font.getWidth(day, 0.4F) / 2, this.y + 5, day, 0, day.length(), 0.4F, Colors.BLACK, Colors.NO_COLOR);

        String text = manager.localize(ResourceName.intern("lore.start_note." + (this.variation + 1)));
        List<String> split = font.splitTextToLength(this.maxLengths, 0.2F, true, text);

        int y = 0;
        for (String s : split) {
            font.drawString(this.x + 7, this.y + 18 + y, s, 0, s.length(), 0.2F, Colors.BLACK, Colors.NO_COLOR);
            y += 5;
        }
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("start_note");
    }

    @Override
    public boolean canCloseWithInvKey() {
        return true;
    }

    @Override
    public boolean doesPauseGame() {
        return false;
    }
}
