package com.mrsmartguy.logisticsducts.roles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mrsmartguy.logisticsducts.ducts.attachments.ILogisticator;
import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;
import com.mrsmartguy.logisticsducts.network.LogisticsDestination;
import com.mrsmartguy.logisticsducts.network.LogisticsNetwork;

import cofh.thermaldynamics.duct.attachments.filter.FilterLogic;
import cofh.thermaldynamics.duct.item.DuctUnitItem;
import cofh.thermaldynamics.duct.item.TravelingItem;
import cofh.thermaldynamics.multiblock.IGridTileRoute;
import cofh.thermaldynamics.multiblock.Route;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class RoleExtractor extends LogisticsRole {

	@Override
	public String getName() {
		return "extractor";
	}
	
	// Extractors should only have single item stacks in the filter (extracted items)
	@Override
	public boolean filterHasStackSize() { return false; }

	@Override
	public void performRole(LogisticatorItem logisticator, FilterLogic filter, LogisticsNetwork network) {
		// Extractors attempt to send the contents of the attached inventory
		// to an appropriate acceptor in the logistics network
		
		ItemStack stack = null;
		int slot = -1;
		
		// Get the cache attached to the logisticator
		DuctUnitItem.Cache cache = logisticator.itemDuct.tileCache[logisticator.side];
		if (cache == null) return;
		
		// Get the handler for the side of the inventory attached to the duct
		IItemHandler handler = cache.getItemHandler(logisticator.side ^ 1);
		if (handler == null) return;
		
		// Find an item stack to pull from the inventory
		for (int i = 0; i < handler.getSlots(); i++)
		{			
			// Simulate pulling out of the inventory to see what ItemStack would be pulled
			ItemStack curStack = handler.extractItem(i, filter.getLevel(FilterLogic.levelStackSize), true);
			// Check if the stack is empty, nonzero size and passes the associated filter
			if (!curStack.isEmpty() && curStack.getCount() > 0 && filter.matchesFilter(curStack)) {
				stack = curStack;
				slot = i;
				break;
			}
		}
		
		// If a stack was found, extract it
		if (stack != null)
		{
			if (network != null)
			{
				for (ILogisticator endpoint : network.getEndpoints())
				{
					int numAccepted = endpoint.acceptsItems(stack);
					if (numAccepted > 0)
					{
						// Pull the item stack out of the inventory
						ItemStack extracted = handler.extractItem(slot, numAccepted, false);
						// Update the remaining number of items to be extracted
						stack.shrink(numAccepted);
						
						//TODO pull out more if applicable
	
						if (extracted.isEmpty() || extracted.getCount() == 0) {
							return;
						}
						
						Route route = logisticator.createRoute(network, endpoint);
						if (route != null)
						{
							TravelingItem traveling = new TravelingItem(extracted, logisticator.itemDuct, route, (byte) (logisticator.side ^ 1), logisticator.getSpeed());
							
							logisticator.itemDuct.insertNewItem(traveling);
							logisticator.addPendingItem(traveling);
							
							if (stack.getCount() == 0) break;
						}
					}
				}
			}
		}
	}

	@Override
	public int requestItems(LogisticatorItem logisticator, FilterLogic filter, LogisticsNetwork network, ILogisticator target, ItemStack items, boolean ignoreMeta, boolean ignoreNBT) {
		// Extractors do not provide items.
		return 0;
	}

	@Override
	public int acceptsItems(LogisticatorItem logisticator, FilterLogic filter, ItemStack items) {
		// Extractors do not accept items.
		return 0;
	}

	@Override
	public List<ItemStack> getProvidedItems(LogisticatorItem logisticator, FilterLogic filter) {
		// Extractors do not provide items.
		return null;
	}

	@Override
	public List<ItemStack> getCraftedItems(LogisticatorItem logisticator, FilterLogic filter) {
		// Extractors do not craft items.
		return null;
	}
	
	@Override
	public void updateCaches(LogisticatorItem logisticator, FilterLogic filter)
	{
		// Extractors do not have any data to cache.
	}

}
