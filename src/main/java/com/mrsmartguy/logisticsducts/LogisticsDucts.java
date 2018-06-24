package com.mrsmartguy.logisticsducts;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.mrsmartguy.logisticsducts.blocks.LDBlocks;
import com.mrsmartguy.logisticsducts.ducts.attachments.LDAttachmentRegistry;
import com.mrsmartguy.logisticsducts.gui.GuiHandler;
import com.mrsmartguy.logisticsducts.items.LDItems;
import com.mrsmartguy.logisticsducts.proxy.CommonProxy;
import com.mrsmartguy.logisticsducts.roles.LDRoleRegistry;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import cofh.thermaldynamics.ThermalDynamics;
import cofh.thermalexpansion.ThermalExpansion;

@Mod(modid = LogisticsDucts.MODID, name = LogisticsDucts.MODNAME, version = LogisticsDucts.MODVERSION, acceptedMinecraftVersions = "[1.12.2]",
		dependencies = ThermalDynamics.VERSION_GROUP + ThermalExpansion.VERSION_GROUP)
public class LogisticsDucts {
	public static final String MODID = "logisticsducts";
	public static final String MODNAME = "Logistics Ducts";
	public static final String MODVERSION = "0.0.1";

	public static final GuiHandler GUI_HANDLER = new GuiHandler();
	
	private Logger logger;
	
	@Mod.Instance(MODID)
	public static LogisticsDucts instance;
	
	@SidedProxy(
			serverSide = "com.mrsmartguy.logisticsducts.proxy.CommonProxy",
			clientSide = "com.mrsmartguy.logisticsducts.proxy.ClientProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		logger.log(Level.INFO, MODNAME + "is loading!");
		proxy.preInit(event);
		
		LDBlocks.preInit();
		LDItems.preInit();
		
		LDAttachmentRegistry.registerAttachments();
		LDRoleRegistry.registerRoles();
		
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, GUI_HANDLER);
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
