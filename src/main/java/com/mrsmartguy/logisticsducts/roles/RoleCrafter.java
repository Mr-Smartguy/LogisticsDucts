package com.mrsmartguy.logisticsducts.roles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mrsmartguy.logisticsducts.crafting.CraftingRequest;
import com.mrsmartguy.logisticsducts.crafting.CraftingTree;
import com.mrsmartguy.logisticsducts.ducts.attachments.ILogisticator;
import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;
import com.mrsmartguy.logisticsducts.gui.container.ContainerRecipe;
import com.mrsmartguy.logisticsducts.items.ItemLogisticsRecipe;
import com.mrsmartguy.logisticsducts.network.LogisticsDestination;
import com.mrsmartguy.logisticsducts.network.LogisticsNetwork;

import cofh.thermaldynamics.duct.attachments.filter.FilterLogic;
import cofh.thermaldynamics.multiblock.IGridTileRoute;
import cofh.thermaldynamics.multiblock.Route;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class RoleCrafter extends LogisticsRole {
	
	// Stores pending crafting operations
	// The outer most list is a list of requests
	// Each request is a list of operations that must be performed in that order
	// Each operation is a mapping of result stack to a list of ingredient stacks
	//private List<List<Map<ItemStack, List<ItemStack>>>> pendingCrafts = new ArrayList<List<Map<ItemStack, List<ItemStack>>>>();
	
	private List<ItemStack> craftedItems = new ArrayList<ItemStack>();
	private List<ContainerRecipe> recipes = new ArrayList<ContainerRecipe>();

	@Override
	public String getName() {
		return "crafter";
	}
	
	// Crafters should only have single item stacks in the filter (recipes)
	@Override
	public boolean filterHasStackSize() { return false; }
	
	// Crafters do not use the blacklist feature
	@Override
	public boolean guiHasBlacklistButton() { return false; }

	@Override
	public void performRole(LogisticatorItem logisticator, FilterLogic filter, LogisticsNetwork network) {
		// TODO Crafter role
		// Needs to process pending crafts and update the status of their pending items if needed
		// as well as send crafted items along the ducts to their destinations
		
	}

	@Override
	public int requestItems(LogisticatorItem logisticator, FilterLogic filter, LogisticsNetwork network, ILogisticator target, ItemStack items, boolean ignoreMeta, boolean ignoreNBT) {
		// Crafters do not provide items (crafters must explicitly be asked to craft)
		return 0;
	}
	
	@Override
	public int craftItems(LogisticatorItem logisticator, FilterLogic filter, LogisticsNetwork network, ILogisticator target, ItemStack items, boolean ignoreMeta, boolean ignoreNBT, boolean completeCraftsOnly) {
		
		Map<ItemStack, ILogisticator> providedItems = new LinkedHashMap<ItemStack, ILogisticator>();
		Map<ContainerRecipe, ILogisticator> recipeMap = new LinkedHashMap<ContainerRecipe, ILogisticator>();
		
		for (ILogisticator curLogisticator : network.getEndpoints())
		{
			// Do not request from self
			if (curLogisticator != logisticator)
			{
				for (ItemStack stack : curLogisticator.getProvidedItems())
					providedItems.put(stack.copy(), curLogisticator);
			}
			for (ContainerRecipe curRecipe : curLogisticator.getRecipes())
				recipeMap.put(curRecipe, curLogisticator);
		}
		// Construct the crafting tree
		CraftingRequest craft = CraftingRequest.createRequest(target, items, recipeMap, providedItems);
		
		// If no tree was returned, the craft is not possible
		if (craft == null)
			return 0;
		
		// Get a list of all provider request operations in the tree
		List<CraftingTree> requestTrees = craft.getRequestSubTrees();
		
		// Move all items from the given providers to their destinations
		for (CraftingTree curRequest : requestTrees)
		{
			// Get the logisticator that provides the current item request
			ILogisticator source = curRequest.operation.logisticator;
			
			// Get the logisticator to send the items to
			ILogisticator dest = curRequest.operation.dest;
			
			for (int i = 0; i < curRequest.operation.recipeQuantity; i++)
				source.requestItems(network, dest, curRequest.operation.getProduct(), false, false);
		}
		// TODO
		return 0;
	}

	@Override
	public int acceptsItems(LogisticatorItem logisticator, FilterLogic filter, ItemStack items) {
		// Crafters do not accept any items
		return 0;
	}

	@Override
	public List<ItemStack> getProvidedItems(LogisticatorItem logisticator, FilterLogic filter) {
		// Crafters do not provide items (crafters must explicitly be asked to craft)
		return null;
	}

	@Override
	public List<ItemStack> getCraftedItems(LogisticatorItem logisticator, FilterLogic filter) {
		// Return the list of items this crafter has recipes for
		return Collections.unmodifiableList(craftedItems);
	}
	
	@Override
	public List<ContainerRecipe> getRecipes()
	{
		return recipes;
	}
	
	@Override
	public void updateCaches(LogisticatorItem logisticator, FilterLogic filter)
	{
		craftedItems.clear();
		recipes.clear();
		
		// Check item stacks and look for recipes
		for (ItemStack stack : filter.getFilterStacks())
		{
			if (stack.getItem() instanceof ItemLogisticsRecipe)
			{
				ContainerRecipe recipe = new ContainerRecipe(stack, null);
				craftedItems.add(recipe.getProduct());
				recipes.add(recipe);
			}
		}
	}
	
	@Override
	public boolean writeToTag(NBTTagCompound tag) {
		// TODO write pending crafts to tag
		return true;
	}
	
	@Override
	public void readFromTag(NBTTagCompound tag) {
		// TODO read pending crafts from tag
	}

}
