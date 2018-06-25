package com.mrsmartguy.logisticsducts.roles;

import java.util.List;
import java.util.Map;

import com.mrsmartguy.logisticsducts.ducts.attachments.ILogisticator;
import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;

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
		// TODO Auto-generated method stub

	}

	@Override
	public int requestItems(LogisticatorItem logisticator, FilterLogic filter, IGridTileRoute target, byte finalDir, ItemStack items, boolean ignoreMeta, boolean ignoreNBT) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int acceptsItems(LogisticatorItem logisticator, FilterLogic filter, ItemStack items) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<ItemStack> getProvidedItems(LogisticatorItem logisticator, FilterLogic filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ItemStack> getCraftedItems(LogisticatorItem logisticator, FilterLogic filter) {
		return null;
	}
	
	@Override
	public void updateCaches(LogisticatorItem logisticator, FilterLogic filter)
	{
		// TODO crafter caching
	}
	
	@Override
	public boolean writeToTag(NBTTagCompound tag) {
		
		
		
		return true;
	}
	
	@Override
	public void readFromTag(NBTTagCompound tag) {
		
	}

}
