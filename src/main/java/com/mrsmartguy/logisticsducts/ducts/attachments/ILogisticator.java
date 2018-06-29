package com.mrsmartguy.logisticsducts.ducts.attachments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mrsmartguy.logisticsducts.gui.container.ContainerRecipe;
import com.mrsmartguy.logisticsducts.network.LogisticsDestination;
import com.mrsmartguy.logisticsducts.network.LogisticsNetwork;
import com.mrsmartguy.logisticsducts.roles.LogisticsRole;

import cofh.thermaldynamics.duct.attachments.filter.FilterLogic;
import cofh.thermaldynamics.duct.item.TravelingItem;
import cofh.thermaldynamics.multiblock.IGridTileRoute;
import cofh.thermaldynamics.multiblock.Route;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * A component of a logistics network. This logisticator may be on several networks, and they are all
 * differentiated by which endpoint you use (and vice-versa). i.e. A TileEntity logisticator can have 6 networks that
 * it's a part of, with each destination being the pipe on each side
 */
public interface ILogisticator {
	
	/**
	 * Sends as many of the requested item to the destination along the given route via the logistics network.
	 * @param network The logistics network.
	 * @param requester The logisticator requesting the items.
	 * @param items The item stack being requested.
	 * @param ignoreMeta Whether items can be provided that do not match the metadata of the given stack.
	 * @param ignoreNBT Whether items can be provided that do not match the tags of the given stack.
	 * @return The total number of items sent.
	 */
	public int requestItems(LogisticsNetwork network, ILogisticator requester, ItemStack items, boolean ignoreMeta, boolean ignoreNBT);
	
	/**
	 * Attempts to craft the requested items.
	 * @param network The logistics network.
	 * @param requester The logisticator requesting the items.
	 * @param items The item stack being requested.
	 * @param ignoreMeta Whether items can be provided that do not match the metadata of the given stack.
	 * @param ignoreNBT Whether items can be provided that do not match the tags of the given stack.
	 * @param completeCraftsOnly Whether insufficient crafting requests should not be completed.
	 * @return The total number of items sent.
	 */
	public int craftItems(LogisticsNetwork network, ILogisticator requester, ItemStack items, boolean ignoreMeta, boolean ignoreNBT, boolean completeCraftsOnly);
	
	/**
	 * Gets a list of all item stacks that can be provided to the logistics network by this logisticator.
	 * @return The list of all item stacks provided by this logisticator to the network.
	 */
	public List<ItemStack> getProvidedItems();
	
	/**
	 * Returns a list of all items that can be crafted by this logisticator.
	 * @return The list of all item stacks crafted by this logisticator.
	 */
	public abstract List<ItemStack> getCraftedItems();
	
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
	
	/**
	 * Returns the logistics network that this logisticator is part of.
	 * @param destination This logisticator's destination for the network to delete
	 * @return The logistics network that this logisticator is part of, or null if the network
	 * hasn't been constructed yet.
	 */
	public LogisticsNetwork getNetwork(LogisticsDestination destination);
	
	/**
	 * Gets this logisticators destination on the given network
	 * @return The destination for this logisticator on the given network, null if this logisticator
	 * is not a part of the given network
	 */
	public LogisticsDestination getDestination(LogisticsNetwork network);
	
	
	/**
	 * Sets which logistics network this logisticator is part of.
	 * @param destination This logisticator's destination for the network to set
	 * @param network The new network to put this logisticator in
	 */
	public void setNetwork(LogisticsDestination destination, LogisticsNetwork network);

	/**
	 * Creates a route from this logisticator to another.
	 * @param network The logistics network to use for the created route
	 * @param endpoint The other logisticator to create a route to
	 * @return A route from this logisticator to the given endpoint, null if none exists
	 */
	public Route createRoute(LogisticsNetwork network, ILogisticator endpoint);
	
	/**
	 * Makes this logisticator clear its reference to its containing network.
	 * @param destination This logisticator's destination for the network to invalidate
	 */
	public void invalidateNetwork(LogisticsDestination destination);

	public List<ContainerRecipe> getRecipes();
	
	
}
