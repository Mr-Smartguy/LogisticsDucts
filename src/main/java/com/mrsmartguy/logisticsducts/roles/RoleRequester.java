package com.mrsmartguy.logisticsducts.roles;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.mrsmartguy.logisticsducts.ducts.attachments.FilterLogicConstants;
import com.mrsmartguy.logisticsducts.ducts.attachments.ILogisticator;
import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;
import com.mrsmartguy.logisticsducts.items.LDItemHelper;
import com.mrsmartguy.logisticsducts.network.LogisticsDestination;
import com.mrsmartguy.logisticsducts.network.LogisticsNetwork;

import cofh.thermaldynamics.duct.attachments.filter.FilterLogic;
import cofh.thermaldynamics.duct.item.DuctUnitItem;
import cofh.thermaldynamics.multiblock.IGridTileRoute;
import cofh.thermaldynamics.multiblock.Route;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class RoleRequester extends LogisticsRole {

	@Override
	public String getName() {
		return "requester";
	}
	
	// Requesters should only have single item stacks in the filter (the requested items)
	@Override
	public boolean filterHasStackSize() { return false; }
	
	// Requesters do not use the blacklist feature
	@Override
	public boolean guiHasBlacklistButton() { return false; }

	@Override
	public void performRole(LogisticatorItem logisticator, FilterLogic filter, LogisticsNetwork network) {
		
		boolean ignoreMeta = filter.getFlag(FilterLogicConstants.flagIgnoreMetadata);
		boolean ignoreNBT = filter.getFlag(FilterLogicConstants.flagIgnoreNBT);
		
		// Make sure we don't request items if the logisticator is already stuffed
		if (logisticator.isStuffed()) return;
		
		// Get the cache attached to the logisticator
		DuctUnitItem.Cache cache = logisticator.itemDuct.tileCache[logisticator.side];
		if (cache == null) return;
		
		// Get the handler for the side of the inventory attached to the duct
		IItemHandler handler = cache.getItemHandler(logisticator.side ^ 1);
		if (handler == null) return;
		
		// Iterate over items in the filter and see if they can be requested
		for (ItemStack curRequest : filter.getFilterStacks())
		{
			// Make sure we don't try to request an empty item
			if (curRequest == null || curRequest.isEmpty())
				continue;
			
			// Calculate the amount of items to request
			final ItemStack curRequestWithSize = curRequest.copy();
			curRequestWithSize.setCount(filter.getLevel(FilterLogic.levelStackSize));
			
			int slotIndex = -1;
			
			// Check if the stack fits in any slots
			for (int i = 0; i < handler.getSlots(); i++)
			{
				// Simulate inserting into the current slot
				ItemStack remainingSimulated = handler.insertItem(i, curRequestWithSize, true);
				
				// Check if any items were inserted in the simulation, if they were use this slot
				if (remainingSimulated.getCount() < curRequestWithSize.getCount())
				{
					slotIndex = i;
					curRequestWithSize.shrink(remainingSimulated.getCount());
				}
			}
			
			// A slot was found, request the items
			if (slotIndex != -1)
			{
				// Request items from any logisticators on the network
				for (ILogisticator target : network.getEndpoints())
				{			
					// Ensure the logisticator doesn't request from itself
					if (target == logisticator) continue;
					
					List<ItemStack> provided = target.getProvidedItems();
					
					// Check if the provider has any items
					Optional<ItemStack> opt = provided
							.stream()
							.filter(stack -> LDItemHelper.itemComparator.compareWithFlags(stack, curRequestWithSize, ignoreMeta, ignoreNBT) == 0)
							.findFirst();
					
					if (opt.isPresent())
					{
						target.requestItems(network, logisticator, curRequestWithSize, ignoreMeta, ignoreNBT);
						return;
					}
				}
			}
		}
	}

	@Override
	public int requestItems(LogisticatorItem logisticator, FilterLogic filter, LogisticsNetwork network, ILogisticator target, ItemStack items, boolean ignoreMeta, boolean ignoreNBT) {
		// Requesters do not provide any items.
		return 0;
	}

	@Override
	public int acceptsItems(LogisticatorItem logisticator, FilterLogic filter, ItemStack items) {
		// Requesters do not accept any items.
		return 0;
	}

	@Override
	public List<ItemStack> getProvidedItems(LogisticatorItem logisticator, FilterLogic filter) {
		// Requesters do not provide any items.
		return null;
	}

	@Override
	public List<ItemStack> getCraftedItems(LogisticatorItem logisticator, FilterLogic filter) {
		// Requesters do not craft any items.
		return null;
	}

	@Override
	public void updateCaches(LogisticatorItem logisticator, FilterLogic filter)
	{
		// Requesters have no data to cache.
	}
}
