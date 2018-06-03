package com.mrsmartguy.logisticsducts;

import com.mrsmartguy.logisticsducts.blocks.LDBlocks;
import com.mrsmartguy.logisticsducts.items.LDItems;
import com.mrsmartguy.logisticsducts.proxy.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import cofh.thermaldynamics.ThermalDynamics;

@Mod(modid = LogisticsDucts.MODID, name = LogisticsDucts.MODNAME, version = LogisticsDucts.MODVERSION, acceptedMinecraftVersions = "[1.12.2]",
		dependencies = ThermalDynamics.VERSION_GROUP)
public class LogisticsDucts {
	public static final String MODID = "logisticsducts";
	public static final String MODNAME = "Logistics Ducts";
	public static final String MODVERSION = "0.0.1";
	
	@Mod.Instance(MODID)
	public static LogisticsDucts instance;
	
	@SidedProxy(
			serverSide = "com.mrsmartguy.logisticsducts.proxy.CommonProxy",
			clientSide = "com.mrsmartguy.logisticsducts.proxy.ClientProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		System.out.println(MODNAME + " is loading!");
		LDBlocks.preInit();
		LDItems.preInit();
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}
