package de.ellpeck.rockbottom.api.mod;

import de.ellpeck.rockbottom.api.IApiHandler;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.event.IEventHandler;

/**
 * This is the class that describes a mod main class
 * <br> To make a mod, make any non-final class with a default constructor extend it.
 * The mod loader will then find it inside of the compiled mod jar and add it to the list of mods
 */
public interface IMod{

    /**
     * @return The display name of the mod
     */
    String getDisplayName();

    /**
     * @return The mod's unique string id. Needs to be all lowercase and cannot contain spaces
     */
    String getId();

    /**
     * @return The current version of the mod
     */
    String getVersion();

    /**
     * Returns the location in the jar file that mod-related {@link de.ellpeck.rockbottom.api.assets.IAsset}s
     * are stored (images, sounds, language files etc.)
     * <br> The location must contain a file named "assets.info" containg a mapping
     * of all the asset names to their locations in the jar file
     * <br>
     * <br> For instance, RockBottom uses the resource location "/assets/rockbottom", meaning
     * the "assets.info" file has to be at "/assets/rockbottom/assets.info". For the game to then load a
     * png resource at "/assets/rockbottom/example/test.png" with the resource name "test", the assets.info
     * file must contain the following line: "tex.text=/example/test.png"
     *
     * @return The location
     */
    String getResourceLocation();

    /**
     * @return The mod's description to be displayed in the mod info {@link de.ellpeck.rockbottom.api.gui.Gui}
     * on the title screen
     */
    default String getDescription(){
        return "";
    }

    /**
     * Returns the priority with which this mod will be sorted in the list of mods
     * <br>Higher values mean that it will be closer to the start of the list
     * <br>RockBottom has a priority of {@link Integer#MAX_VALUE} to always be loaded first
     *
     * @return The priority
     */
    default int getSortingPriority(){
        return 0;
    }

    /**
     * Override this method to do any actions that should happen before the initialization of the base game
     *
     * @param game         The current game instance
     * @param apiHandler   The API handler
     * @param eventHandler The event handler
     */
    default void preInit(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler){

    }

    /**
     * Override this method to do any actions that should happen during the initialization of the base game
     *
     * @param game         The current game instance
     * @param assetManager The asset manager
     * @param apiHandler   The API handler
     * @param eventHandler The event handler
     */
    default void init(IGameInstance game, IAssetManager assetManager, IApiHandler apiHandler, IEventHandler eventHandler){

    }

    /**
     * Override this method to do any actions that should happen after the initialization of the base game
     *
     * @param game         The current game instance
     * @param assetManager The asset manager
     * @param apiHandler   The API handler
     * @param eventHandler The event handler
     */
    default void postInit(IGameInstance game, IAssetManager assetManager, IApiHandler apiHandler, IEventHandler eventHandler){

    }
}
