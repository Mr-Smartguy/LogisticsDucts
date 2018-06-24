package com.mrsmartguy.logisticsducts.gui;

import com.mrsmartguy.logisticsducts.gui.container.ContainerRecipe;
import com.mrsmartguy.logisticsducts.items.ItemLogisticsRecipe;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	
	public static final int GUI_RECIPE = 0;

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		
		Item inHand = null;

		// x is used to store the hand when the GUI is opened by an item
		if (x >= 0 && x < EnumHand.values().length)
		{
			EnumHand handIn = EnumHand.values()[x];
			inHand = player.getHeldItem(handIn).getItem();
		}
		
		switch (ID)
		{
			case GUI_RECIPE:
				if (inHand instanceof ItemLogisticsRecipe)
					return new GuiRecipe(new ContainerRecipe((ItemLogisticsRecipe) inHand, player.inventory));
		}
		return null;
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		
		Item inHand = null;

		// x is used to store the hand when the GUI is opened by an item
		if (x >= 0 && x < EnumHand.values().length)
		{
			EnumHand handIn = EnumHand.values()[x];
			inHand = player.getHeldItem(handIn).getItem();
		}
		
		switch (ID)
		{
			case GUI_RECIPE:
				if (inHand instanceof ItemLogisticsRecipe)
					return new ContainerRecipe((ItemLogisticsRecipe) inHand, player.inventory);
		}
		return null;
	}	

}
