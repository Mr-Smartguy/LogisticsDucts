package com.mrsmartguy.logisticsducts.roles;

import java.util.List;
import java.util.Map;

import com.mrsmartguy.logisticsducts.ducts.attachments.ILogisticator;
import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;

import cofh.thermaldynamics.duct.attachments.filter.FilterLogic;
import cofh.thermaldynamics.multiblock.Route;
import net.minecraft.item.ItemStack;

public class RoleCrafter extends LogisticsRole {

	@Override
	public String getName() {
		return "crafter";
	}
	
	// Crafters should only have single item stacks in the filter (recipes)
	@Override
	public boolean filterHasStackSize() { return false; }

	@Override
	public void performRole(LogisticatorItem logisticator, FilterLogic filter, Map<ILogisticator, Route> network) {
		// TODO Auto-generated method stub

	}

	@Override
	public int requestItems(LogisticatorItem logisticator, FilterLogic filter, Map<ILogisticator, Route> network, Route route, ItemStack items) {
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

}
