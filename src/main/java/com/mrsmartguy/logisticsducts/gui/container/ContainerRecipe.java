package com.mrsmartguy.logisticsducts.gui.container;

import com.mrsmartguy.logisticsducts.gui.slot.SlotFilterStack;
import com.mrsmartguy.logisticsducts.items.ItemLogisticsRecipe;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;

public class ContainerRecipe extends Container {
	
	public static final int CRAFTING_GRID_LEFT = 30;
	public static final int CRAFTING_GRID_TOP = 17;
	
	public static final int CRAFTING_RESULT_LEFT = 124;
	public static final int CRAFTING_RESULT_TOP = 35;
	
	public static final int INV_LEFT = 8;
	public static final int INV_TOP = 84;
	public static final int HOTBAR_TOP = 142;

	ItemLogisticsRecipe recipeItem;
	InventoryPlayer playerInv;
	InventoryBasic inv;
	
	public ContainerRecipe(ItemLogisticsRecipe recipeItem, InventoryPlayer playerInv)
	{
		this.recipeItem = recipeItem;
		this.playerInv = playerInv;
		this.inv = new InventoryBasic("Recipe", false, 10);
		
		addCraftingSlots();
		addInventorySlots();
	}
	
	private void addCraftingSlots()
	{
		// Add ingredient slots
		for (int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(inv, i, CRAFTING_GRID_LEFT + (i % 3) * 18, CRAFTING_GRID_TOP + (i / 3) * 18));
		}
		
		// Add result slot
		addSlotToContainer(new Slot(inv, 9, CRAFTING_RESULT_LEFT, CRAFTING_RESULT_TOP));		
	}
	
	private void addInventorySlots()
	{
		// Add hotbar slots
		for (int i = 0; i < 9; i++)
		{
			addSlotToContainer(new Slot(playerInv, i, i * 18 + INV_LEFT, HOTBAR_TOP));
		}
		// Add remaining inventory slots
		for (int i = 0; i < 27; i++)
		{
			addSlotToContainer(new Slot(playerInv, i + 9, (i % 9) * 18 + INV_LEFT, (i / 9) * 18 + INV_TOP));
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;
	}
	
	

}
