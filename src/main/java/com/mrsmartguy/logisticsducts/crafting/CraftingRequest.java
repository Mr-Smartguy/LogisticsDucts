package com.mrsmartguy.logisticsducts.crafting;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;
import com.mrsmartguy.logisticsducts.gui.container.ContainerRecipe;
import com.mrsmartguy.logisticsducts.items.ItemLogisticsRecipe;
import com.mrsmartguy.logisticsducts.items.LDItemHelper;

import cofh.thermaldynamics.duct.attachments.filter.FilterLogic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Represents one request to a crafter on the logistics network.
 * This includes the list of operations required to complete this request.
 */
public class CraftingRequest {
	
	private CraftingTree treeRoot;
	
	/**
	 * Creates a request for the given product
	 * @param requestedProduct The target product to make, with quantity
	 * @param recipes The list of recipes that the crafter has access to
	 * @param providedItemsSorted The list of items currently available for crafting, sorted (the contents will be modified!)
	 * @return The generated request, or null if the given product cannot be crafted with the given recipes
	 */
	public static CraftingRequest createRequest(ItemStack requestedProduct, List<ContainerRecipe> recipes, List<ItemStack> providedItemsSorted)
	{		
		
		ItemStack insufficient = new ItemStack((Item)null);
		ArrayList<ItemStack> toRetrieve = new ArrayList<ItemStack>();
		CraftingTree newTree = constructTree(requestedProduct, recipes, providedItemsSorted, toRetrieve, insufficient);
		if (newTree != null)
			return new CraftingRequest(newTree);
		else
			return null;
	}
	
	/**
	 * 
	 * @param curProduct
	 * @param recipes
	 * @param providedItemsSorted
	 * @param usedItems
	 * @param insufficient
	 * @return
	 */
	private static CraftingTree constructTree(
			ItemStack curProduct,
			List<ContainerRecipe> recipes,
			List<ItemStack> providedItemsSorted,
			List<ItemStack> usedItems,
			ItemStack insufficient)
	{
		// Get items that match the product in the provided items
		List<ItemStack> productProvided = LDItemHelper.getAllThatMatch(curProduct, providedItemsSorted, false, false);
		// Check how many of the product, if any, are provided
		int numProductProvided = productProvided
				.stream()
				.mapToInt(x -> x.getCount())
				.sum();
		
		// If any are provided, adjust their stack sizes and return a tree with as many as can be provided or the desired amount if possible
		if (numProductProvided > 0)
		{
			ItemStack providedStack = curProduct.copy();
			providedStack.setCount(Math.min(numProductProvided, curProduct.getCount()));
			CraftingTree retVal = new CraftingTree(new CraftingOperation(providedStack), null);
			return retVal;
		}
		// Check if we have a recipe for the product
		Optional<ContainerRecipe> recipeOpt = recipes
				.stream()
				.filter(x -> LDItemHelper.itemComparator.compareWithFlags(curProduct, x.getProduct(), false, false) == 0)
				.findFirst();
		
		
		return null;
	}
	
	private CraftingRequest(CraftingTree treeRoot)
	{
		this.treeRoot = treeRoot;
	}
	
	/**
	 * Finds the current crafting operation being run (the first terminal node encountered in a DFS of the crafting tree)
	 * @return The current crafting operation being run
	 */
	public CraftingOperation getCurrentOperation()
	{
		CraftingTree curNode = treeRoot;
		
		if (curNode == null)
			return null;
		
		while (curNode.hasChildren())
		{
			curNode = curNode.getChildren().get(0);
		}
		
		return curNode.getOperation();
	}
	
	/**
	 * Removes the current crafting operation (the first terminal node encountered in a DFS of the crafting tree)
	 */
	public void completeCurrentOperation()
	{
		//operations.remove(0);
	}
	
}
