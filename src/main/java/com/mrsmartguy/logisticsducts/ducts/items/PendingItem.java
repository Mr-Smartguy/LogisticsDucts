package com.mrsmartguy.logisticsducts.ducts.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

/**
 * Object that represents an item that will eventually end up in the logistics network
 */
public class PendingItem {
	
	private boolean sent = false;
	private ItemStack stack;
	private BlockPos dest;
	
	public PendingItem(ItemStack stack, BlockPos dest)
	{
		// Copy the item stack because item stacks are mutable
		this.stack = stack.copy();
		this.dest = dest;
	}
	
	/**
	 * Returns whether this pending item has been sent into the logistics network yet
	 * @return True if the item has been produced and sent into the pipes, false otherwise
	 */
	public boolean isSentYet()
	{
		return sent;
	}
	
	public void setSent()
	{
		sent = true;
	}
	
	/**
	 * Returns the item stack that this pending item corresponds to
	 * @return The item stack that this pending item corresponds to
	 */
	public ItemStack getStack()
	{
		return stack.copy();
	}
	
	/**
	 * Returns the destination of this item once it's sent through the logistics network
	 * @return This pending item's destination's position
	 */
	public BlockPos getTargetPos()
	{
		return dest;
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		NBTTagCompound itemTag = new NBTTagCompound();
		stack.writeToNBT(itemTag);
		tag.setTag("item", itemTag);
				
		tag.setInteger("x", dest.getX());
		tag.setInteger("y", dest.getX());
		tag.setInteger("z", dest.getX());
		
		tag.setBoolean("sent", sent);
		
	}
	public void readFromNBT(NBTTagCompound tag)
	{
		ItemStack newStack = new ItemStack(tag.getCompoundTag("item"));
		
		dest = new BlockPos(
				tag.getInteger("x"),
				tag.getInteger("y"),
				tag.getInteger("z"));
		
		sent = tag.getBoolean("sent");
	}

}
