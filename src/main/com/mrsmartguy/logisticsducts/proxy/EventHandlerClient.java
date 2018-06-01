package com.mrsmartguy.logisticsducts.proxy;

import com.mrsmartguy.logisticsducts.textures.LDTextures;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerClient {

	public static final EventHandlerClient INSTANCE = new EventHandlerClient();

	@SubscribeEvent
	public void handleTextureStitchEventPre(TextureStitchEvent.Pre event) {

		LDTextures.registerTextures(event.getMap());

		/*for (int i = 0; i < TDDucts.ductList.size(); i++) {
			if (TDDucts.isValid(i)) {
				TDDucts.ductList.get(i).registerIcons(event.getMap());
			}
		}
		TDDucts.structureInvis.registerIcons(event.getMap());*/
	}

}
