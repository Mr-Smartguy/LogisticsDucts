package com.mrsmartguy.logisticsducts.ducts.attachments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import com.mrsmartguy.logisticsducts.LogisticsDucts;
import com.mrsmartguy.logisticsducts.gui.GuiLogisticator;
import com.mrsmartguy.logisticsducts.gui.container.ContainerLogisticator;
import com.mrsmartguy.logisticsducts.items.LDItemHelper;
import com.mrsmartguy.logisticsducts.items.LDItems;
import com.mrsmartguy.logisticsducts.network.LogisticsNetwork;
import com.mrsmartguy.logisticsducts.roles.RoleAcceptor;
import com.mrsmartguy.logisticsducts.roles.RoleExtractor;
import com.mrsmartguy.logisticsducts.roles.LDRoleRegistry;
import com.mrsmartguy.logisticsducts.roles.LogisticsRole;
import com.mrsmartguy.logisticsducts.textures.LDTextures;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.core.network.PacketBase;
import cofh.core.network.PacketHandler;
import cofh.core.network.PacketTileInfo;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.RedstoneControlHelper;
import cofh.thermaldynamics.duct.Attachment;
import cofh.thermaldynamics.duct.AttachmentRegistry;
import cofh.thermaldynamics.duct.Duct.Type;
import cofh.thermaldynamics.duct.attachments.ConnectionBase;
import cofh.thermaldynamics.duct.attachments.ConnectionBase.NETWORK_ID;
import cofh.thermaldynamics.duct.attachments.filter.FilterLogic;
import cofh.thermaldynamics.duct.attachments.filter.IFilterItems;
import cofh.thermaldynamics.duct.attachments.retriever.RetrieverItem;
import cofh.thermaldynamics.duct.item.DuctUnitItem;
import cofh.thermaldynamics.duct.item.StackMap;
import cofh.thermaldynamics.duct.item.TravelingItem;
import cofh.thermaldynamics.duct.tiles.TileGrid;
import cofh.thermaldynamics.gui.client.GuiDuctConnection;
import cofh.thermaldynamics.multiblock.IGridTileRoute;
import cofh.thermaldynamics.multiblock.Route;
import cofh.thermaldynamics.multiblock.RouteCache;
import cofh.thermaldynamics.render.RenderDuct;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class LogisticatorItem extends RetrieverItem implements ILogisticator {
	
	private LinkedList<TravelingItem> pending = new LinkedList<TravelingItem>();
	
	// The number of roles a given level of logisticator can have
	public static final int[] numRoles = {2, 3, 4, 5, 6};
	
	private FilterLogic[] filters = null;
	private LogisticsRole[] roles = null;
	private LogisticsRole[] prevRoles = null;
	
	private HashMap<String, Integer> playerTabMap = new HashMap<String, Integer>();
	
	// Keep track of role being edited
	private int activeRole = -1;
	
	private List<ItemStack> sortedTraveling = new ArrayList<ItemStack>();
			
	public LogisticatorItem(TileGrid tile, byte side) {
		super(tile, side);
	}
	
	public LogisticatorItem(TileGrid tile, byte side, int type) {
		super(tile, side, type);
		createRoles();
	}
	
	private void createRoles()
	{
		// Create roles and corresponding filters
		roles = new LogisticsRole[numRoles[type]];
		prevRoles = new LogisticsRole[numRoles[type]];
		filters = new FilterLogic[numRoles[type]];
		
		for (int i = 0; i < numRoles[type]; i++)
		{
			roles[i] = null;
			prevRoles[i] = null;
			filters[i] = createFilterLogic();
		}
	}
	
	public LogisticsRole getRole(int index)
	{
		return roles[index];
	}
	
	/**
	 * Sets the active role (used for editing purposes)
	 * @param index
	 */
	public void setActiveRole(int index)
	{
		activeRole = index;
		
		if (filters == null)
			createRoles();
		
		filter = filters[index];
	}
	
	public void setRole(LogisticsRole newRole, int index)
	{
		roles[index] = newRole;
		// Send an update to the server
		if (baseTile.world().isRemote)
		{
			sendRoleUpdatePacket(newRole, index);
		}
		// Mark the chunk dirty to be saved and sent to players
		else
		{
			baseTile.markDirty();
		}
	}
	
	/**
	 * Sends a packet to the server to update the Role of the Logisticator server-side.
	 */
	private void sendRoleUpdatePacket(LogisticsRole newRole, int index)
	{
		PacketTileInfo packet = getNewPacket(LD_NETWORK_ID.ROLE);
		packet.addString(newRole.getName());
		packet.addByte(index);
		PacketHandler.sendToServer(packet);
	}
	
	/**
	 * Send a packet to the server to update the tab that the given player is currently in
	 * @param playerName 
	 * @param tab
	 */
	public void sendPlayerGuiTabPacket(String playerName, int tab)
	{
		PacketTileInfo packet = getNewPacket(LD_NETWORK_ID.PLAYERTAB);
		packet.addString(playerName);
		packet.addByte(tab);
		PacketHandler.sendToServer(packet);
	}
	
	@Override
	public void sendFilterConfigPacketFlag(int flagType, boolean flag) {

		PacketTileInfo packet = getNewPacket(NETWORK_ID.FILTERFLAG);
		
		packet.addByte(activeRole);
		packet.addByte(flagType << 1 | (flag ? 1 : 0));
		PacketHandler.sendToServer(packet);
	}

	@Override
	public void sendFilterConfigPacketLevel(int levelType, int level) {

		PacketTileInfo packet = getNewPacket(NETWORK_ID.FILTERLEVEL);

		packet.addByte(activeRole);
		packet.addByte(levelType);
		packet.addShort(level);

		PacketHandler.sendToServer(packet);
	}
	
	/**
	 * Handle incoming packets.
	 */
	@Override
	public void handleInfoPacketType(byte a, PacketBase payload, boolean isServer, EntityPlayer player) {

		if (a == LD_NETWORK_ID.ROLE)
		{
			String roleString = payload.getString();
			int index = (int)payload.getByte();
			LogisticsRole role = LDRoleRegistry.createRole(roleString);
			setRole(role, index);
		}
		else if (a == LD_NETWORK_ID.PLAYERTAB)
		{
			String playerName = payload.getString();
			int tab = (int)payload.getByte();
			playerTabMap.put(playerName, tab);
		}
		else
		{
			// Extract the role index if the packets were sent by the Logisticator implementation
			// of the appropriate functions (see above)
			if (a == NETWORK_ID.FILTERFLAG || a == NETWORK_ID.FILTERLEVEL)
				setActiveRole((int)payload.getByte());
			super.handleInfoPacketType(a, payload, isServer, player);
		}
	}
	
	public int getPlayerRoleIndex(String playerName)
	{
		// Player side, just return active role index
		if (baseTile.world().isRemote)
		{
			return activeRole;
		}
		// Server side, return from map
		else
		{
			if (playerTabMap.containsKey(playerName))
			{
				return playerTabMap.get(playerName);
			}
			return 0;
		}
	}
	
	/**
	 * Returns this logisticator's filter array
	 */
	public FilterLogic[] getFilters()
	{
		if (filters == null)
			createRoles();
		return filters;
	}
	
	// Allow all items through the filter
	@Override
	public IFilterItems getItemFilter() {
		return new IFilterItems() {

			@Override
			public boolean matchesFilter(ItemStack item) {
				return true;
			}

			@Override
			public boolean shouldIncRouteItems() {
				return false;
			}

			@Override
			public int getMaxStock() {
				return Integer.MAX_VALUE;
			}
			
		};
	}

	@Override
	public String getInfo() {

		return "tab.logisticsducts.logisticatorItem";
	}

	@Override
	public boolean isFilter() {

		return false;
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

	/*@Override
	public int tickDelay() {

		return 5;
	}*/
	
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
	
	private void updateTravelingCache()
	{
		sortedTraveling.clear();
		StackMap stackMap = itemDuct.getGrid().travelingItems.get(itemDuct.pos().offset(EnumFacing.VALUES[side]));
		if (stackMap != null)
		{
			stackMap.getItems().forEach(stack -> sortedTraveling.add(stack));	
		}
		sortedTraveling.sort(LDItemHelper.itemComparator);
	}
	
	@Override
	public void tick(int pass)
	{
		super.tick(pass);
		// Update caches of roles before roles are run
		if (pass == 1)
		{
			updateTravelingCache();
			if (roles != null)
			{
				for (int roleIndex = 0; roleIndex < roles.length; roleIndex++)
				{
					LogisticsRole role = roles[roleIndex];
					if (role != null)
					{
						role.updateCaches(this, filters[roleIndex]);
					}
				}
			}
		}
	}
	
	@Override
	public void handleItemSending() {

		handlePendingItems();
		
		IItemHandler simulatedInv = getCachedInv();
		
		HashMap<ILogisticator, Route> network = new HashMap<ILogisticator, Route>();
		
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
		
		if (roles != null)
		{
			// Perform logistics roles
			for (int roleIndex = 0; roleIndex < roles.length; roleIndex++)
			{
				LogisticsRole role = roles[roleIndex];
				FilterLogic filter = filters[roleIndex];
				if (role != null)
					role.performRole(this, filter, network);
			}
		}
	}
	
	@Override
	public Object getGuiClient(InventoryPlayer inventory) {

		return new GuiLogisticator(inventory, this);
	}
	
	@Override
	public Object getGuiServer(InventoryPlayer inventory) {

		return new ContainerLogisticator(inventory, this, 0);
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

	/* NBT METHODS */
	
	// Read from NBT tag without calling superclass' readFromNBT
	private void readFromNBTNoSuper(NBTTagCompound tag)
	{
		if (filters == null)
			createRoles();
		
		// Read filters from NBT tag
		for (int i = 0; i < filters.length; i++)
		{
			if (tag.hasKey("Filter" + i))
			{
				FilterLogic curFilter = createFilterLogic();
				curFilter.readFromNBT(tag.getCompoundTag("Filter" + i));
				filters[i] = curFilter;
			}
		}
		// Read roles from NBT tag
		for (int i = 0; i < roles.length; i++)
		{
			if (tag.hasKey("Role" + i))
			{
				roles[i] = LDRoleRegistry.createRole(tag.getString("Role" + i));
			}
		}
	}
	
	// Write to NBT tag without calling superclass' writeToNBT
	private void writeToNBTNoSuper(NBTTagCompound tag)
	{
		if (filters == null)
			createRoles();
		
		// Write filters to NBT tag
		for (int i = 0; i < filters.length; i++)
		{
			NBTTagCompound curFilterTag = new NBTTagCompound();
			filters[i].writeToNBT(curFilterTag);
			tag.setTag("Filter" + i, curFilterTag);
		}
		// Write roles to NBT tag
		for (int i = 0; i < roles.length; i++)
		{
			if (roles[i] != null)
				tag.setString("Role" + i, roles[i].getName());
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {

		super.readFromNBT(tag);
		readFromNBTNoSuper(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {

		super.writeToNBT(tag);
		writeToNBTNoSuper(tag);
	}

	/* IPortableData */
	@Override
	public void readPortableData(EntityPlayer player, NBTTagCompound tag) {

		readFromNBTNoSuper(tag);
		super.readPortableData(player, tag);
	}
	
	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		super.writePortableData(player, tag);
		tag.setString("DisplayType", "item.logisticsducts.logisticator.0.name");
		writeToNBTNoSuper(tag);
	}

	@Override
	public void receiveGuiNetworkData(int i, int j) {

		if (roles == null)
			createRoles();
		
		if (i >= LD_WINDOW_PROPERTIES.ROLE_START && i < LD_WINDOW_PROPERTIES.ROLE_START + roles.length)
		{
			roles[i - LD_WINDOW_PROPERTIES.ROLE_START] = LDRoleRegistry.createRole(j);
		}
		else
		{
			super.receiveGuiNetworkData(i, j);
		}
	}
	
	@Override
	public void sendGuiNetworkData(Container container, List<IContainerListener> players, boolean newListener) {
		
		// Set role to 0 to avoid sending incorrect data
		setActiveRole(0);
		
		super.sendGuiNetworkData(container, players, newListener);
		for (int i = 0; i < roles.length; i++)
		{
			if (prevRoles[i] != roles[i] || newListener)
			{
				for (IContainerListener player : players)
				{
					player.sendWindowProperty(container, LD_WINDOW_PROPERTIES.ROLE_START + i, LDRoleRegistry.getRoleIndex(roles[i]));
				}
				prevRoles[i] = roles[i];
			}
		}
	}
	
	/**
	 * Sends as many of the requested item to the destination along the given route via the logistics network.
	 * @return The total number of items sent.
	 */
	@Override
	public int requestItems(Map<ILogisticator, Route> network, IGridTileRoute target, byte finalDir, ItemStack items, boolean ignoreMeta, boolean ignoreNBT)
	{
		// Copy items to prevent modifying the original stack, in case the caller didn't do this already
		items = items.copy();
		int sent = 0;
		if (roles != null)
		{
			for (int roleIndex = 0; roleIndex < roles.length; roleIndex++)
			{
				LogisticsRole role = roles[roleIndex];
				if (role != null)
				{
					FilterLogic filter = filters[roleIndex];
					
					int curSent = role.requestItems(this, filter, target, finalDir, items, ignoreMeta, ignoreNBT);
					sent += curSent;
					if (items.getCount() > curSent)
						items.shrink(curSent);
					else
						break;
				}
			}
		}
		return sent;
	}
	
	@Override
	public List<ItemStack> getProvidedItems()
	{
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		if (roles != null)
		{
			for (int roleIndex = 0; roleIndex < roles.length; roleIndex++)
			{
				LogisticsRole role = roles[roleIndex];
				FilterLogic filter = filters[roleIndex];
				if (role != null)
				{
					Collection<ItemStack> newStacks = role.getProvidedItems(this, filter);
					if (newStacks != null)
					{
						for (ItemStack curStack : newStacks)
						{
							boolean added = false;
							for (int i = 0; i < stacks.size(); i++)
							{
								if (ItemHandlerHelper.canItemStacksStack(curStack, stacks.get(i)))
								{
									added = true;
									stacks.get(i).grow(curStack.getCount());
									break;
								}
							}
							if (!added)
								stacks.add(curStack);
						}
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
	@Override
	public int acceptsItems(ItemStack items)
	{
		int numAccepted = 0;
		
		if (roles != null)
		{
			for (int roleIndex = 0; roleIndex < roles.length; roleIndex++)
			{
				LogisticsRole role = roles[roleIndex];
				FilterLogic filter = filters[roleIndex];
				if (role != null)
				{
					numAccepted += role.acceptsItems(this, filter, items);
					if (numAccepted > items.getCount()) 
					{
						return items.getCount();
					}
				}
			}
		}
		return numAccepted;
	}
	
	public List<ItemStack> getTravelingItemsSorted()
	{
		return Collections.unmodifiableList(sortedTraveling);
	}

	/**
	 * Indicates to this logisticator that an item is pending delivery.
	 * @param traveling The item traveling towards this logisticator.
	 */
	@Override
	public void addPendingItem(TravelingItem traveling) {
		pending.add(traveling);
	}

	@Override
	public void handleStuffedItems() {

		FilterLogic curFilter = filter;
		
		
		for (Iterator<ItemStack> iterator = stuffedItems.iterator(); iterator.hasNext(); ) {
			ItemStack stuffedItem = iterator.next();
			int accepted = acceptsItems(stuffedItem);
			if (accepted > 0)
			{
				stuffedItem.setCount(itemDuct.insertIntoInventory(stuffedItem, side));
				if (stuffedItem.getCount() <= 0) {
					iterator.remove();
					break;
				}
			}
		}

		// Create temporary filter to ensure superclasses don't accept any stuffed items
		filter = createFilterLogic();
		// Set filter to whitelist
		filter.setFlag(0, false);
		super.handleStuffedItems();
		// Restore old filter
		filter = curFilter;
	}
	
	
	/*
	 * Window property numbers for sendGuiNetworkData/receiveGuiNetworkData
	 * Should be at least FilterLogic.defaultLevels.length + 1
	 */
	public static class LD_WINDOW_PROPERTIES {
		public final static short ROLE_START = 5000;
	}

	/* More network IDs for use in LogisticsDucts
	 * Should be compatible with ThermalDynamics' ConnectionBase.NETWORK_ID
	 */
	public static class LD_NETWORK_ID {

		public final static byte ROLE = 70;
		public final static byte PLAYERTAB = 71;

	}

}
