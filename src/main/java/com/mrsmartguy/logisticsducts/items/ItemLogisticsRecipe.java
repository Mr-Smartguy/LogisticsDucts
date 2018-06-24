package com.mrsmartguy.logisticsducts.items;

import com.mrsmartguy.logisticsducts.LogisticsDucts;
import com.mrsmartguy.logisticsducts.gui.GuiHandler;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemLogisticsRecipe extends Item {
	
	public ItemLogisticsRecipe() {
		
		super();
		this.setUnlocalizedName("logisticsducts.logisticsrecipe");
		this.setRegistryName("logisticsrecipe");
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		
		playerIn.openGui(LogisticsDucts.instance, GuiHandler.GUI_RECIPE, worldIn, handIn.ordinal(), playerIn.chunkCoordY, playerIn.chunkCoordZ);
		
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	public void initialize() {
		
		
	}
	
	@SideOnly(Side.CLIENT)
	public void registerModels()
	{
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

}
