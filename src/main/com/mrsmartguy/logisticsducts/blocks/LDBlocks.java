package com.mrsmartguy.logisticsducts.blocks;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LDBlocks {
	
	@ObjectHolder("logisticsducts:testblock")
	public static TestBlock testBlock;
	
	public static void preInit() { }	

	@SideOnly(Side.CLIENT)
	public static void initModels()
	{
		testBlock.initModel();
	}
}
