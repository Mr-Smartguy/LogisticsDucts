package com.mrsmartguy.logisticsducts.gui;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;
import com.mrsmartguy.logisticsducts.gui.container.ContainerLogisticator;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.ElementButton;
import cofh.thermaldynamics.gui.client.GuiDuctConnection;
import cofh.thermaldynamics.gui.container.ContainerDuctConnection;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiLogisticator extends GuiDuctConnection {

	private static final String BUTTON_TEX_PATH = "logisticsducts:textures/gui/elements/buttons_logisticator.png";
	
	private static final String ROLE_LEFT_NAME = "RoleLeft";
	private static final String ROLE_RIGHT_NAME = "RoleRight";
	
	private ContainerDuctConnection superContainer;
	private ContainerLogisticator container;
	private Field filterField;
	
	private ElementButton roleLeftButton;
	private ElementButton roleRightButton;

	public GuiLogisticator(InventoryPlayer inventory, LogisticatorItem conBase) {
		super(inventory, conBase);
		// Overwrite the container to ensure that rendering uses the Logisticator container
		this.inventorySlots = new ContainerLogisticator(inventory, conBase, 0);
		this.container = (ContainerLogisticator) inventorySlots;
		// Use reflection to get the filter field of GuiDuctConnection's container field
		// GuiDuctConnection::container is private, so we have to use reflection to access it
		// ContainerDuctConnection::filter is final, so we have to use reflection to modify it
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
			
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void initGui() {

		super.initGui();
		addButtons();
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
	
	private void tryDecrementRole()
	{
		
	}
	
	private void tryIncrementRole()
	{
		
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

}
