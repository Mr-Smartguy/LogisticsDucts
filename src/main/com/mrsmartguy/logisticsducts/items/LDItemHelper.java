package com.mrsmartguy.logisticsducts.items;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class LDItemHelper {
	
	public static final LDItemComparator itemComparator = new LDItemComparator();
	
	/**
	 * Finds the items that exist in both sorted lists (ignores stack size).
	 * To reiterate, both lists MUST BE sorted for this to work correctly!
	 * @param source The list of items from which ones in target will be matched to (e.g. the filter)
	 * @param target The list of items to match with those in source (e.g. the inventory)
	 * @return A mapping of items in source to those that match from target based on the given options (e.g. filter stacks to a list of matching inventory stacks)
	 */
	public static Map<ItemStack, List<ItemStack>> findElementsInSorted(List<ItemStack> source, List<ItemStack> target, boolean ignoreMeta, boolean ignoreNBT)
	{
		// Current indices of the two lists 
		int sourceIndex = 0, targetIndex = 0;
		
		LinkedHashMap<ItemStack, List<ItemStack>> intersection = new LinkedHashMap<ItemStack, List<ItemStack>>();
		
		// Iterate over sorted arrays in order of their elements comparison results
		// Because the arrays are both sorted, this can be done in one pass
		while (sourceIndex < source.size() && targetIndex < target.size())
		{
			ItemStack sourceStack = source.get(sourceIndex);
			ItemStack targetStack = target.get(targetIndex);
			
			int compareVal = itemComparator.compareWithFlags(sourceStack, targetStack, ignoreMeta, ignoreNBT);
			
			// Get list corresponding to the source stack, creating it if it doesn't exist (and if source stack isnt empty)
			List<ItemStack> curEntry = intersection.get(sourceStack);
			
			if (!sourceStack.isEmpty() && curEntry == null)
			{
				curEntry = new ArrayList<ItemStack>();
				intersection.put(sourceStack, curEntry);
			}
			
			if (compareVal == 0)
			{
				if (!sourceStack.isEmpty())
				{
					curEntry.add(targetStack);
				}
				// Check next stack in target for equality so that we don't miss duplicate stacks in target
				if (targetIndex < target.size() - 1 &&
						itemComparator.compareWithFlags(sourceStack, target.get(targetIndex + 1), ignoreMeta, ignoreNBT) == 0)
					targetIndex++;
				else
					sourceIndex++;
			}
			else if (compareVal > 0)
			{
				targetIndex++;
			}
			else
			{
				sourceIndex++;
			}
		}
		
		return intersection;
	}
	
	/**
	 * Comparator for ordering ItemStacks based on their item names, followed by metadata followed by NBT.
	 */
	public static class LDItemComparator implements Comparator<ItemStack> {

		/**
		 * Compares based on item name
		 */
		public int compareNoMetaNoNBT(ItemStack o1, ItemStack o2)
		{
			// Null and empty check
			if (o1 == null || o1.isEmpty())
			{
				if (o2 == null || o2.isEmpty())
				{
					// Both are null, comparison is equal
					return 0;
				}
				// o1 is null and o2 is not, o1 is "less than" o2
				return Integer.MIN_VALUE;
			}
			else if (o2 == null || o2.isEmpty())
			{
				// o1 is not null and o2 is null, o1 is "greater than" o2
				return Integer.MAX_VALUE;
			}
			
			return o1.getItem().getUnlocalizedName().compareTo(o2.getItem().getUnlocalizedName());
		}

		/**
		 * Compares based on item name and metadata
		 */
		public int compareNoNBT(ItemStack o1, ItemStack o2)
		{
			// Compare names
			int nameCompare = compareNoMetaNoNBT(o1, o2);
			if (nameCompare != 0)
			{
				return nameCompare;
			}
			// Null check because both being null would result in 0 result from prev compare
			else if (o1 == null && o2 == null)
			{
				return 0;
			}
			
			// Compare metadata
			return o1.getMetadata() - o2.getMetadata();
		}
		
		/**
		 * Compares based on item name and tags
		 */
		public int compareNoMeta(ItemStack o1, ItemStack o2)
		{
			// Compare names
			int nameCompare = compareNoMetaNoNBT(o1, o2);
			if (nameCompare != 0)
			{
				return nameCompare;
			}
			// Null check because both being null would result in 0 result from prev compare
			else if (o1 == null && o2 == null)
			{
				return 0;
			}
			
			// Compare tags
			return o1.getTagCompound().hashCode() - o2.getTagCompound().hashCode();
		}

		/**
		 * Compares based on item name, metadata and tags
		 */
		@Override
		public int compare(ItemStack o1, ItemStack o2) {
			
			// Compare names and metadata
			int nameAndMetaCompare = compareNoNBT(o1, o2);
			if (nameAndMetaCompare != 0)
			{
				return nameAndMetaCompare;
			}
			// Null check because both being null would result in 0 result from prev compare
			else if (o1 == null && o2 == null)
			{
				return 0;
			}
			
			// Compare tags
			NBTTagCompound tag1 = o1.getTagCompound();
			NBTTagCompound tag2 = o2.getTagCompound();
			
			if (tag1 == null)
				if (tag2 == null)
					return 0;
				else
					return Integer.MIN_VALUE;
			else if (tag2 == null)
				return Integer.MAX_VALUE;
			else
				return o1.getTagCompound().hashCode() - o2.getTagCompound().hashCode();
		}

		/**
		 * Compares based on the given flags
		 */
		public int compareWithFlags(ItemStack o1, ItemStack o2, boolean ignoreMeta, boolean ignoreNBT)
		{
			if (ignoreMeta)
			{
				if (ignoreNBT)
					return compareNoMetaNoNBT(o1, o2);
				else
					return compareNoMeta(o1, o2);
			}
			else
			{
				if (ignoreNBT)
					return compareNoNBT(o1, o2);
				else
					return compare(o1, o2);
			}
		}
		
	}

}
