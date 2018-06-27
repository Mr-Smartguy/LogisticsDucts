package com.mrsmartguy.logisticsducts.roles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mrsmartguy.logisticsducts.ducts.attachments.ILogisticator;
import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;
import com.mrsmartguy.logisticsducts.items.LDItemHelper;
import com.mrsmartguy.logisticsducts.network.LogisticsDestination;
import com.mrsmartguy.logisticsducts.network.LogisticsNetwork;

import cofh.thermaldynamics.duct.attachments.filter.FilterLogic;
import cofh.thermaldynamics.duct.item.DuctUnitItem;
import cofh.thermaldynamics.duct.item.TravelingItem;
import cofh.thermaldynamics.multiblock.IGridTileRoute;
import cofh.thermaldynamics.multiblock.Route;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import scala.actors.threadpool.Arrays;

public class RoleProvider extends LogisticsRole {
	
	// Map of item stacks to their slot index in the attached inventory
	private Map<ItemStack, Integer> itemSlotMap = new LinkedHashMap<ItemStack, Integer>();
	// Sorted list of item stacks in the attached inventory
	private List<ItemStack> itemsSorted = new ArrayList<ItemStack>();

	@Override
	public String getName() {
		return "provider";
	}
	
	// Providers should only have single item stacks in the filter (provided items)
	@Override
	public boolean filterHasStackSize() { return false; }

	@Override
	public void performRole(LogisticatorItem logisticator, FilterLogic filter, LogisticsNetwork network) {
		// Providers have no active role.
	}

	@Override
	public int requestItems(LogisticatorItem logisticator, FilterLogic filter, LogisticsNetwork network, ILogisticator target, ItemStack items, boolean ignoreMeta, boolean ignoreNBT) {

		// Do not provide items if the logisticator is not powered
		if (logisticator.isPowered() == false) return 0;
		
		// Send up to the desired number of the given item on the route
		int numSent = 0;
				
		// Get the cache attached to the logisticator
		DuctUnitItem.Cache cache = logisticator.itemDuct.tileCache[logisticator.side];
		if (cache == null) return 0;
		
		// Get the handler for the side of the inventory attached to the duct
		IItemHandler handler = cache.getItemHandler(logisticator.side ^ 1);
		if (handler == null) return 0;
		
		List<ItemStack> toRemove = new ArrayList<ItemStack>();
		
		for (Entry<ItemStack, Integer> entry : itemSlotMap.entrySet())
		{
			ItemStack curStack = entry.getKey();
			int i = entry.getValue();
			int toPull = Math.min(Math.min(handler.getStackInSlot(i).getCount(), items.getCount() - numSent), filter.getLevel(FilterLogic.levelStackSize));
			ItemStack simulated = handler.extractItem(i, toPull, true);
			if (LDItemHelper.itemComparator.compareWithFlags(simulated, items, ignoreMeta, ignoreNBT) == 0)
			{
				ItemStack stackPulled = handler.extractItem(i, simulated.getCount(), false);
				// Update cache
				if (stackPulled.getCount() >= curStack.getCount())
				{
					toRemove.add(curStack);
				}
				else
				{
					curStack.shrink(stackPulled.getCount());
				}
				LogisticsDestination dest = logisticator.getDestination(network);
				Route route = logisticator.createRoute(network, target);
				
				if (route != null)
				{
					TravelingItem traveling = new TravelingItem(stackPulled, logisticator.itemDuct, route, (byte) (logisticator.side ^ 1), logisticator.getSpeed());
					traveling.mustGoToDest = true;
					logisticator.itemDuct.insertNewItem(traveling);
					numSent += stackPulled.getCount();
					if (numSent > 0)
						break;
				}
			}
		}
		
		for (ItemStack curRemoval : toRemove)
		{
			itemSlotMap.remove(curRemoval);
			itemsSorted.remove(curRemoval);
		}
		
		return numSent;
	}

	@Override
	public int acceptsItems(LogisticatorItem logisticator, FilterLogic filter, ItemStack items) {
		// Providers do not accept any items.
		return 0;
	}

	@Override
	public List<ItemStack> getProvidedItems(LogisticatorItem logisticator, FilterLogic filter) {
		
		// Do not provide items if the logisticator is not powered
		if (logisticator.isPowered() == false) return Collections.EMPTY_LIST;
		
		return Collections.unmodifiableList(itemsSorted);
	}

	@Override
	public List<ItemStack> getCraftedItems(LogisticatorItem logisticator, FilterLogic filter) {
		// Providers do not craft items.
		return null;
	}
	
	@Override
	public void updateCaches(LogisticatorItem logisticator, FilterLogic filter)
	{
		// Clear the cached items
		itemSlotMap.clear();
		itemsSorted.clear();
		
		List<ItemStack> unsorted = new ArrayList<ItemStack>();
		
		
		// Get the cache attached to the logisticator
		DuctUnitItem.Cache cache = logisticator.itemDuct.tileCache[logisticator.side];
		if (cache == null) return;
		
		// Get the handler for the side of the inventory attached to the duct
		IItemHandler handler = cache.getItemHandler(logisticator.side ^ 1);
		if (handler == null) return;
		
		// Find any stacks that match the filter
		for (int i = 0; i < handler.getSlots(); i++)
		{
			// Check the contents of this slot
			ItemStack curStack = handler.getStackInSlot(i).copy();
			// Check if the stack is empty, nonzero size and passes the associated filter
			if (!curStack.isEmpty() && curStack.getCount() > 0 && filter.matchesFilter(curStack)) {
				itemSlotMap.put(curStack, i);
				unsorted.add(curStack);
			}
		}
		
		// Sort items and replace sorted list with new list
		unsorted.sort(LDItemHelper.itemComparator);
		itemsSorted = unsorted;
	}

}
