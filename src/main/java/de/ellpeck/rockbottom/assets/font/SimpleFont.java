package de.ellpeck.rockbottom.assets.font;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.assets.tex.Texture;
import de.ellpeck.rockbottom.content.ContentManager;

import java.util.HashMap;
import java.util.Map;

public class SimpleFont extends Font {

    public SimpleFont() {
        super("simple_en", getTex(), 23, 3, getChars());
    }

    private static ITexture getTex() {
        try {
            return new Texture(ContentManager.getResourceAsStream("assets/rockbottom/font/simple.png"));
        } catch (Exception e) {
            return RockBottomAPI.getGame().getAssetManager().getMissingTexture();
        }
    }

    private static Map<Character, Pos2> getChars() {
        String[] chars = new String[]{
                "!?,./0123456789:-ABCDEF",
                "GHIJKLMNOPQRSTUVWXYZabc",
                "defghijklmnopqrstuvwxyz"
        };

        Map<Character, Pos2> map = new HashMap<>();
        for (int y = 0; y < 3; y++) {
            String row = chars[y];
            for (int x = 0; x < 23; x++) {
                map.put(row.charAt(x), new Pos2(x, y));
            }
        }
        return map;
    }
}
