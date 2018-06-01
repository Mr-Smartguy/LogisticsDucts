package com.mrsmartguy.logisticsducts.ducts.attachments;

import com.mrsmartguy.logisticsducts.LogisticsDucts;
import com.mrsmartguy.logisticsducts.items.LDItems;
import com.mrsmartguy.logisticsducts.textures.LDTextures;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.IconTransformation;
import cofh.thermaldynamics.duct.AttachmentRegistry;
import cofh.thermaldynamics.duct.attachments.retriever.RetrieverItem;
import cofh.thermaldynamics.duct.tiles.TileGrid;
import cofh.thermaldynamics.render.RenderDuct;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LogisticatorItem extends RetrieverItem {
	
	static {
		
	}
	
	private ResourceLocation RESOURCE_LOGISTICATOR_ITEM = new ResourceLocation(LogisticsDucts.MODID, "logisticator_item");
	
	public LogisticatorItem(TileGrid tile, byte side) {
		super(tile, side);
	}
	
	public LogisticatorItem(TileGrid tile, byte side, int type) {
		super(tile, side, type);
	}

	@Override
	public String getInfo() {

		return "tab.logisticsducts.logisticatorItem";
	}

	@Override
	public ResourceLocation getId() {

		//return AttachmentRegistry.RETRIEVER_ITEM;
		return LDAttachmentRegistry.LOGISTICATOR_ITEM;
	}

	@Override
	public ItemStack getPickBlock() {

		return new ItemStack(LDItems.itemLogisticator, 1, type);
	}

	@Override
	public String getName() {

		return "item.logisticsducts.logisticatorItem";
	}

	@Override
	@SideOnly (Side.CLIENT)
	public boolean render(IBlockAccess world, BlockRenderLayer layer, CCRenderState ccRenderState) {

		if (layer != BlockRenderLayer.SOLID) {
			return false;
		}

		Translation trans = Vector3.fromTileCenter(baseTile).translation();
		RenderDuct.modelConnection[isPowered ? 1 : 2][side].render(ccRenderState, trans, new IconTransformation(LDTextures.LOGISTICATOR_BASE[stuffed ? 1 : 0][type]));
		return true;
	}

	/* IPortableData */
	@Override
	public void writePortableData(EntityPlayer player, NBTTagCompound tag) {

		super.writePortableData(player, tag);
		tag.setString("DisplayType", "item.logisticsducts.logisticator.0.name");
	}

}
