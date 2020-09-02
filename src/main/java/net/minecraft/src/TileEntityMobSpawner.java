package net.minecraft.src;

public class TileEntityMobSpawner extends TileEntity {

	/**
	 * Reads a tile entity from NBT.
	 */
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
	}

	/**
	 * Allows the entity to update its state. Overridden in most subclasses, e.g.
	 * the mob spawner uses this to count ticks and creates a new spawn inside its
	 * implementation.
	 */
	public void updateEntity() {
		super.updateEntity();
		
		if(worldObj.isRemote && worldObj.rand.nextFloat() < 0.2f) {
			worldObj.spawnParticle("flame", this.xCoord + worldObj.rand.nextFloat(), this.yCoord + worldObj.rand.nextFloat(), this.zCoord + worldObj.rand.nextFloat(), 0.0D, 0.0D, 0.0D);
			worldObj.spawnParticle("flame", this.xCoord + worldObj.rand.nextFloat(), this.yCoord + worldObj.rand.nextFloat(), this.zCoord + worldObj.rand.nextFloat(), 0.0D, 0.0D, 0.0D);
		}
	}

	/**
	 * Overriden in a sign to provide the text.
	 */
	public Packet getDescriptionPacket() {
		NBTTagCompound var1 = new NBTTagCompound();
		this.writeToNBT(var1);
		var1.removeTag("SpawnPotentials");
		return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, var1);
	}

}
