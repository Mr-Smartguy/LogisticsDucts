package com.mrsmartguy.logisticsducts.gui.container;

import java.util.LinkedList;

import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;
import com.mrsmartguy.logisticsducts.gui.slot.SlotFilterStack;

import cofh.core.gui.slot.SlotFalseCopy;
import cofh.core.util.helpers.ItemHelper;
import cofh.thermaldynamics.duct.Attachment;
import cofh.thermaldynamics.duct.attachments.ConnectionBase;
import cofh.thermaldynamics.duct.attachments.filter.FilterLogic;
import cofh.thermaldynamics.gui.container.ContainerAttachmentBase;
import cofh.thermaldynamics.gui.slot.SlotFilterFluid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * Container for Logisticators. Very similar to (and mostly derived from) ContainerDuctConnection,
 * except that it uses SlotFilterStack instead of SlotFilter for container slots.
 */
public class ContainerLogisticator extends ContainerAttachmentBase {

	private final LogisticatorItem tile;
	public final FilterLogic[] filters;
	public LinkedList<SlotFilterStack> filterSlots = new LinkedList<>();
	public final int slotsPerFilter;
	public final int gridWidth;
	public final int gridHeight;
	public final int gridX0;
	public final int gridY0;

	private int activeRoleIndex;
	
	// The amount that deactivated slots are moved by
	private static final int DEACTIVATED_X_OFFSET = 10000;
	
	public ContainerLogisticator(InventoryPlayer inventory, LogisticatorItem tile, int activeRoleIndex)
	{

		super(inventory, tile);
		this.tile = tile;

		filters = tile.getFilters();

		assert filters != null;
		
		slotsPerFilter = filters[0].getFilterStacks().length;
		gridWidth = filters[0].filterStackGridWidth();
		gridHeight = slotsPerFilter / gridWidth;

		gridX0 = 89 - gridWidth * 9;
		
		this.activeRoleIndex = activeRoleIndex;

		switch (gridHeight)
		{
			case 1:
				gridY0 = 38;
				break;
			case 2:
				gridY0 = 29;
				break;
			default:
				gridY0 = 20;
				break;
		}
		// Create slots for every filter in the logisticator
		for (int filterIndex = 0; filterIndex < filters.length; filterIndex++)
		{
			for (int i = 0; i < gridHeight; i++)
			{
				for (int j = 0; j < gridWidth; j++)
				{
					// Shift slots over if the filter is not the currently active one
					int xPos = gridX0 + j * 18;
					
					if (filterIndex != activeRoleIndex)
						xPos += DEACTIVATED_X_OFFSET;
					if (filters[i].isItem())
					{
						filterSlots.add(((SlotFilterStack) addSlotToContainer(
								new SlotFilterStack(filters[filterIndex], j + i * gridWidth, xPos, gridY0 + i * 18))));
					}
					/*else
					{
						filterSlots.add(((SlotFilter) addSlotToContainer(
								new SlotFilterFluid(filter, j + i * gridWidth, gridX0 + j * 18, gridY0 + i * 18))));
					}*/
				}
			}
		}
	}
	
	/**
	 * Makes the currently active role's slots move offscreen.
	 * Used to select a new role in a logisticator's gui.
	 */
	public void deactivateCurrentRole()
	{
		if (activeRoleIndex != -1)
		{
			// Move the previous active filter's slots over and move the newly selected slots back
			for (int i = 0; i < slotsPerFilter; i++)
			{
				filterSlots.get(slotsPerFilter * activeRoleIndex + i).xPos += DEACTIVATED_X_OFFSET;
			}
			activeRoleIndex = -1;
		}
	}
	
	/**
	 * Sets the currently active role by index.
	 * Used to select a new role in a logisticator's gui.
	 * @param The index of the currently active role
	 */
	public void setActiveRoleIndex(int roleIndex)
	{
		if (activeRoleIndex != -1)
		{
			// Move the previous active filter's slots over
			for (int i = 0; i < slotsPerFilter; i++)
			{
				filterSlots.get(slotsPerFilter * activeRoleIndex + i).xPos += DEACTIVATED_X_OFFSET;
			}
		}
		// Move the newly selected slots onto the screen
		for (int i = 0; i < slotsPerFilter; i++)
		{
			filterSlots.get(slotsPerFilter * roleIndex + i).xPos -= DEACTIVATED_X_OFFSET;
		}
		this.activeRoleIndex = roleIndex;
	}
	
