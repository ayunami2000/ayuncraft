package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

public class ItemFirework extends Item {
	public ItemFirework(int par1) {
		super(par1);
	}

	/**
	 * Callback for item usage. If the item does something special on right
	 * clicking, he will have one of those. Return True if something happen and
	 * false if it don't. This is for ITEMS, not BLOCKS
	 */
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
		if (!par3World.isRemote) {
			EntityFireworkRocket var11 = new EntityFireworkRocket(par3World, (double) ((float) par4 + par8), (double) ((float) par5 + par9), (double) ((float) par6 + par10), par1ItemStack);
			par3World.spawnEntityInWorld(var11);

			if (!par2EntityPlayer.capabilities.isCreativeMode) {
				--par1ItemStack.stackSize;
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		if (par1ItemStack.hasTagCompound()) {
			NBTTagCompound var5 = par1ItemStack.getTagCompound().getCompoundTag("Fireworks");

			if (var5 != null) {
				if (var5.hasKey("Flight")) {
					par3List.add(StatCollector.translateToLocal("item.fireworks.flight") + " " + var5.getByte("Flight"));
				}

				NBTTagList var6 = var5.getTagList("Explosions");

				if (var6 != null && var6.tagCount() > 0) {
					for (int var7 = 0; var7 < var6.tagCount(); ++var7) {
						NBTTagCompound var8 = (NBTTagCompound) var6.tagAt(var7);
						ArrayList var9 = new ArrayList();
						ItemFireworkCharge.func_92107_a(var8, var9);

						if (var9.size() > 0) {
							for (int var10 = 1; var10 < var9.size(); ++var10) {
								var9.set(var10, "  " + (String) var9.get(var10));
							}

							par3List.addAll(var9);
						}
					}
				}
			}
		}
	}
}
