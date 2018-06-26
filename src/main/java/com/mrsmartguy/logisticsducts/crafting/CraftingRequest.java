package com.mrsmartguy.logisticsducts.crafting;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
		
		List<ItemStack> insufficient = new ArrayList<ItemStack>();
		CraftingTree newTree = constructTree(requestedProduct, recipes, providedItemsSorted, insufficient);
		if (newTree != null)
			return new CraftingRequest(newTree);
		else
			return null;
	}
	
	/**
	 * Returns the integer result of a divided by b, rounded up
	 * @param a The numerator
	 * @param b The denominator
	 * @return Ceil(a/b)
	 */
	private static int roundUpDivide(int a, int b)
	{
		return (a + b - 1) / b;
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
			List<ItemStack> insufficient)
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
		
		// TODO make this handle more than one recipe for the same item
		// Find the first recipe with a product that equals the desired product 
		Optional<ContainerRecipe> recipeOpt = recipes
				.stream()
				.filter(x -> LDItemHelper.itemComparator.compareWithFlags(curProduct, x.getProduct(), false, false) == 0)
				.findFirst();
		
		if (recipeOpt.isPresent())
		{
			List<CraftingTree> children = new ArrayList<CraftingTree>();
			ContainerRecipe curRecipe = recipeOpt.get();
			List<ItemStack> ingredients = curRecipe.getIngredients();
			Map<Integer, ItemStack> ingredientMap = curRecipe.getIngredientMap();
			int recipeQuantity = roundUpDivide(curProduct.getCount(), curRecipe.getProduct().getCount());
			
			boolean allIngredientsFound = true;
			
			for (ItemStack ingredient : ingredients)
			{
				// Scale up the ingredient count by the recipe quantity
				ingredient = ingredient.copy();
				ingredient.setCount(ingredient.getCount() * recipeQuantity);
				
				// Attempt to create a crafting tree for the current ingredient
				CraftingTree ingredientTree = constructTree(ingredient, recipes, providedItemsSorted, insufficient);
				if (ingredientTree != null)
				{
					children.add(ingredientTree);
					// Less than the desired amount of ingredient was returned, this means we need another tree
					// for crafting the remaining ingredients
					if (ingredientTree.getOperation().getProduct().getCount() < curProduct.getCount())
					{
						// Make a copy of the current ingredient and set the count to the remaining amount needed
						ItemStack ingredientRemaining = ingredient.copy();
						ingredientRemaining.shrink(ingredientTree.getOperation().getProduct().getCount());
						// Attempt to make another crafting tree to craft the remaining ingredient needed
						CraftingTree ingredientTreeCraft = constructTree(ingredient, recipes, providedItemsSorted, insufficient);
						if (ingredientTreeCraft != null &&
								ingredientTreeCraft.getOperation().getProduct().getCount() >= ingredientRemaining.getCount())
						{
							children.add(ingredientTreeCraft);
						}
						else
						{
							// No valid ingredient crafting tree can be made for the remaining quantity, exit the loop
							allIngredientsFound = false;
							break;
						}
					}
				}
				// No valid ingredient crafting tree can be made, exit the loop
				else
				{
					allIngredientsFound = false;
					break;
				}
			}
			
			// All ingredients are accounted for, create and return the resultant crafting tree
			if (allIngredientsFound)
			{
				return new CraftingTree(new CraftingOperation(curProduct, ingredientMap, recipeQuantity), children);
			}
		}
		
		// No product provided and no recipe found, add insufficient ingredient and return null
		insufficient.add(curProduct);
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