	/**
	 * Replaces every filter's contents at index with the contents of the filter at index + 1.
	 * Clears the contents of the last filter.
	 * Used to delete a role from a logisticator.
	 * @param index The index of the filter to begin shifting with (the deleted logisticator role index)
	 */
	public void shiftContentsBack(int index)
	{
		// Iterate from index to the second to last filter
		for (int i = index; i < filters.length - 1; i++)
		{
			// Move contents from next filter to current filter
			for (int slotIndex = 0; slotIndex < slotsPerFilter; slotIndex++)
			{
				filterSlots.get(i * slotsPerFilter + slotIndex).putStack(
						filterSlots.get((i + 1) * slotsPerFilter + slotIndex).getStack());
			}
		}
		// Clear the contents of the last filter
		for (int slotIndex = 0; slotIndex < slotsPerFilter; slotIndex++)
		{
			filterSlots.get((filters.length - 1) * slotsPerFilter + slotIndex).putStack(ItemStack.EMPTY);;
		}
	}
	
	@Override
	public ItemStack slotClick(int slotId, int mouseButton, ClickType modifier, EntityPlayer player) {

		Slot slot = slotId < 0 ? null : this.inventorySlots.get(slotId);
		
		if (slot instanceof SlotFilterStack) {
			// Cancel dragging, not applicable here
			resetDrag();
			
			ItemStack slotStack = slot.getStack();
			ItemStack playerStack = player.inventory.getItemStack();
			ItemStack newStack = slotStack.copy();
			
			// Middle click, remove stack in slot
			if (mouseButton == 2)
			{
				newStack = ItemStack.EMPTY;
			}
			// Right click, decrement if sneaking otherwise either split or increment
			else if (mouseButton == 1)
			{
				// Sneaking, decrement
				if (modifier == ClickType.QUICK_MOVE)
				{
					if (newStack.getCount() == 1)
						newStack = ItemStack.EMPTY;
					else
						newStack.shrink(1);
				}
				// Add one if slot stack is empty
				else if (slotStack.isEmpty())
				{
					newStack = playerStack.copy();
					newStack.setCount(1);
				}
				// Increment if stacks can stack
				else if (ItemHandlerHelper.canItemStacksStack(slotStack, playerStack) || playerStack.isEmpty())
				{
					newStack.grow(1);
				}
				// Split if player hand is empty
				else if (playerStack.isEmpty())
				{
					newStack.setCount(newStack.getCount() / 2);
				}
			}
			// Left click, clear if sneaking otherwise either add or replace
			else
			{
				// Clear stack if player is not holding anything or quick move
				if (playerStack.isEmpty() || modifier == ClickType.QUICK_MOVE)
				{
					newStack = ItemStack.EMPTY;
				}
				// Add if stacks can stack
				else if (ItemHandlerHelper.canItemStacksStack(slotStack, playerStack))
				{
					newStack.grow(playerStack.getCount());
				}
				// Replace the slot stack with the player stack
				else
				{
					newStack = playerStack.copy();
				}
			}
			slot.putStack(newStack);
			slot.onSlotChanged();
			return player.inventory.getItemStack();
		}
		return super.slotClick(slotId, mouseButton, modifier, player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {

		Slot slot = inventorySlots.get(slotIndex);

		int invPlayer = 27;
		int invFull = invPlayer + 9;
		int invTile = invFull + slotsPerFilter * filters.length;

		if (slot != null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();
			if (slotIndex < 0) {
				return ItemStack.EMPTY;
			} else if (slotIndex < invFull) {
				Slot k = null;
				for (int i = invFull; i < invTile; i++) {
					Slot slot1 = inventorySlots.get(i);
					if (!slot1.getHasStack()) {
						if (k == null) {
							k = slot1;
						}
					} else {
						if (ItemHelper.itemsEqualWithMetadata(slot1.getStack(), stack)) {
							return ItemStack.EMPTY;
						}
					}
				}
				if (k != null) {
					k.putStack(stack.copy());
				}

				return ItemStack.EMPTY;
			} else {
				slot.putStack(ItemStack.EMPTY);
				slot.onSlotChanged();

			}
		}
		return ItemStack.EMPTY;
	}
	
}
