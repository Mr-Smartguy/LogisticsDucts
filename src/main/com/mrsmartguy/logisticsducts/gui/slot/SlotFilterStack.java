package com.mrsmartguy.logisticsducts.gui.slot;

import cofh.thermaldynamics.duct.attachments.filter.IFilterConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Filter Slot that allows for stacks with quantity other than 1.
 */
public class SlotFilterStack extends Slot {
	
	private IFilterConfig filter;
	private long timeLastAdded = 0;

	public int slotIndex;

	private static final IInventory INV = new InventoryBasic("[FALSE]", false, 0);
	
	public SlotFilterStack(IFilterConfig tile, int slotIndex, int x, int y) {
		super(INV, slotIndex, x, y);
		this.slotIndex = slotIndex;
		this.filter = tile;
	}

	/**
	 * Called when an item stack is put into this slot
	 */
	@Override
	public void putStack(ItemStack stack) {
		
		// Debounce due to stack sometimes being added twice in quick succession
		/*if (System.currentTimeMillis() - timeLastAdded < 100)
			return;
		
		timeLastAdded = System.currentTimeMillis();*/
		
        filter.getFilterStacks()[getSlotIndex()] = stack;
        this.onSlotChanged();
		/*synchronized (filter.getFilterStacks()) {
			if (!stack.isEmpty() &&
					ItemHandlerHelper.canItemStacksStack(stack, filter.getFilterStacks()[getSlotIndex()])) {
				stack.grow(filter.getFilterStacks()[getSlotIndex()].getCount());
			}
			filter.getFilterStacks()[getSlotIndex()] = stack;
			onSlotChanged();
		}*/
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {

		return false;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return !stack.isEmpty();
	}

	@Override
	public void onSlotChanged() {

		filter.onChange();
	}

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

	@Override
	public boolean isHere(IInventory inv, int slotIn) {

		return false;
	}

}
