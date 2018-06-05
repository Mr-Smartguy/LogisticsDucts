package com.mrsmartguy.logisticsducts.ducts.attachments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.mrsmartguy.logisticsducts.LogisticsDucts;
import com.mrsmartguy.logisticsducts.gui.GuiLogisticator;
import com.mrsmartguy.logisticsducts.gui.container.ContainerLogisticator;
import com.mrsmartguy.logisticsducts.items.LDItems;
import com.mrsmartguy.logisticsducts.network.LogisticsNetwork;
import com.mrsmartguy.logisticsducts.roles.AcceptorRole;
import com.mrsmartguy.logisticsducts.roles.ExtractorRole;
import com.mrsmartguy.logisticsducts.roles.LogisticsRole;
import com.mrsmartguy.logisticsducts.textures.LDTextures;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.thermaldynamics.duct.Attachment;
import cofh.thermaldynamics.duct.AttachmentRegistry;
import cofh.thermaldynamics.duct.attachments.ConnectionBase;
import cofh.thermaldynamics.duct.attachments.retriever.RetrieverItem;
import cofh.thermaldynamics.duct.item.DuctUnitItem;
import cofh.thermaldynamics.duct.item.TravelingItem;
import cofh.thermaldynamics.duct.tiles.TileGrid;
import cofh.thermaldynamics.gui.client.GuiDuctConnection;
import cofh.thermaldynamics.multiblock.Route;
import cofh.thermaldynamics.multiblock.RouteCache;
import cofh.thermaldynamics.render.RenderDuct;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class LogisticatorItem extends RetrieverItem {
	
	private LogisticsRole[] roles = new LogisticsRole[] {new ExtractorRole(), new AcceptorRole()};
	private LinkedList<TravelingItem> pending = new LinkedList<TravelingItem>();
	
	public LogisticatorItem(TileGrid tile, byte side) {
		super(tile, side);
	}
	
	public LogisticatorItem(TileGrid tile, byte side, int type) {
		super(tile, side, type);
	}

	@Override
	public String getInfo() {

		return "tab.logisticsducts.logisticatorItem";
	}

	@Override
	public boolean isFilter() {

		return true;
	}

	@Override
	public ResourceLocation getId() {

		return LDAttachmentRegistry.LOGISTICATOR_ITEM;
	}

	@Override
	public ItemStack getPickBlock() {

		return new ItemStack(LDItems.itemLogisticator, 1, type);
	}

	@Override
	public String getName() {

		return "item.logisticsducts.logisticator." + type + ".name";
	}

	@Override
	public int tickDelay() {

		return 5;
	}
	
	/**
	 * Verifies that all pending items are still in transit to this logisticator.
	 */
	private void handlePendingItems() {
		Iterator<TravelingItem> it = pending.iterator();
		while (it.hasNext()) {
			TravelingItem item = it.next();
			BlockPos target = new BlockPos(item.destX, item.destY, item.destZ);
			
			if (item.stack.getCount() == 0 || !target.equals(itemDuct.pos()))
			{
				it.remove();
			}
		}
	}
	
	@Override
	public void handleItemSending() {

		handlePendingItems();
		
		IItemHandler simulatedInv = getCachedInv();
		
		HashMap<LogisticatorItem, Route> network = new HashMap<LogisticatorItem, Route>();
		
		// Make an unmodifiable copy of pending to prevent roles from modifying it unintentionally
		List<TravelingItem> pendingUnmod = Collections.unmodifiableList(pending);
		
		if (!verifyCache()) {
			return;
		}
		
		// Scan routes for other logisticators
		for (Route route : routesWithInsertSideList) {
			DuctUnitItem endPoint = (DuctUnitItem) route.endPoint;

			int i = route.getLastSide();

			Attachment attachment = endPoint.parent.getAttachment(i);
			
			if (attachment != null && attachment instanceof LogisticatorItem && attachment != this) {
				network.put((LogisticatorItem) attachment, route);
			}
		}
		
		// Perform logistics roles
		for (LogisticsRole role : roles)
		{
			role.performRole(this, network);
		}
	}
	
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiLogisticator(inventory, this);
	}
	
	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerLogisticator(inventory, this);
	}

	@Override
	@SideOnly (Side.CLIENT)
	public boolean render(IBlockAccess world, BlockRenderLayer layer, CCRenderState ccRenderState) {

		if (layer != BlockRenderLayer.SOLID) {
			return false;
		}

		Translation trans = Vector3.fromTileCenter(baseTile).translation();
		RenderDuct.modelConnection[isPowered ? 1 : 2][side].render(ccRenderState, trans, new IconTransformation(LDTextures.LOGISTICATOR_BASE[stuffed ? 1 : 0][type]));
		return true;
	}

	/* IPortableData */
	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		super.writePortableData(player, tag);
		tag.setString("DisplayType", "item.logisticsducts.logisticator.0.name");
	}
	
	/**
	 * Sends as many of the requested item to the destination along the given route via the logistics network.
	 * @return The total number of items sent.
	 */
	public int requestItems(Route route, ItemStack items)
	{
		// Copy items to prevent modifying the original stack, in case the caller didn't do this already
		items = items.copy();
		int sent = 0;
		for (LogisticsRole role : roles)
		{
			int curSent = role.requestItems(this, route, items);
			sent += curSent;
			if (items.getCount() < curSent)
				items.shrink(curSent);
			else
				break;
		}
		return sent;
	}
	
	/**
	 * Gets a list of all item stacks that can be provided to the logistics network by this logisticator.
	 * Warning! This method is worst-case O(n^2) with respect to the number of provided item stacks. Use with caution.
	 * @return The list of all item stacks provided by this logisticator to the network.
	 */
	public List<ItemStack> getProvidedItems()
	{
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		for (LogisticsRole role : roles)
		{
			List<ItemStack> newStacks = role.getProvidedItems(this);
			for (ItemStack curStack : newStacks)
			{
				for (int i = 0; i < stacks.size(); i++)
				{
					if (ItemHandlerHelper.canItemStacksStack(curStack, stacks.get(i)))
					{
						stacks.get(i).grow(curStack.getCount());
					}
					else
					{
						stacks.add(curStack);
					}
				}
			}
		}
		return stacks;
	}
	
	/**
	 * Determines if a given stack can be accepted by this logisticator from the network.
	 * @param items The item stack to be accepted.
	 * @return
	 */
	public int acceptsItems(ItemStack items)
	{
		int numAccepted = 0;
		
		for (LogisticsRole role : roles)
		{
			numAccepted += role.acceptsItems(this, items);
			if (numAccepted > items.getCount()) 
			{
				return items.getCount();
			}
		}
		return numAccepted;
	}

	/**
	 * Indicates to this logisticator that an item is pending delivery.
	 * @param traveling The item traveling towards this logisticator.
	 */
	public void addPendingItem(TravelingItem traveling) {
		pending.add(traveling);
	}

}
