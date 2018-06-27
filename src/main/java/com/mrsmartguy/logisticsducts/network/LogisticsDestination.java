package com.mrsmartguy.logisticsducts.network;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * Represents a destination on the logistics network. This contains the block position of the final duct to pass through
 * as well as the final direction for the item to take inside this duct.
 */
public class LogisticsDestination {
	
	/**
	 * The position of the final duct for this destination.
	 */
	public final BlockPos destPos;
	
	/**
	 * The direction to take through the final duct for this destination.
	 */
	public final EnumFacing destDir;
	
	/**
	 * Constructs a LogisticsDestination given a position and a direction (as EnumFacing).
	 * @param finalPos The position of the final duct on this destination
	 * @param finalDirection The final direction to take on this destination's route
	 */
	public LogisticsDestination(BlockPos finalPos, EnumFacing finalDirection)
	{
		this.destPos = finalPos;
		this.destDir = finalDirection;
	}
	
	/**
	 * Constructs a LogisticsDestination given a position and a direction (as a byte).
	 * @param finalPos The position of the final duct on this destination
	 * @param finalDirection The ordinal of the final direction to take on this destination's route
	 */
	public LogisticsDestination(BlockPos finalPos, byte finalDirection)
	{
		this.destPos = finalPos;
		this.destDir = EnumFacing.VALUES[finalDirection];
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof LogisticsDestination))
			return false;
		LogisticsDestination otherDest = (LogisticsDestination) other;
		return destDir.equals(otherDest.destDir) && destPos.equals(otherDest.destPos);
	}
}
