package de.ellpeck.rockbottom.api.assets;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.font.Font;
import de.ellpeck.rockbottom.api.assets.local.Locale;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;

import java.io.InputStream;
import java.util.Map;

public interface IAssetManager{

    void reloadCursor(IGameInstance game);

    Map<String, IAsset> getAllOfType(Class<? extends IAsset> type);

    <T> T getAssetWithFallback(String path, IAsset<T> fallback);

    Image getImage(String path);

    Sound getSound(String path);

    Locale getLocale(String path);

    Font getFont(String path);

    String localize(String unloc, Object... format);

    Font getFont();

    InputStream getResourceStream(String s);
}
