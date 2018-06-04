package com.mrsmartguy.logisticsducts.roles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;

import cofh.thermaldynamics.duct.item.DuctUnitItem;
import cofh.thermaldynamics.duct.item.TravelingItem;
import cofh.thermaldynamics.multiblock.Route;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ExtractorRole extends LogisticsRole {

	@Override
	public void performRole(LogisticatorItem logisticator, Map<LogisticatorItem, Route> network) {
		System.out.println("Attempging to extract items");
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
			//TODO only grab stack size based on duct type
			
			// Simulate pulling out of the inventory to see what ItemStack would be pulled
			ItemStack curStack = handler.extractItem(i, Integer.MAX_VALUE, true);
			if (!curStack.isEmpty() && curStack.getCount() > 0) {
				stack = curStack;
				slot = i;
				break;
			}
		}
		
		// If a stack was found, extract it
		if (stack != null)
		{
			for (Map.Entry<LogisticatorItem, Route> endpoint : network.entrySet())
			{
				int numAccepted = endpoint.getKey().acceptsItems(stack);
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
					
					TravelingItem traveling = new TravelingItem(extracted, logisticator.itemDuct, endpoint.getValue().copy(), (byte) (logisticator.side ^ 1), logisticator.getSpeed());
					
					logisticator.itemDuct.insertNewItem(traveling);
					logisticator.addPendingItem(traveling);
					
					if (stack.getCount() == 0) break;
				}
			}
		}
	}

	@Override
	public int requestItems(LogisticatorItem logisticator, Route route, ItemStack items) {
		// Extractors do not provide items
		return 0;
	}

	@Override
	public int acceptsItems(LogisticatorItem logisticator, ItemStack items) {
		// Extractors do not accept items
		return 0;
	}

	@Override
	public List<ItemStack> getProvidedItems(LogisticatorItem logisticator) {
		// Extractors do not provide items
		return null;
	}

}
