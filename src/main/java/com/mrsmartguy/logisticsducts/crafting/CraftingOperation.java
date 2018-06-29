package com.mrsmartguy.logisticsducts.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mrsmartguy.logisticsducts.ducts.attachments.ILogisticator;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

/**
 * Represents a single crafting operation.
 * Stores the ingredients as well as the product of the operation.
 */
public class CraftingOperation {
	private ItemStack product;
	private Map<Integer, ItemStack> posIngredientMap;
	private List<ItemStack> ingredientList;
	public final int recipeQuantity;
	public final ILogisticator logisticator; 
	
	/**
	 * Constructs a crafting operation.
	 * @param product The produced item stack for this operation's recipe
	 * @param ingredients A mapping of crafting position to item
	 * @param recipeQuantity The number of times the recipe is to be crafted
	 * @param logisticator The logisticator that will perform this operation (crafting)
	 */
	public CraftingOperation(ItemStack product, Map<Integer, ItemStack> ingredients, int recipeQuantity, ILogisticator logisticator)
	{
		this.product = product.copy();
		this.posIngredientMap = new LinkedHashMap<Integer, ItemStack>(ingredients);
		this.ingredientList = posIngredientMap.values()
				.stream()
				.filter(x -> x != null)
				.collect(Collectors.toCollection(ArrayList::new));
		this.recipeQuantity = recipeQuantity;
		this.logisticator = logisticator;
	}
	
	/**
	 * Constructs a crafting operation with no recipe (meaning the items exist in the logistics system already).
	 * @param product The "produced" item stack for this operation.
	 * @param logisticator The logisticator that will perform this operation (providing)
	 */
	public CraftingOperation(ItemStack product, ILogisticator logisticator)
	{
		this.product = product.copy();
		this.posIngredientMap = null;
		this.ingredientList = null;
		this.recipeQuantity = 1;
		this.logisticator = logisticator;
	}
	
	/**
	 * Constructs an operation to serve as the root node for request operations. The logisticator is null
	 * and the product stack is the sum of all provided stacks from the children operations.
	 * @param product The "produced" item stack for this operation.
	 */
	public CraftingOperation(ItemStack product)
	{
		this.product = product.copy();
		this.posIngredientMap = null;
		this.ingredientList = null;
		this.recipeQuantity = 0;
		this.logisticator = null;
	}
	
	public ItemStack getProduct()
	{
		return product;
	}
	
	public int getTotalProductCount()
	{
		return product.getCount() * recipeQuantity;
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

	/**
	 * Returns whether this operation is a request operation (as opposed to a crafting operation)
	 * @return True if this operation is a request operation, false otherwise
	 */
	public boolean isRequest() {
		return ingredientList == null && logisticator != null;
	}
}
