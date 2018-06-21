package com.mrsmartguy.logisticsducts.roles;

import java.util.List;
import java.util.Map;

import com.mrsmartguy.logisticsducts.ducts.attachments.ILogisticator;
import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;

import cofh.thermaldynamics.duct.attachments.filter.FilterLogic;
import cofh.thermaldynamics.duct.item.DuctUnitItem;
import cofh.thermaldynamics.multiblock.IGridTileRoute;
import cofh.thermaldynamics.multiblock.Route;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class RoleAcceptor extends LogisticsRole {

	@Override
	public String getName() {
		return "acceptor";
	}
	
	// Acceptors should only have single item stacks in the filter (accepted items)
	@Override
	public boolean filterHasStackSize() { return false; }

	@Override
	public void performRole(LogisticatorItem logisticator, FilterLogic filter, Map<ILogisticator, Route> network) {
		// Acceptors have no active role.
	}

	@Override
	public int requestItems(LogisticatorItem logisticator, FilterLogic filter, IGridTileRoute target, byte finalDir, ItemStack items) {
		// Acceptors do not provide items.
		return 0;
	}

	@Override
	public int acceptsItems(LogisticatorItem logisticator, FilterLogic filter, ItemStack items) {
		// Acceptors will accept any items that pass the given filter that the attached inventory has space for
		
		// Check if the associated filter allows the items to be accepted
		if (!filter.matchesFilter(items))
			return 0;
		
		// Copy the item stack to prevent modifying the original
		items = items.copy();
		
		int numAccepted = 0;		
		int total = items.getCount();
		
		// Get the cache attached to the logisticator
		DuctUnitItem.Cache cache = logisticator.itemDuct.tileCache[logisticator.side];
		if (cache == null) return 0;
		
		// Get the handler for the side of the inventory attached to the duct
		IItemHandler handler = cache.getItemHandler(logisticator.side ^ 1);
		if (handler == null) return 0;
		
		for (int i = 0; i < handler.getSlots(); i++)
		{
			ItemStack curAccepted = handler.insertItem(i, items, true);
			numAccepted += (items.getCount() - curAccepted.getCount());
			
			if (numAccepted >= total)
			{
				numAccepted = total;
				break;
			}
		}
		return numAccepted;
	}

	@Override
	public List<ItemStack> getProvidedItems(LogisticatorItem logisticator, FilterLogic filter) {
		// Acceptors do not provide items.
		return null;
	}

	@Override
	public List<ItemStack> getCraftedItems(LogisticatorItem logisticator, FilterLogic filter) {
		// Acceptors do not craft items.
		return null;
	}

}
