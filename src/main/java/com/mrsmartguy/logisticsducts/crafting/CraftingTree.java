package com.mrsmartguy.logisticsducts.crafting;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the crafting hierarchy for a crafting request.
 * 
 */
public class CraftingTree {
	
	private CraftingOperation operation;
	private List<CraftingTree> children;
	
	public CraftingTree(CraftingOperation operation, List<CraftingTree> children)
	{
		this.operation = operation;
		this.children = children;
	}
	
	/**
	 * Returns a list of the lowest-most crafting operations.
	 * @return A list of the lowest-most crafting operations
	 */
	public List<CraftingOperation> getTerminalOperations()
	{
		if (!hasChildren())
		{
			// No children, return this node's operation
			ArrayList<CraftingOperation> op = new ArrayList<CraftingOperation>();
			op.add(operation);
			return op;
		}
		else
		{
			// Collect operations from children
			List<List<CraftingOperation>> childOps = new ArrayList<List<CraftingOperation>>();
			for (CraftingTree child : children)
			{
				childOps.add(child.getTerminalOperations());
			}
			ArrayList<CraftingOperation> allOps = new ArrayList<CraftingOperation>();
			
			// Add all child operations to the list of operations
			for (List<CraftingOperation> childOp : childOps)
			{
				allOps.addAll(childOp);
			}
			
			return allOps;
		}
	}
	
	public CraftingOperation getOperation()
	{
		return operation;
	}
	
	public List<CraftingTree> getChildren()
	{
		return children;
	}
	
	public boolean hasChildren()
	{
		return children != null && !children.isEmpty();
	}
	
}
