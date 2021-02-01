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
		simpleFurnace = getPlayerRecipe(GameContent.Tiles.SIMPLE_FURNACE.getName());
		chest = getPlayerRecipe(GameContent.Tiles.CHEST.getName());
		constructionTable = getPlayerRecipe(GameContent.Tiles.CONSTRUCTION_TABLE.getName());
		smithingTable = getPlayerRecipe(GameContent.Tiles.SMITHING_TABLE.getName());
		mortar = getPlayerRecipe(GameContent.Tiles.MORTAR.getName());
	}

	public static PlayerCompendiumRecipe getPlayerRecipe(ResourceName name) {
		return (PlayerCompendiumRecipe)ICompendiumRecipe.forName(name);
	}
}
