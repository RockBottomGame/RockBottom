package de.ellpeck.rockbottom.api;

import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.entity.player.IInteractionManager;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.particle.IParticleManager;
import de.ellpeck.rockbottom.api.util.IAction;
import de.ellpeck.rockbottom.api.util.reg.NameToIndexInfo;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import org.newdawn.slick.GameContainer;

import java.io.File;
import java.net.URLClassLoader;
import java.util.UUID;

/**
 * The current instance of the game
 * <br> Use this to access important game functions and variables
 */
public interface IGameInstance extends IMod{

    /**
     * @return {@code true} if the game is currently in a world,
     * {@code false} if on the title or loading screen
     */
    boolean isInWorld();

    /**
     * Starts up a {@link IWorld} from a specified {@link File} and {@link WorldInfo}
     * and puts the player into it
     * <br> Not supposed to be used by mods
     *
     * @param worldFile The world's directory
     * @param info      The world info
     */
    void startWorld(File worldFile, WorldInfo info);

    /**
     * Joins a {@link IWorld} with a specified {@link DataSet} storing the player information,
     * a {@link WorldInfo} and  a specified {@link NameToIndexInfo} storing the mapping of tiles
     * to their ids on disk.
     * <br> Not supposed to be used by mods
     *
     * @param playerSet   The player data
     * @param info        The world info
     * @param tileRegInfo The tile registry info
     */
    void joinWorld(DataSet playerSet, WorldInfo info, NameToIndexInfo tileRegInfo);

    /**
     * Quits the current {@link IWorld}
     * <br> Not supposed to be used by mods
     */
    void quitWorld();

    /**
     * Opens the ingame {@link de.ellpeck.rockbottom.api.gui.Gui}
     * <br> Not supposed to be used by mods
     */
    void openIngameMenu();

    /**
     * Schedules an {@link IAction} to be executed next tick
     * <br> Useful for use with {@link de.ellpeck.rockbottom.api.net.packet.IPacket} as
     * it prevents concurrent modification
     *
     * @param action The action to be executed
     */
    void scheduleAction(IAction action);

    /**
     * @return The current {@link GameContainer}
     */
    GameContainer getContainer();

    /**
     * @return The width of the current visible screen area when applying {@link Settings#renderScale}
     */
    double getWidthInWorld();

    /**
     * @return The height of the current visible screen area when applying {@link Settings#renderScale}
     */
    double getHeightInWorld();

    /**
     * @return The width of the current visible screen area when applying {@link Settings#guiScale}
     */
    double getWidthInGui();

    /**
     * @return The height of the current visible screen area when applying {@link Settings#guiScale}
     */
    double getHeightInGui();

    /**
     * @return The x coordinate of the {@link org.lwjgl.input.Mouse} when applying {@link Settings#guiScale}
     */
    float getMouseInGuiX();

    /**
     * @return The y coordinate of the {@link org.lwjgl.input.Mouse} when applying {@link Settings#guiScale}
     */
    float getMouseInGuiY();

    /**
     * @return The {@link IDataManager} containing the game location and save locations
     */
    IDataManager getDataManager();

    /**
     * @return The {@link Settings} of the game
     */
    Settings getSettings();

    /**
     * @return The current {@link AbstractEntityPlayer} of {@code null} if there is none
     */
    AbstractEntityPlayer getPlayer();

    /**
     * @return The {@link IGuiManager} of the game that can be used to open {@link de.ellpeck.rockbottom.api.gui.Gui}
     */
    IGuiManager getGuiManager();

    /**
     * @return The {@link IInteractionManager} of the game
     */
    IInteractionManager getInteractionManager();

    /**
     * @return The {@link IChatLog} that can be used to send and query chat messages
     */
    IChatLog getChatLog();

    /**
     * @return The current {@link IWorld} of the game or {@code null} if there is none
     */
    IWorld getWorld();

    /**
     * @return The {@link IAssetManager} that can be used to get and modify game assets
     */
    IAssetManager getAssetManager();

    /**
     * @return The {@link IParticleManager} of the game
     */
    IParticleManager getParticleManager();

    /**
     * @return The {@link UUID} of the game and its {@link AbstractEntityPlayer}
     */
    UUID getUniqueId();

    /**
     * @return If the game is in debug mode making the debug menu show
     */
    boolean isDebug();

    /**
     * @return If the game is in light debug mode making all lighting be equally bright
     */
    boolean isLightDebug();

    /**
     * @return If the game is in foreground debug making the foreground layer disappear
     */
    boolean isForegroundDebug();

    /**
     * @return If the game is in background debug making the background layer disappear
     */
    boolean isBackgroundDebug();

    /**
     * @return The average TPS (ticks per second) out of {@link Constants#TARGET_TPS} over the last second
     */
    int getTpsAverage();

    /**
     * @return The average FPS (frames per second) out of {@link Settings#targetFps} over the last second
     */
    int getFpsAverage();

    /**
     * @return The {@link ClassLoader} used to load vanilla game and {@link IMod} classes
     */
    URLClassLoader getClassLoader();
}
