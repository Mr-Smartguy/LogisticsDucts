package com.mrsmartguy.logisticsducts.items;

import com.mrsmartguy.logisticsducts.blocks.TestBlock;
import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;

import cofh.core.util.core.IInitializer;
import cofh.thermaldynamics.item.ItemCover;
import cofh.thermaldynamics.item.ItemFilter;
import cofh.thermaldynamics.item.ItemRelay;
import cofh.thermaldynamics.item.ItemRetriever;
import cofh.thermaldynamics.item.ItemServo;
import cofh.thermalfoundation.init.TFProps;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LDItems {
	
	private static final LDItems INSTANCE = new LDItems();
	
	@ObjectHolder("logisticsducts:itemlogisticator")
	public static ItemLogisticator itemLogisticator;
	//@ObjectHolder("logisticsducts:itemlogisticspacket")
	public static ItemLogisticsPacket itemLogisticsPacket;
	
	public static void preInit() {

		itemLogisticator = new ItemLogisticator();
		itemLogisticsPacket = new ItemLogisticsPacket();
		
		itemLogisticator.preInit();
		//itemLogisticsPacket.preInit();

		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}
	
	@SideOnly(Side.CLIENT)
	public static void initModels()
	{
		itemLogisticator.registerModels();
	}

}
