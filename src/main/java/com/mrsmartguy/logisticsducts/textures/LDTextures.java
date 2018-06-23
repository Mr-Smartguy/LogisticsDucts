package com.mrsmartguy.logisticsducts.textures;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public class LDTextures {
	
	public static void registerTextures(TextureMap map)
	{
		textureMap = map;
		
		LOGISTICATOR_BASE_0_0 =    register(LOGISTICATOR_ + "0_0");
		LOGISTICATOR_BASE_0_1 =    register(LOGISTICATOR_ + "0_1");
		LOGISTICATOR_BASE_0_2 =    register(LOGISTICATOR_ + "0_2");
		LOGISTICATOR_BASE_0_3 =    register(LOGISTICATOR_ + "0_3");
		LOGISTICATOR_BASE_0_4 =    register(LOGISTICATOR_ + "0_4");
		LOGISTICATOR_BASE_1_0 =    register(LOGISTICATOR_ + "1_0");
		LOGISTICATOR_BASE_1_1 =    register(LOGISTICATOR_ + "1_1");
		LOGISTICATOR_BASE_1_2 =    register(LOGISTICATOR_ + "1_2");
		LOGISTICATOR_BASE_1_3 =    register(LOGISTICATOR_ + "1_3");
		LOGISTICATOR_BASE_1_4 =    register(LOGISTICATOR_ + "1_4");
		
		LOGISTICATOR_BASE = new TextureAtlasSprite[][] {
			new TextureAtlasSprite[] {
					LOGISTICATOR_BASE_0_0,
					LOGISTICATOR_BASE_0_1,
					LOGISTICATOR_BASE_0_2,
					LOGISTICATOR_BASE_0_3,
					LOGISTICATOR_BASE_0_4,
			},
			new TextureAtlasSprite[] {
					LOGISTICATOR_BASE_1_0,
					LOGISTICATOR_BASE_1_1,
					LOGISTICATOR_BASE_1_2,
					LOGISTICATOR_BASE_1_3,
					LOGISTICATOR_BASE_1_4,
			}
		};
	}

	/* HELPERS */
	private static TextureAtlasSprite register(String sprite) {

		return textureMap.registerSprite(new ResourceLocation(sprite));
	}

	private static TextureMap textureMap;

	private static final String BLOCKS_ = "logisticsducts:blocks/";
	private static final String DUCT_ATTACHMENT_ = BLOCKS_ + "duct/attachment/";
	private static final String LOGISTICATOR_ = DUCT_ATTACHMENT_ + "logisticator/logisticator_base_";
	
	public static TextureAtlasSprite[][] LOGISTICATOR_BASE;
	public static TextureAtlasSprite LOGISTICATOR_BASE_0_0;
	public static TextureAtlasSprite LOGISTICATOR_BASE_0_1;
	public static TextureAtlasSprite LOGISTICATOR_BASE_0_2;
	public static TextureAtlasSprite LOGISTICATOR_BASE_0_3;
	public static TextureAtlasSprite LOGISTICATOR_BASE_0_4;
	public static TextureAtlasSprite LOGISTICATOR_BASE_1_0;
	public static TextureAtlasSprite LOGISTICATOR_BASE_1_1;
	public static TextureAtlasSprite LOGISTICATOR_BASE_1_2;
	public static TextureAtlasSprite LOGISTICATOR_BASE_1_3;
	public static TextureAtlasSprite LOGISTICATOR_BASE_1_4;

}
