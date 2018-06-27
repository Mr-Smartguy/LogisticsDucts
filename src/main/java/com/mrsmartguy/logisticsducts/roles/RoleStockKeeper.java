package com.mrsmartguy.logisticsducts.roles;

import java.util.ArrayList;
import java.util.Arrays;
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

public class RoleStockKeeper extends LogisticsRole {

	@Override
	public String getName() {
		return "stockkeeper";
	}
	
	// Stock keepers do not use the blacklist feature
	@Override
	public boolean guiHasBlacklistButton() { return false; }

	@Override
	public void performRole(LogisticatorItem logisticator, FilterLogic filter, LogisticsNetwork network) {
		
		// Make sure we don't request items if the logisticator is already stuffed
		if (logisticator.isStuffed()) return;
		
		// Get the cache attached to the logisticator
		DuctUnitItem.Cache cache = logisticator.itemDuct.tileCache[logisticator.side];
		if (cache == null) return;
		
		// Get the handler for the side of the inventory attached to the duct
		IItemHandler handler = cache.getItemHandler(logisticator.side ^ 1);
		if (handler == null) return;
		
		// Get stacks in attached inventory and sort them
		List<ItemStack> invSorted = new ArrayList<ItemStack>(handler.getSlots());
		for (int i = 0; i < handler.getSlots(); i++)
		{
			invSorted.add(handler.getStackInSlot(i));
		}
		invSorted.sort(LDItemHelper.itemComparator);
		
		// Sort filter stacks
		List<ItemStack> filterSorted = new ArrayList<ItemStack>(Arrays.asList(filter.getFilterStacks()));
		filterSorted.sort(LDItemHelper.itemComparator);
		
		List<ItemStack> travellingSorted = logisticator.getTravelingItemsSorted();
		
		boolean ignoreMeta = filter.getFlag(FilterLogicConstants.flagIgnoreMetadata);
		boolean ignoreNBT = filter.getFlag(FilterLogicConstants.flagIgnoreNBT);
		
		// Get intersection of inventory items and filtered items
		Map<ItemStack, List<ItemStack>> curStockedItems = LDItemHelper.findElementsInSorted(filterSorted, invSorted, ignoreMeta, ignoreNBT);
		// Get intersection of inventory items and incoming items
		Map<ItemStack, List<ItemStack>> pendingStockedItems = LDItemHelper.findElementsInSorted(filterSorted, travellingSorted, ignoreMeta, ignoreNBT);
		
		
		// Check if any items in the filter need to be restocked
		for (Entry<ItemStack, List<ItemStack>> stockedEntry : curStockedItems.entrySet())
		{
			// Get desired and actual item count for the given entry
			ItemStack filterStack = stockedEntry.getKey();
			int targetStocked = filterStack.getCount();
			int actualStocked = stockedEntry.getValue()
					.stream()
					.mapToInt(stack -> stack.getCount())
					.sum();
			
			// Get pending items that match current filter items
			List<ItemStack> curPendingStocked = pendingStockedItems.get(filterStack);
			if (curPendingStocked != null)
			{
				actualStocked += curPendingStocked
						.stream()
						.mapToInt(stack -> stack.getCount())
						.sum();
			}
			
			// Request items if the stocked amount is less than the desired amount
			if (actualStocked < targetStocked)
			{
				// Request items from any logisticators on the network
				for (ILogisticator target : network.getEndpoints())
				{
					// Ensure the logisticator doesn't request from itself
					if (target == logisticator) continue;
					
					// Get the logisticator's provided items
					List<ItemStack> provided = target.getProvidedItems();
					
					if (provided == null)
						continue;
					
					// Calculate request size
					final ItemStack curRequestWithSize = filterStack.copy();
					curRequestWithSize.setCount(Math.min(filter.getLevel(FilterLogic.levelStackSize), targetStocked - actualStocked));
					
					// Check if requested item is present in this provider and request the calculated amount if so
					Optional<ItemStack> opt = provided
							.stream()
							.filter(stack -> 0 == LDItemHelper.itemComparator.compareWithFlags(stack, curRequestWithSize, ignoreMeta, ignoreMeta))
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
