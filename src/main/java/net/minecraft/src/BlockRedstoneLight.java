package net.minecraft.src;

import java.util.Random;

public class BlockRedstoneLight extends Block {
	/** Whether this lamp block is the powered version. */
	private final boolean powered;

	public BlockRedstoneLight(int par1, boolean par2) {
		super(par1, Material.redstoneLight);
		this.powered = par2;

		if (par2) {
			this.setLightValue(1.0F);
		}
	}

	/**
	 * When this method is called, your block should register all the icons it needs
	 * with the given IconRegister. This is the only chance you get to register
	 * icons.
	 */
	public void registerIcons(IconRegister par1IconRegister) {
		if (this.powered) {
			this.blockIcon = par1IconRegister.registerIcon("redstoneLight_lit");
		} else {
			this.blockIcon = par1IconRegister.registerIcon("redstoneLight");
		}
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	public void onBlockAdded(World par1World, int par2, int par3, int par4) {
		if (!par1World.isRemote) {
			if (this.powered && !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4)) {
				par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, 4);
			} else if (!this.powered && par1World.isBlockIndirectlyGettingPowered(par2, par3, par4)) {
				par1World.setBlock(par2, par3, par4, Block.redstoneLampActive.blockID, 0, 2);
			}
		}
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which
	 * neighbor changed (coordinates passed are their own) Args: x, y, z, neighbor
	 * blockID
	 */
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
		if (!par1World.isRemote) {
			if (this.powered && !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4)) {
				par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, 4);
			} else if (!this.powered && par1World.isBlockIndirectlyGettingPowered(par2, par3, par4)) {
				par1World.setBlock(par2, par3, par4, Block.redstoneLampActive.blockID, 0, 2);
			}
		}
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
		if (!par1World.isRemote && this.powered && !par1World.isBlockIndirectlyGettingPowered(par2, par3, par4)) {
			par1World.setBlock(par2, par3, par4, Block.redstoneLampIdle.blockID, 0, 2);
		}
	}

	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	public int idDropped(int par1, Random par2Random, int par3) {
		return Block.redstoneLampIdle.blockID;
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to
	 * inventory.setCurrentItem (along with isCreative)
	 */
	public int idPicked(World par1World, int par2, int par3, int par4) {
		return Block.redstoneLampIdle.blockID;
	}
}
