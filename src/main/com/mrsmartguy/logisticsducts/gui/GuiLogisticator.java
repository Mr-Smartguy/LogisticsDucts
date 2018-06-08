package com.mrsmartguy.logisticsducts.gui;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;
import com.mrsmartguy.logisticsducts.gui.container.ContainerLogisticator;
import com.mrsmartguy.logisticsducts.roles.LogisticsRole;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementBase;
import cofh.core.gui.element.ElementButton;
import cofh.core.util.helpers.StringHelper;
import cofh.thermaldynamics.duct.attachments.filter.FilterLogic;
import cofh.thermaldynamics.gui.client.GuiDuctConnection;
import cofh.thermaldynamics.gui.container.ContainerDuctConnection;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiLogisticator extends GuiDuctConnection {

	static final String BG_TEX_PATH = "thermaldynamics:textures/gui/connection.png";
	static final ResourceLocation BG_TEXTURE = new ResourceLocation(BG_TEX_PATH);

	private static final String BUTTON_TEX_PATH = "logisticsducts:textures/gui/elements/buttons_logisticator.png";
	
	private static final String ROLE_LEFT_NAME = "RoleLeft";
	private static final String ROLE_RIGHT_NAME = "RoleRight";
	
	private ContainerDuctConnection superContainer;
	private ContainerLogisticator container;
	private Field filterField;
	private LogisticatorItem logisticator;
	private Field isItemServoField;
	private Field isAdvItemFilterField;
	
	private ElementButton roleLeftButton;
	private ElementButton roleRightButton;

	public GuiLogisticator(InventoryPlayer inventory, LogisticatorItem conBase) {
		super(inventory, conBase);
		this.logisticator = conBase;
		// Overwrite the container to ensure that rendering uses the Logisticator container
		this.inventorySlots = new ContainerLogisticator(inventory, conBase, 0);
		this.container = (ContainerLogisticator) inventorySlots;
		// Use reflection to get the filter field of GuiDuctConnection's container field
		// GuiDuctConnection::container is private, so we have to use reflection to access it
		// ContainerDuctConnection::filter is final, so we have to use reflection to modify it
		// Also get the isItemServo and isAdvItemFilter fields so we can disable drawing the text
		try {
			// Get the container field of this as a GuiDuctConnection
			Field superContainerField = GuiDuctConnection.class.getDeclaredField("container");
			superContainerField.setAccessible(true);
			// Get the container held in the field
			superContainer = (ContainerDuctConnection) superContainerField.get(this);
			// Get the filter field of the container
			filterField = ContainerDuctConnection.class.getDeclaredField("filter");
			// Get the modifiers field of the filter field and set it to not be final so we can modify it
			Field modifierField = filterField.getClass().getDeclaredField("modifiers");
			modifierField.setAccessible(true);
			int modifiers = filterField.getModifiers();
			modifiers = modifiers & ~Modifier.FINAL;
			modifierField.set(filterField, modifiers);
			// Set the filter
			filterField.set(superContainer, container.filters[0]);	
			// Get the isItemServo and isAdvItemFilter fields
			isItemServoField = GuiDuctConnection.class.getDeclaredField("isItemServo");
			isItemServoField.setAccessible(true);
			isAdvItemFilterField = GuiDuctConnection.class.getDeclaredField("isAdvItemFilter");
			isAdvItemFilterField.setAccessible(true);
			
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void setElementVisible(ElementBase element, boolean visible)
	{
		if (element != null)
		{
			element.setEnabled(visible);
			element.setVisible(visible);
		}
	}
	
	private void setStackSizeTextEnabled(boolean enabled)
	{
		try {
			isItemServoField.set(this, enabled);
			isAdvItemFilterField.set(this, enabled);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Called when the gui buttons need to be enabled/disabled (when a role is added, deleted, etc.)
	 */
	private void updateButtonEnabledStates()
	{
		boolean enabled = container.activeRoleExists();
		for (ElementButton element : levelButtons)
		{
			setElementVisible(element, enabled);
		}
		for (ElementButton element : flagButtons)
		{
			setElementVisible(element, enabled);
		}

		setElementVisible(decStackSize, enabled);
		setElementVisible(incStackSize, enabled);

		setElementVisible(decRetainSize, enabled);
		setElementVisible(incRetainSize, enabled);
		
		setStackSizeTextEnabled(enabled);
	}
	
	private void roleChanged()
	{
		// Update the filter
		setContainerFilter(logisticator.getFilters()[container.getActiveRoleIndex()]);
		
		updateButtonEnabledStates();
	}
	
	@Override
	public void initGui() {

		super.initGui();
		addButtons();
		
		roleChanged();
	}
	
	private void setContainerFilter(FilterLogic newFilter)
	{
		try {
			filterField.set(superContainer, newFilter);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void addButtons()
	{
		roleLeftButton = new ElementButton(this,
				17, 4,  // Pos
				ROLE_LEFT_NAME,
				0, 0,   // sheet pos
				0, 11,  // hover pos
				0, 22,  // disabled pos
				14, 11, // size
				BUTTON_TEX_PATH);
		roleLeftButton.setTexture(BUTTON_TEX_PATH, 32, 64);
		roleRightButton = new ElementButton(this,
				145, 4, // Pos
				ROLE_RIGHT_NAME,
				14, 0,  // sheet pos
				14, 11, // hover pos
				14, 22, // disabled pos
				14, 11, // size
				BUTTON_TEX_PATH);
		roleRightButton.setTexture(BUTTON_TEX_PATH, 32, 64);
		addElement(roleLeftButton);
		addElement(roleRightButton);
	}
	
	private boolean canDecrement()
	{
		// Check if the first role is selected or if no roles exist yet
		if (container.getActiveRoleIndex() <= 0)
			return false;
		return true;
	}
	
	private boolean canIncrement()
	{
		// Check if the last possible role is selected
		if (container.getActiveRoleIndex() == LogisticatorItem.numRoles[logisticator.type] - 1)
			return false;
		// Check if no roles exist yet
		if (container.getActiveRoleIndex() == -1)
			return false;
		// Check if the current role doesn't exist yet
		if (!container.activeRoleExists())
			return false;
		return true;
	}
	
	private void decrement()
	{
		container.setActiveRoleIndex(container.getActiveRoleIndex() - 1);
		roleChanged();
	}
	
	private void increment()
	{
		container.setActiveRoleIndex(container.getActiveRoleIndex() + 1);
		roleChanged();
	}
	
	/**
	 * Attempts to decrement the selected role index, ensuring that decrementing is currently possible.
	 */
	private void tryDecrementRole()
	{
		if (canDecrement())
		{
			decrement();
		}
	}

	/**
	 * Attempts to increment the selected role index, ensuring that incrementing is currently possible.
	 */
	private void tryIncrementRole()
	{
		if (canIncrement())
		{
			increment();
		}
	}
	
	@Override
	protected void updateElementInformation() {
		super.updateElementInformation();
		
		roleLeftButton.setEnabled(canDecrement());
		roleRightButton.setEnabled(canIncrement());
		
		int index = container.getActiveRoleIndex();
		
		if (container.activeRoleExists())
		{
			LogisticsRole role = logisticator.getRole(index);
			name = StringHelper.localize("item.logisticsducts.logisticator.role." + (index + 1)) +
					" " +
					StringHelper.localize("item.logisticsducts.logisticator.role." + role.getName());
		}
		else
		{
			name = StringHelper.localize("item.logisticsducts.logisticator.role." + (index + 1)) +
					" " +
					StringHelper.localize("item.logisticsducts.logisticator.role.none");
		}
	}
	
	@Override
	public void handleElementButtonClick(String buttonName, int mouseButton)
	{
		if (buttonName == ROLE_LEFT_NAME)
		{
			tryDecrementRole();
		}
		else if (buttonName == ROLE_RIGHT_NAME)
		{
			tryIncrementRole();
		}
		
		super.handleElementButtonClick(buttonName, mouseButton);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int x, int y) {

		super.drawGuiContainerBackgroundLayer(partialTick, x, y);

		// "Undo" drawing slots and text if the current active role does not exist
		if (!container.activeRoleExists())
		{
			coverBackground();
		}
	}
	
	private void coverBackground()
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		bindTexture(BG_TEXTURE);
		
		int x0 = container.gridX0 - 1;
		int y0 = container.gridY0 - 1;
		
		int w = container.gridWidth * 18;
		int h = container.gridHeight * 18;
		

		drawTexturedModalRect(guiLeft + x0, guiTop + y0, x0, y0, w, h);
	}

}
