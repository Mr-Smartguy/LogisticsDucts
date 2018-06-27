package com.mrsmartguy.logisticsducts.gui.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import com.mrsmartguy.logisticsducts.gui.slot.SlotFilterStack;
import com.mrsmartguy.logisticsducts.items.ItemLogisticsRecipe;
import com.mrsmartguy.logisticsducts.roles.LogisticsRole;

import cofh.core.util.helpers.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.ItemHandlerHelper;

public class ContainerRecipe extends Container {
	
	public static final int CRAFTING_GRID_LEFT = 30;
	public static final int CRAFTING_GRID_TOP = 17;
	
	public static final int CRAFTING_RESULT_LEFT = 124;
	public static final int CRAFTING_RESULT_TOP = 35;
	
	public static final int INV_LEFT = 8;
	public static final int INV_TOP = 84;
	public static final int HOTBAR_TOP = 142;
	
	public static final int NUM_INGREDIENT_SLOTS = 9;
	public static final int NUM_PRODUCT_SLOTS = 1;

	ItemStack recipeItem;
	InventoryPlayer playerInv;
	InventoryBasic inv;
	
	private List<ItemStack> ingredientList;
	private Map<Integer, ItemStack> ingredientMap;
	
	public ContainerRecipe(ItemStack recipeItem, InventoryPlayer playerInv)
	{
		this.recipeItem = recipeItem;
		this.playerInv = playerInv;
		this.inv = new InventoryBasic("Recipe", false, 10);
		this.ingredientList = new ArrayList<ItemStack>();
		this.ingredientMap = new LinkedHashMap<Integer, ItemStack>();
		
		addCraftingSlots();
		
		if (playerInv != null)
		{
			addInventorySlots();
		}
		
		readFromStack(recipeItem);
	}
	
	private void readFromStack(ItemStack stack)
	{
		if (stack == null || stack.isEmpty())
			throw new RuntimeException("Attempted to get ContainerRecipe of empty stack");
		
		if (!(stack.getItem() instanceof ItemLogisticsRecipe))
			throw new RuntimeException("Attempted to get ContainerRecipe of stack with item type that is not ItemLogisticsRecipe");
		
		if (!stack.hasTagCompound())
			return;
		
		NBTTagCompound tag = stack.getTagCompound();
		
		if (!tag.hasKey("inventory"))
			return;
		
		NBTTagList itemTags = tag.getTagList("inventory", NBT.TAG_COMPOUND);
		if (itemTags.tagCount() != (ContainerRecipe.NUM_INGREDIENT_SLOTS + ContainerRecipe.NUM_PRODUCT_SLOTS))
			return;
		
		for (int i = 0; i < (ContainerRecipe.NUM_INGREDIENT_SLOTS + ContainerRecipe.NUM_PRODUCT_SLOTS); i++)
		{
			ItemStack curStack = new ItemStack(itemTags.getCompoundTagAt(i));
			putStackInSlot(i, curStack);
		}
		
		makeIngredientList();
	}
	
	private void makeIngredientList()
	{
		ingredientList.clear();
		
		for (int i = 0; i < ContainerRecipe.NUM_INGREDIENT_SLOTS; i++)
		{
			ingredientList.add(getSlot(i).getStack());
			ingredientMap.put(i, getSlot(i).getStack());
		}
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
	
	public ItemStack getProduct()
	{
		return inventorySlots.get(NUM_INGREDIENT_SLOTS).getStack();
	}
	
	/**
	 * Returns a list of all items in the recipe's ingredients.
	 * @return A list of all items in the recipe's ingredients
	 */
	public List<ItemStack> getIngredients()
	{
		return Collections.unmodifiableList(ingredientList);
	}
	
	/**
	 * Returns a mapping of crafting grid location (0-8 inclusive) to itemstack.
	 * @return A mapping of crafting grid location (0-8 inclusive) to itemstack
	 */
	public Map<Integer, ItemStack> getIngredientMap()
	{
		return Collections.unmodifiableMap(ingredientMap);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		
		super.onContainerClosed(playerIn);
		updateStackNBT();
	}
	
	@Override
	public ItemStack slotClick(int slotId, int mouseButton, ClickType modifier, EntityPlayer player) {

		Slot slot = slotId < 0 ? null : this.inventorySlots.get(slotId);
		
		// Check if the slot is part of the recipe
		if (slot != null && slot.inventory == inv) {
			
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
		
		return ItemStack.EMPTY;
	}
	
	/**
	 * Updates the NBT tag of the stack that this container is associated with.
	 * Must be called whenever slots in the container are updated.
	 */
	private void updateStackNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList invTagList = new NBTTagList();
		
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack curStack = inv.getStackInSlot(i);
			NBTTagCompound curStackTag = new NBTTagCompound();
			
			if (curStack != null)
				curStackTag = curStack.writeToNBT(curStackTag);

			invTagList.appendTag(curStackTag);
		}
		
		tag.setTag("inventory", invTagList);
		recipeItem.setTagCompound(tag);
	}
	

}
