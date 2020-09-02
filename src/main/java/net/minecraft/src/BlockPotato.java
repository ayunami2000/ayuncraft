package net.minecraft.src;

public class BlockPotato extends BlockCrops {
	private Icon[] iconArray;

	public BlockPotato(int par1) {
		super(par1);
	}

	/**
	 * From the specified side and block metadata retrieves the blocks texture.
	 * Args: side, metadata
	 */
	public Icon getIcon(int par1, int par2) {
		if (par2 < 7) {
			if (par2 == 6) {
				par2 = 5;
			}

			return this.iconArray[par2 >> 1];
		} else {
			return this.iconArray[3];
		}
	}

	/**
	 * Generate a seed ItemStack for this crop.
	 */
	protected int getSeedItem() {
		return Item.potato.itemID;
	}

	/**
	 * Generate a crop produce ItemStack for this crop.
	 */
	protected int getCropItem() {
		return Item.potato.itemID;
	}

	/**
	 * Drops the block items with a specified chance of dropping the specified items
	 */
	public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7) {
		super.dropBlockAsItemWithChance(par1World, par2, par3, par4, par5, par6, par7);

		if (!par1World.isRemote) {
			if (par5 >= 7 && par1World.rand.nextInt(50) == 0) {
				this.dropBlockAsItem_do(par1World, par2, par3, par4, new ItemStack(Item.poisonousPotato));
			}
		}
	}

	/**
	 * When this method is called, your block should register all the icons it needs
	 * with the given IconRegister. This is the only chance you get to register
	 * icons.
	 */
	public void registerIcons(IconRegister par1IconRegister) {
		this.iconArray = new Icon[4];

		for (int var2 = 0; var2 < this.iconArray.length; ++var2) {
			this.iconArray[var2] = par1IconRegister.registerIcon("potatoes_" + var2);
		}
	}
}
