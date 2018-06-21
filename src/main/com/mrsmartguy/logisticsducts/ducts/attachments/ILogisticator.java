package com.mrsmartguy.logisticsducts.ducts.attachments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mrsmartguy.logisticsducts.roles.LogisticsRole;

import cofh.thermaldynamics.duct.attachments.filter.FilterLogic;
import cofh.thermaldynamics.duct.item.TravelingItem;
import cofh.thermaldynamics.multiblock.Route;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public interface ILogisticator {
	/**
	 * Sends as many of the requested item to the destination along the given route via the logistics network.
	 * @return The total number of items sent.
	 */
	public int requestItems(Map<ILogisticator, Route> network, Route route, ItemStack items);
	/**
	 * Gets a list of all item stacks that can be provided to the logistics network by this logisticator.
	 * Warning! This method is worst-case O(n^2) with respect to the number of provided item stacks. Use with caution.
	 * @return The list of all item stacks provided by this logisticator to the network.
	 */
	public List<ItemStack> getProvidedItems();
	/**
	 * Determines if a given stack can be accepted by this logisticator from the network.
	 * @param items The item stack to be accepted.
	 * @return
	 */
	public int acceptsItems(ItemStack items);
	
	/**
	 * Indicates to this logisticator that an item is pending delivery.
	 * @param traveling The item traveling towards this logisticator.
	 */
	public void addPendingItem(TravelingItem traveling);
	
	
}
