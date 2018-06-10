package com.mrsmartguy.logisticsducts.roles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;

import cofh.thermaldynamics.duct.attachments.filter.FilterLogic;
import cofh.thermaldynamics.duct.item.DuctUnitItem;
import cofh.thermaldynamics.multiblock.Route;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class RoleProvider extends LogisticsRole {

	@Override
	public String getName() {
		return "provider";
	}

	@Override
	public void performRole(LogisticatorItem logisticator, FilterLogic filter, Map<LogisticatorItem, Route> network) {
		// Providers have no active role.
	}

	@Override
	public int requestItems(LogisticatorItem logisticator, FilterLogic filter, Route route, ItemStack items) {
		// TODO implement items being requested.
		return 0;
	}

	@Override
	public int acceptsItems(LogisticatorItem logisticator, FilterLogic filter, ItemStack items) {
		// Providers do not accept any items.
		return 0;
	}

	@Override
	public List<ItemStack> getProvidedItems(LogisticatorItem logisticator, FilterLogic filter) {
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		
		// Get the cache attached to the logisticator
		DuctUnitItem.Cache cache = logisticator.itemDuct.tileCache[logisticator.side];
		if (cache == null) return stacks;
		
		// Get the handler for the side of the inventory attached to the duct
		IItemHandler handler = cache.getItemHandler(logisticator.side ^ 1);
		if (handler == null) return stacks;
		
		// Find any stacks that match the filter
		for (int i = 0; i < handler.getSlots(); i++)
		{
			// Check the contents of this slot
			ItemStack curStack = handler.getStackInSlot(i);
			// Check if the stack is empty, nonzero size and passes the associated filter
			if (!curStack.isEmpty() && curStack.getCount() > 0 && filter.matchesFilter(curStack)) {
				stacks.add(curStack);
			}
		}
		
		return stacks;
	}

}
