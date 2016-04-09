package net.blay09.mods.cookingforblockheads.registry;

public enum RecipeType {
	CRAFTING,
	SMELTING;

	private static RecipeType[] values = values();

	public static RecipeType fromId(int i) {
		return values[i];
	}
}
