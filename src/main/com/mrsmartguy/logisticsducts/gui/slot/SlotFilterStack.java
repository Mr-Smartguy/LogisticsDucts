package com.mrsmartguy.logisticsducts.gui.slot;

import cofh.thermaldynamics.duct.attachments.filter.IFilterConfig;
import cofh.thermaldynamics.gui.slot.SlotFilter;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * Extension of SlotFilter that allows for stacks with quantity other than 1.
 */
public class SlotFilterStack extends SlotFilter {
	
	private IFilterConfig filter;
	private long timeLastAdded = 0;
	
	public SlotFilterStack(IFilterConfig tile, int slotIndex, int x, int y) {
		super(tile, slotIndex, x, y);
		filter = tile;
	}

	/**
	 * Called when an item stack is put into this slot
	 */
	@Override
	public void putStack(ItemStack stack) {
		
		// Debounce due to stack sometimes being added twice in quick succession
		if (System.currentTimeMillis() - timeLastAdded < 100)
			return;
		
		timeLastAdded = System.currentTimeMillis();

		synchronized (filter.getFilterStacks()) {
			if (!stack.isEmpty() &&
					ItemHandlerHelper.canItemStacksStack(stack, filter.getFilterStacks()[getSlotIndex()])) {
				stack.grow(filter.getFilterStacks()[getSlotIndex()].getCount());
			}
			filter.getFilterStacks()[getSlotIndex()] = stack;
			onSlotChanged();
		}
	}

	/**
	 * Overrided because filter in this context is different than filter in the super's context
	 */
	@Override
	public ItemStack getStack() {

		return filter.getFilterStacks()[getSlotIndex()];
	}
	
	@Override
	public int getSlotStackLimit() {

		return Integer.MAX_VALUE;
	}

	@Override
	public ItemStack decrStackSize(int amount) {

		ItemStack newStack = filter.getFilterStacks()[getSlotIndex()];
		
		if (newStack == null || newStack.isEmpty() || newStack.getCount() <= amount)
			return ItemStack.EMPTY;
		
		newStack.shrink(amount);
		return newStack;
	}

}
