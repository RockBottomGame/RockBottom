package de.ellpeck.rockbottom.construction;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public class RecipeCache {

	public static ICompendiumRecipe simpleFurnace;
	public static ICompendiumRecipe chest;
	public static ICompendiumRecipe constructionTable;
	public static ICompendiumRecipe mortar;


	public static void postInit() {
		simpleFurnace = getRecipe(GameContent.TILE_SIMPLE_FURNACE.getName());
		chest = getRecipe(GameContent.TILE_CHEST.getName());
		constructionTable = getRecipe(GameContent.TILE_CONSTRUCTION_TABLE.getName());
		mortar = getRecipe(GameContent.TILE_MORTAR.getName());
	}

	private static ICompendiumRecipe getRecipe(ResourceName name) {
		return ICompendiumRecipe.forName(name);
	}
}
