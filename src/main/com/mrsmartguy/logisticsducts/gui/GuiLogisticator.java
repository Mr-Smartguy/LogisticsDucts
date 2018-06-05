package com.mrsmartguy.logisticsducts.gui;

import com.mrsmartguy.logisticsducts.gui.container.ContainerLogisticator;

import cofh.thermaldynamics.duct.attachments.ConnectionBase;
import cofh.thermaldynamics.gui.client.GuiDuctConnection;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class GuiLogisticator extends GuiDuctConnection {

	public GuiLogisticator(InventoryPlayer inventory, ConnectionBase conBase) {
		super(inventory, conBase);
		// Overwrite the container to ensure that rendering uses the Logisticator container
		this.inventorySlots = new ContainerLogisticator(inventory, conBase);
	}

}
