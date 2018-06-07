package com.mrsmartguy.logisticsducts.roles;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;

import cofh.thermaldynamics.multiblock.Route;
import net.minecraft.item.ItemStack;

/**
 * Represents a role that a logisticator can perform in the logistics system
 */
public abstract class LogisticsRole {
	
	/**
	 * Returns the name of this role (e.g. acceptor, etc.)
	 * @return The name of this role
	 */
	public abstract String getName();
	/**
	 * Performs the function of this role (e.g. keep stock, request).
	 * @param logisticator The logisticator that possesses this role.
	 * @param logisticators The logistics network, minus the logisticator that possesses this role.
	 */
	public abstract void performRole(LogisticatorItem logisticator, Map<LogisticatorItem, Route> network);

	/**
	 * Attempts to send the requested items along the given route.
	 * @param logisticator The logisticator that possesses this role.
	 * @param route The route to send the items along.
	 * @param items The requested items.
	 * @return The total number of items sent.
	 */
	public abstract int requestItems(LogisticatorItem logisticator, Route route, ItemStack items);
	
	/**
	 * Determines how much of a given stack can be accepted by this role.
	 * @param logisticator The logisticator that possesses this role.
	 * @param items The item stack to be accepted.
	 * @return The number of items from the stack that can be accepted.
	 */
	public abstract int acceptsItems(LogisticatorItem logisticator, ItemStack items);
	
	/**
	 * Returns a list of all item stacks provided by this role.
	 * @param logisticator The logisticator that possesses this role.
	 * @return The list of all item stacks provided by this role.
	 */
	public abstract List<ItemStack> getProvidedItems(LogisticatorItem logisticator);

}
