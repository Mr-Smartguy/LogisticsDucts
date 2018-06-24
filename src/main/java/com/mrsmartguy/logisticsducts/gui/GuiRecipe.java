package com.mrsmartguy.logisticsducts.gui;

import com.mrsmartguy.logisticsducts.gui.container.ContainerRecipe;
import com.mrsmartguy.logisticsducts.items.ItemLogisticsRecipe;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiRecipe extends GuiContainer {
	
	private static final String BACKGROUND_LOC = "logisticsducts:textures/gui/item/recipe.png";
	private static final ResourceLocation BACKGROUND = new ResourceLocation(BACKGROUND_LOC);

	private static final int BG_WIDTH = 176;
	private static final int BG_HEIGHT = 166;
	
	public GuiRecipe(ContainerRecipe recipeItem) {
		super(recipeItem);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

		GlStateManager.color(1, 1, 1, 1);
		
		mc.renderEngine.bindTexture(BACKGROUND);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	
	}
	
	/**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

}
