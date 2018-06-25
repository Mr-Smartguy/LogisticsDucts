package com.mrsmartguy.logisticsducts.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;

/**
 * Represents a single crafting operation.
 * Stores the ingredients as well as the product of the operation.
 */
public class CraftingOperation {
	private ItemStack product;
	private Map<Integer, ItemStack> posIngredientMap;
	private List<ItemStack> ingredientList;
	
	/**
	 * Constructs a crafting operation.
	 * @param product The produced item stack for this operation's recipe.
	 * @param ingredients A mapping of crafting position to item
	 */
	public CraftingOperation(ItemStack product, Map<Integer, ItemStack> ingredients)
	{
		this.product = product.copy();
		this.posIngredientMap = new LinkedHashMap<Integer, ItemStack>(ingredients);
		this.ingredientList = posIngredientMap.values()
				.stream()
				.filter(x -> x != null)
				.collect(Collectors.toCollection(ArrayList::new));
	}
	
	/**
	 * Constructs a crafting operation with no recipe (meaning the items exist in the logistics system already).
	 * @param product The "produced" item stack for this operation.
	 */
	public CraftingOperation(ItemStack product)
	{
		this.product = product.copy();
		this.posIngredientMap = null;
		this.ingredientList = null;
	}
	
	public ItemStack getProduct()
	{
		return product;
	}
	
	public ItemStack getIngredientAtPos(int pos)
	{
		if (posIngredientMap == null)
			return null;
		return posIngredientMap.get(pos);
	}
	
	public List<ItemStack> getAllIngredients()
	{
		if (ingredientList == null)
			return null;
		return Collections.unmodifiableList(ingredientList);
	}
	
	public int getNumIngredients()
	{
		if (ingredientList == null)
			return 0;
		return ingredientList.size();
	}
	
	/**
	 * Returns whether the product of this operation can already be retrieved from the logistics system.
	 * @return True if it can, false otherwise
	 */
	public boolean productInSystem()
	{
		return posIngredientMap == null;
	}
}
