package com.mrsmartguy.logisticsducts.items;

import static cofh.core.util.helpers.RecipeHelper.addShapedRecipe;
import static cofh.core.util.helpers.RecipeHelper.addShapelessRecipe;

import com.mrsmartguy.logisticsducts.ducts.attachments.LogisticatorItem;

import cofh.thermaldynamics.ThermalDynamics;
import cofh.thermaldynamics.duct.Attachment;
import cofh.thermaldynamics.duct.attachments.retriever.RetrieverFluid;
import cofh.thermaldynamics.duct.attachments.retriever.RetrieverItem;
import cofh.thermaldynamics.duct.tiles.DuctToken;
import cofh.thermaldynamics.duct.tiles.TileGrid;
import cofh.thermaldynamics.item.ItemAttachment;
import cofh.thermaldynamics.item.ItemFilter;
import cofh.thermaldynamics.item.ItemServo;
import cofh.thermalfoundation.item.ItemMaterial;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemLogisticator extends ItemAttachment {

	public static EnumRarity[] rarity = { EnumRarity.UNCOMMON, EnumRarity.UNCOMMON, EnumRarity.RARE, EnumRarity.RARE, EnumRarity.RARE };
	public static ItemStack logisticatorBasic, logisticatorHardened, logisticatorReinforced, logisticatorSignalum, logisticatorResonant;
	
	public ItemLogisticator() {

		super();
		this.setUnlocalizedName("logisticsducts.logisticator");
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {

		return super.getUnlocalizedName(item) + "." + item.getItemDamage();
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

		if (isInCreativeTab(tab)) {
			for (int i = 0; i < 5; i++) {
				items.add(new ItemStack(this, 1, i));
			}
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {

		return rarity[stack.getItemDamage() % 5];
	}

	@Override
	public Attachment getAttachment(EnumFacing side, ItemStack stack, TileGrid tile) {

		int type = stack.getItemDamage() % 5;
		//if (tile.getDuct(DuctToken.FLUID) != null) {
		//	return new RetrieverFluid(tile, (byte) (side.ordinal() ^ 1), type);
		//}
		if (tile.getDuct(DuctToken.ITEMS) != null) {
			return new LogisticatorItem(tile, (byte) (side.ordinal() ^ 1), type);
		}
		return null;
	}

	@Override
	public boolean preInit() {

		ForgeRegistries.ITEMS.register(setRegistryName("logisticator"));
		//ThermalDynamics.proxy.addIModelRegister(this);

		logisticatorBasic = new ItemStack(this, 1, 0);
		logisticatorHardened = new ItemStack(this, 1, 1);
		logisticatorReinforced = new ItemStack(this, 1, 2);
		logisticatorSignalum = new ItemStack(this, 1, 3);
		logisticatorResonant = new ItemStack(this, 1, 4);

		return true;
	}
	
	@Override
	public boolean initialize() {
		
		addShapedRecipe(logisticatorBasic,
				"IGI",
				"FRS",
				'F', ItemFilter.filterBasic,
				'I', "ingotIron",
				'S', ItemServo.servoBasic,
				'G', "blockGlass",
				'R', ItemMaterial.redstoneServo
		);

		addShapedRecipe(logisticatorHardened,
				"IGI",
				"FRS",
				'F', ItemFilter.filterHardened,
				'I', "ingotIron",
				'S', ItemServo.servoHardened,
				'G', "blockGlass",
				'R', ItemMaterial.redstoneServo
		);
		addShapelessRecipe(logisticatorHardened, logisticatorBasic, "ingotInvar", "ingotInvar");

		addShapedRecipe(logisticatorReinforced,
				"IGI",
				"FRS",
				'F', ItemFilter.filterReinforced,
				'I', "ingotIron",
				'S', ItemServo.servoReinforced,
				'G', "blockGlass",
				'R', ItemMaterial.redstoneServo
		);
		addShapelessRecipe(logisticatorReinforced, logisticatorBasic, "ingotElectrum", "ingotElectrum");
		addShapelessRecipe(logisticatorReinforced, logisticatorHardened, "ingotElectrum", "ingotElectrum");

		addShapedRecipe(logisticatorSignalum,
				"IGI",
				"FRS",
				'F', ItemFilter.filterSignalum,
				'I', "ingotIron",
				'S', ItemServo.servoSignalum,
				'G', "blockGlass",
				'R', ItemMaterial.redstoneServo
		);
		addShapelessRecipe(logisticatorSignalum, logisticatorBasic, "ingotSignalum", "ingotSignalum");
		addShapelessRecipe(logisticatorSignalum, logisticatorHardened, "ingotSignalum", "ingotSignalum");
		addShapelessRecipe(logisticatorSignalum, logisticatorReinforced, "ingotSignalum", "ingotSignalum");

		addShapedRecipe(logisticatorResonant,
				"IGI",
				"FRS",
				'F', ItemFilter.filterResonant,
				'I', "ingotIron",
				'S', ItemServo.servoResonant,
				'G', "blockGlass",
				'R', ItemMaterial.redstoneServo
		);
		addShapelessRecipe(logisticatorResonant, logisticatorBasic, "ingotEnderium", "ingotEnderium");
		addShapelessRecipe(logisticatorResonant, logisticatorHardened, "ingotEnderium", "ingotEnderium");
		addShapelessRecipe(logisticatorResonant, logisticatorReinforced, "ingotEnderium", "ingotEnderium");
		addShapelessRecipe(logisticatorResonant, logisticatorSignalum, "ingotEnderium", "ingotEnderium");
		
		
		return true;
	}

	@Override
	@SideOnly (Side.CLIENT)
	public void registerModels() {

		String[] names = { "basic", "hardened", "reinforced", "signalum", "resonant" };
		for (int i = 0; i < names.length; i++) {
			ModelResourceLocation location = new ModelResourceLocation("logisticsducts:attachment", "type=" + this.getRegistryName().getResourcePath() + "_" + names[i]);
			ModelLoader.setCustomModelResourceLocation(this, i, location);
		}
	}


}
