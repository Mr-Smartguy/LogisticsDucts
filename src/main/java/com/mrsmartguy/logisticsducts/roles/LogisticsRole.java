package com.mrsmartguy.logisticsducts.roles;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mrsmartguy.logisticsducts.ducts.attachments.ILogisticator;
import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;
import com.mrsmartguy.logisticsducts.network.LogisticsDestination;
import com.mrsmartguy.logisticsducts.network.LogisticsNetwork;

import cofh.thermaldynamics.duct.attachments.filter.FilterLogic;
import cofh.thermaldynamics.multiblock.IGridTileRoute;
import cofh.thermaldynamics.multiblock.Route;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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
	 * Returns whether the filter for this role should accept stack sizes greater than one.
	 * @return Whether the filter for this role should accept stack sizes greater than one
	 */
	public boolean filterHasStackSize() { return true; }
	
	/**
	 * Returns whether the filter for this role should have a button to change between black or whitelist.
	 * @return Whether the filter for this role should have a button to change between black or whitelist
	 */
	public boolean guiHasBlacklistButton() { return true; }
	
	/**
	 * Performs the function of this role (e.g. keep stock, request).
	 * @param logisticator The logisticator that possesses this role.
	 * @param filter The filter that corresponds to this role in the logisticator.
	 */
	public abstract void performRole(LogisticatorItem logisticator, FilterLogic filter, LogisticsNetwork network);

	/**
	 * Attempts to send the requested items along the given route.
	 * @param logisticator The logisticator that possesses this role.
	 * @param filter The filter that corresponds to this role in the logisticator.
	 * @param target The target tile to send the item to.
	 * @param finalDir The final direction of the route.
	 * @param items The requested items.
	 * @return The total number of items sent.
	 */
	public abstract int requestItems(LogisticatorItem logisticator, FilterLogic filter, LogisticsNetwork network, ILogisticator target, ItemStack items, boolean ignoreMeta, boolean ignoreNBT);
	
	/**
	 * Determines how much of a given stack can be accepted by this role.
	 * @param filter The filter that corresponds to this role in the logisticator.
	 * @param logisticator The logisticator that possesses this role.
	 * @param items The item stack to be accepted.
	 * @return The number of items from the stack that can be accepted.
	 */
	public abstract int acceptsItems(LogisticatorItem logisticator, FilterLogic filter, ItemStack items);
	
	/**
	 * Returns a list of all item stacks provided by this role.
	 * @param logisticator The logisticator that possesses this role.
	 * @param filter The filter that corresponds to this role in the logisticator.
	 * @return The list of all item stacks provided by this role.
	 */
	public abstract List<ItemStack> getProvidedItems(LogisticatorItem logisticator, FilterLogic filter);
	
	/**
	 * Returns a list of all items that can be crafted by this role.
	 * @param logisticator The logisticator that possesses this role.
	 * @param filter The filter that corresponds to this role in the logisticator.
	 * @return The list of item stacks that can be crafted by this role.
	 */
	public abstract List<ItemStack> getCraftedItems(LogisticatorItem logisticator, FilterLogic filter);

	/**
	 * Callback that gives the role the opportunity to update any cached data.
	 * @param logisticator TODO
	 * @param filter TODO
	 */
	public abstract void updateCaches(LogisticatorItem logisticator, FilterLogic filter);
	
	/**
	 * Writes any extra data this role needs to store to an NBT tag
	 * @param tag The tag to write to
	 * @return True if any data was written, false otherwise
	 */
	public boolean writeToTag(NBTTagCompound tag) { return false; }
	
	/**
	 * Reads any data from the NBT tag that this role saved previously
	 * @param tag The tag to read to
	 */
	public void readFromTag(NBTTagCompound tag) {}

}
