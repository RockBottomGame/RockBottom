package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.construction.compendium.PlayerCompendiumRecipe;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class RecipeCache {

	public static PlayerCompendiumRecipe simpleFurnace;
	public static PlayerCompendiumRecipe chest;
	public static PlayerCompendiumRecipe constructionTable;
	public static PlayerCompendiumRecipe smithingTable;
	public static PlayerCompendiumRecipe mortar;

	public static void postInit() {
		simpleFurnace = getPlayerRecipe(GameContent.TILE_SIMPLE_FURNACE.getName());
		chest = getPlayerRecipe(GameContent.TILE_CHEST.getName());
		constructionTable = getPlayerRecipe(GameContent.TILE_CONSTRUCTION_TABLE.getName());
		smithingTable = getPlayerRecipe(GameContent.TILE_SMITHING_TABLE.getName());
		mortar = getPlayerRecipe(GameContent.TILE_MORTAR.getName());
	}

	public static PlayerCompendiumRecipe getPlayerRecipe(ResourceName name) {
		return (PlayerCompendiumRecipe)ICompendiumRecipe.forName(name);
	}
}
