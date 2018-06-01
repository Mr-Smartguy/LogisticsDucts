package com.mrsmartguy.logisticsducts.ducts.attachments;

import com.mrsmartguy.logisticsducts.LogisticsDucts;
import com.mrsmartguy.logisticsducts.items.ItemLogisticator;

import cofh.thermaldynamics.duct.AttachmentRegistry;
import net.minecraft.util.ResourceLocation;;

public class LDAttachmentRegistry {
	
	public final static ResourceLocation LOGISTICATOR_ITEM = new ResourceLocation(LogisticsDucts.MODID, "logisticator_item");
	
	static {
		AttachmentRegistry.registerAttachment(LOGISTICATOR_ITEM, LogisticatorItem::new);
	}

}
