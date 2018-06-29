package com.mrsmartguy.logisticsducts.crafting;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.mrsmartguy.logisticsducts.ducts.attachments.ILogisticator;
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
	 * @param providedItems A mapping of provided items to the logisticator that provides them (the contents will be modified!)
	 * @return The generated request, or null if the given product cannot be crafted with the given recipes
	 */
	public static CraftingRequest createRequest(ItemStack requestedProduct, Map<ContainerRecipe, ILogisticator> recipes, Map<ItemStack, ILogisticator> providedItems)
	{		
		
		List<ItemStack> insufficient = new ArrayList<ItemStack>();
		List<ItemStack> providedSorted = new ArrayList<ItemStack>(providedItems.keySet());
		providedSorted.sort(LDItemHelper.itemComparator);
		
		CraftingTree newTree = constructTree(requestedProduct, recipes, providedItems, providedSorted, insufficient);
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
	 * @param curProduct The desired product, to be provided or crafted
	 * @param recipes A mapping of recipes to their containing logisticators
	 * @param providedItemsMap A mapping of provided item stacks to logisticators
	 * @param providedItemsSorted A sorted list of provided item stacks (must share objects with providedItemsMap!)
	 * @param insufficient (Output) A list of items of which quantities were insufficient to make the desired products
	 * @return
	 */
	private static CraftingTree constructTree(
			ItemStack curProduct,
			Map<ContainerRecipe, ILogisticator> recipes,
			Map<ItemStack, ILogisticator> providedItemsMap,
			List<ItemStack> providedItemsSorted,
			List<ItemStack> insufficient)
	{
		///////////////////////////////////
		// Attempting to provide product //
		///////////////////////////////////
		
		// Get items that match the product in the provided items
		List<ItemStack> productProvided = LDItemHelper.getAllThatMatch(curProduct, providedItemsSorted, false, false);
		
		// Keep track of the number of product provided
		int numProductProvided = 0;
		
		// Create a list to keep track of all request operations
		ArrayList<CraftingTree> requestChildren = new ArrayList<CraftingTree>();
		
		// Iterate over all matches and create operations to request them
		for (ItemStack curProductProvided : productProvided)
		{
			// Determine the amount to request
			// This should not be greater than the amount of product left to provide
			// And should also not be greater than the stack size of the current match
			int curRequestAmount = Math.min(curProduct.getCount() - numProductProvided, curProductProvided.getCount());
			// Create a copy of the provided stack and set its count to the amount to request
			ItemStack curRequest = curProductProvided.copy();
			curRequest.setCount(curRequestAmount);
			// Create the operation and tree corresponding to this request
			CraftingOperation curRequestOp = new CraftingOperation(curRequest, providedItemsMap.get(curProductProvided));
			CraftingTree curRequestTree = new CraftingTree(curRequestOp, null);
			requestChildren.add(curRequestTree);
			// Update the amount of product provided
			numProductProvided += curRequestAmount;
			
			// The stack was consumed entirely, remove it from the provided items
			if (curProductProvided.getCount() == curRequestAmount)
			{
				providedItemsMap.remove(curProductProvided);
				providedItemsSorted.remove(curProductProvided);
			}
			// Otherwise reduce the provided stack's size by the amount to request
			else
			{
				curProductProvided.shrink(curRequestAmount);
			}
			
		}
		// If any are provided, adjust their stack sizes and return a tree with as many as can be provided or the desired amount if possible
		if (numProductProvided > 0)
		{
			// Create a copy of the product with the amount that can be provided
			ItemStack providedStack = curProduct.copy();
			providedStack.setCount(Math.min(numProductProvided, curProduct.getCount()));
			// Create a request crafting tree, which has a null logisticator in the root operation
			// and the requests as children operations
			CraftingTree retVal = new CraftingTree(new CraftingOperation(providedStack, null), requestChildren);
			return retVal;
		}
		
		//////////////////////////////
		// Attempt to craft product //
		//////////////////////////////
		
		// TODO make this handle more than one recipe for the same item
		
		// Find the first recipe with a product that equals the desired product 
		Optional<Entry<ContainerRecipe, ILogisticator>> recipeOpt = recipes
				.entrySet()
				.stream()
				.filter(e -> LDItemHelper.itemComparator.compareWithFlags(curProduct, e.getKey().getProduct(), false, false) == 0)
				.findFirst();
		
		// If a recipe was found, check if it can be completed
		if (recipeOpt.isPresent())
		{
			List<CraftingTree> children = new ArrayList<CraftingTree>();
			ContainerRecipe curRecipe = recipeOpt.get().getKey();
			ILogisticator logisticator = recipeOpt.get().getValue();
			List<ItemStack> ingredients = curRecipe.getIngredients();
			Map<Integer, ItemStack> ingredientMap = curRecipe.getIngredientMap();
			int recipeQuantity = roundUpDivide(curProduct.getCount(), curRecipe.getProduct().getCount());
			
			boolean allIngredientsFound = true;
			
			for (ItemStack ingredient : ingredients)
			{
				// Don't try to craft nothing
				if (ingredient.isEmpty()) continue;
				// Scale up the ingredient count by the recipe quantity
				ingredient = ingredient.copy();
				ingredient.setCount(ingredient.getCount() * recipeQuantity);
				
				// Attempt to create a crafting tree for the current ingredient
				CraftingTree ingredientTree = constructTree(ingredient, recipes, providedItemsMap, providedItemsSorted, insufficient);
				if (ingredientTree != null)
				{
					children.add(ingredientTree);
					// Less than the desired amount of ingredient was returned, this means we need another tree
					// for crafting the remaining ingredients
					if (ingredientTree.operation.getTotalProductCount() < curProduct.getCount())
					{
						// Make a copy of the current ingredient and set the count to the remaining amount needed
						ItemStack ingredientRemaining = ingredient.copy();
						ingredientRemaining.shrink(ingredientTree.operation.getTotalProductCount());
						// Attempt to make another crafting tree to craft the remaining ingredient needed
						CraftingTree ingredientTreeCraft = constructTree(ingredient, recipes, providedItemsMap, providedItemsSorted, insufficient);
						if (ingredientTreeCraft != null &&
								ingredientTreeCraft.operation.getTotalProductCount() >= ingredientRemaining.getCount())
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
				return new CraftingTree(new CraftingOperation(curProduct, ingredientMap, recipeQuantity, logisticator), children);
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
		
		return curNode.operation;
	}
	
	/**
	 * Returns a list of all request operations in this tree.
	 * @return A list of all request operations in this tree
	 */
	public List<CraftingOperation> getRequestOperations()
	{
		ArrayList<CraftingOperation> requestOperations = new ArrayList<CraftingOperation>();
		
		getRequestOperationsHelper(treeRoot, requestOperations);
		
		return requestOperations;
	}
	
	/**
	 * Internal method for traversing tree to find request operations
	 * @param curNode The current node of the tree
	 * @param operations List to add any found operations too
	 */
	private void getRequestOperationsHelper(CraftingTree curNode, List<CraftingOperation> operations)
	{
		if (curNode.operation.isRequest())
		{
			operations.add(curNode.operation);
		}
		else
		{
			for (CraftingTree curChild : curNode.getChildren())
			{
				getRequestOperationsHelper(curChild, operations);
			}
		}
	}
	
	/**
	 * Removes the current crafting operation (the first terminal node encountered in a DFS of the crafting tree)
	 */
	public void completeCurrentOperation()
	{
		//operations.remove(0);
	}
	
}
