package de.ellpeck.rockbottom.api.assets;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.font.Font;
import de.ellpeck.rockbottom.api.assets.local.Locale;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;

import java.io.InputStream;
import java.util.Map;

public interface IAssetManager{

    void reloadCursor(IGameInstance game);

    void addAssetProp(IMod mod, String path);

    Map<IResourceName, IAsset> getAllOfType(Class<? extends IAsset> type);

    <T> T getAssetWithFallback(IResourceName path, IAsset<T> fallback);

    Image getImage(IResourceName path);

    Sound getSound(IResourceName path);

    Locale getLocale(IResourceName path);

    Font getFont(IResourceName path);

    String localize(IResourceName unloc, Object... format);

    Font getFont();

    InputStream getResourceStream(String s);
}
