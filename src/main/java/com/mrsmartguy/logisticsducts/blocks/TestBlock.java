package com.mrsmartguy.logisticsducts.blocks;

import com.mrsmartguy.logisticsducts.LogisticsDucts;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TestBlock extends Block {
	
	public TestBlock() {
		super(Material.ROCK);
		setUnlocalizedName(LogisticsDucts.MODID + ".testblock");
		setRegistryName("testblock");
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelResourceLocation loc = new ModelResourceLocation(getRegistryName(), "inventory");
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
		
	}

}
