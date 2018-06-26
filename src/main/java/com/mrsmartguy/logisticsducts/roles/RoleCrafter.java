package com.mrsmartguy.logisticsducts.roles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.mrsmartguy.logisticsducts.ducts.attachments.ILogisticator;
import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;
import com.mrsmartguy.logisticsducts.gui.container.ContainerRecipe;
import com.mrsmartguy.logisticsducts.items.ItemLogisticsRecipe;

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
	public void performRole(LogisticatorItem logisticator, FilterLogic filter, Map<ILogisticator, Route> network) {
		// TODO Crafter role
		// Needs to process pending crafts and update the status of their pending items if needed
		// as well as send crafted items along the ducts to their destinations
		
	}

	@Override
	public int requestItems(LogisticatorItem logisticator, FilterLogic filter, IGridTileRoute target, byte finalDir, ItemStack items, boolean ignoreMeta, boolean ignoreNBT) {
		// Crafters do not provide items (crafters must explicitly be asked to craft)
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
	public void updateCaches(LogisticatorItem logisticator, FilterLogic filter)
	{
		// Check item stacks and look for recipes
		for (ItemStack stack : filter.getFilterStacks())
		{
			if (stack.getItem() instanceof ItemLogisticsRecipe)
			{
				ContainerRecipe recipe = new ContainerRecipe(stack, null);
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
