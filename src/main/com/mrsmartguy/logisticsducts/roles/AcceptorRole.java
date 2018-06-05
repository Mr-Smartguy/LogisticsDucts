package com.mrsmartguy.logisticsducts.roles;

import java.util.List;
import java.util.Map;

import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;

import cofh.thermaldynamics.duct.item.DuctUnitItem;
import cofh.thermaldynamics.multiblock.Route;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class AcceptorRole extends LogisticsRole {

	@Override
	public void performRole(LogisticatorItem logisticator, Map<LogisticatorItem, Route> network) {
		// Acceptors have no active role.
	}

	@Override
	public int requestItems(LogisticatorItem logisticator, Route route, ItemStack items) {
		// Acceptors do not request items.
		return 0;
	}

	@Override
	public int acceptsItems(LogisticatorItem logisticator, ItemStack items) {
		// Acceptors will accept any items that pass the given filter that the attached inventory has space for
		
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
	public List<ItemStack> getProvidedItems(LogisticatorItem logisticator) {
		// Acceptors do not provide items.
		return null;
	}

}