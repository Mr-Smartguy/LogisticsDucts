package com.mrsmartguy.logisticsducts.roles;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.mrsmartguy.logisticsducts.ducts.attachments.FilterLogicConstants;
import com.mrsmartguy.logisticsducts.ducts.attachments.ILogisticator;
import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;
import com.mrsmartguy.logisticsducts.items.LDItemHelper;

import cofh.thermaldynamics.duct.attachments.filter.FilterLogic;
import cofh.thermaldynamics.multiblock.IGridTileRoute;
import cofh.thermaldynamics.multiblock.Route;
import net.minecraft.item.ItemStack;

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
	public void performRole(LogisticatorItem logisticator, FilterLogic filter, Map<ILogisticator, Route> network) {
		
		boolean ignoreMeta = filter.getFlag(FilterLogicConstants.flagIgnoreMetadata);
		boolean ignoreNBT = filter.getFlag(FilterLogicConstants.flagIgnoreNBT);
		
		// Request items from any logisticators on the network
		for (Entry<ILogisticator, Route> entry : network.entrySet())
		{
			ILogisticator target = entry.getKey();
			
			List<ItemStack> provided = target.getProvidedItems();
			
			for (ItemStack curRequest : filter.getFilterStacks())
			{
				final ItemStack curRequestWithSize = curRequest.copy();
				curRequestWithSize.setCount(filter.getLevel(FilterLogic.levelStackSize));
				Optional<ItemStack> opt = provided
						.stream()
						.filter(stack -> LDItemHelper.itemComparator.compareWithFlags(stack, curRequestWithSize, ignoreMeta, ignoreNBT) == 0)
						.findFirst();
				
				if (opt.isPresent())
				{
					target.requestItems(network, logisticator.itemDuct, logisticator.side, curRequestWithSize, ignoreMeta, ignoreNBT);
					return;
				}
			}
		}
	}

	@Override
	public int requestItems(LogisticatorItem logisticator, FilterLogic filter, IGridTileRoute target, byte finalDir, ItemStack items, boolean ignoreMeta, boolean ignoreNBT) {
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