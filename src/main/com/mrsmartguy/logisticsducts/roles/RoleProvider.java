package com.mrsmartguy.logisticsducts.roles;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;

import cofh.thermaldynamics.multiblock.Route;
import net.minecraft.item.ItemStack;

public class RoleProvider extends LogisticsRole {

	@Override
	public void performRole(LogisticatorItem logisticator, Map<LogisticatorItem, Route> network) {
		// Providers have no active role.
	}

	@Override
	public int requestItems(LogisticatorItem logisticator, Route route, ItemStack items) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int acceptsItems(LogisticatorItem logisticator, ItemStack items) {
		// Providers do not accept any items.
		return 0;
	}

	@Override
	public List<ItemStack> getProvidedItems(LogisticatorItem logisticator) {
		// TODO Auto-generated method stub
		return null;
	}

}
