package com.mrsmartguy.logisticsducts.proxy;

import com.mrsmartguy.logisticsducts.blocks.LDBlocks;
import com.mrsmartguy.logisticsducts.items.LDItems;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		MinecraftForge.EVENT_BUS.register(EventHandlerClient.INSTANCE);
	}
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		LDBlocks.initModels();
		LDItems.initModels();
	}
}
