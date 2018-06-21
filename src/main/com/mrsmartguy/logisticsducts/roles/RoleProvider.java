package com.mrsmartguy.logisticsducts.roles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mrsmartguy.logisticsducts.ducts.attachments.ILogisticator;
import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;

import cofh.thermaldynamics.duct.attachments.filter.FilterLogic;
import cofh.thermaldynamics.duct.item.DuctUnitItem;
import cofh.thermaldynamics.duct.item.TravelingItem;
import cofh.thermaldynamics.multiblock.IGridTileRoute;
import cofh.thermaldynamics.multiblock.Route;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class RoleProvider extends LogisticsRole {

	@Override
	public String getName() {
		return "provider";
	}
	
	// Providers should only have single item stacks in the filter (provided items)
	@Override
	public boolean filterHasStackSize() { return false; }

	@Override
	public void performRole(LogisticatorItem logisticator, FilterLogic filter, Map<ILogisticator, Route> network) {
		// Providers have no active role.
	}

	@Override
	public int requestItems(LogisticatorItem logisticator, FilterLogic filter, IGridTileRoute target, byte finalDir, ItemStack items) {
		
		// Send up to the desired number of the given item on the route
		int numSent = 0;
		
		// Get the cache attached to the logisticator
		DuctUnitItem.Cache cache = logisticator.itemDuct.tileCache[logisticator.side];
		if (cache == null) return 0;
		
		// Get the handler for the side of the inventory attached to the duct
		IItemHandler handler = cache.getItemHandler(logisticator.side ^ 1);
		if (handler == null) return 0;
		
		// Find any stacks that match the filter
		for (int i = 0; i < handler.getSlots() && numSent < items.getCount(); i++)
		{
			// Check the contents of this slot
			ItemStack curStack = handler.getStackInSlot(i);
			
			if (!curStack.isEmpty() && curStack.getCount() > 0 && filter.matchesFilter(curStack))
			{
				if (ItemStack.areItemsEqual(curStack, items) && ItemStack.areItemStackShareTagsEqual(curStack, items))
				{
					ItemStack stackPulled = handler.extractItem(i, items.getCount(), false);
					Route route = logisticator.itemDuct.getRoute(target).copy();
					route.pathDirections.add(finalDir);
					TravelingItem traveling = new TravelingItem(stackPulled, logisticator.itemDuct, route, (byte) (logisticator.side ^ 1), logisticator.getSpeed());
					logisticator.itemDuct.insertNewItem(traveling);
					numSent += stackPulled.getCount();
					
				}
			}
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

	@Override
	public List<ItemStack> getCraftedItems(LogisticatorItem logisticator, FilterLogic filter) {
		// Providers do not craft items.
		return null;
	}

}
