package net.minecraft.src;

import net.lax1dude.eaglercraft.EaglerProfile;

import net.lax1dude.eaglercraft.ModelBipedNewSkins;
import net.lax1dude.eaglercraft.TextureLocation;
import net.lax1dude.eaglercraft.DefaultSkinRenderer;
import net.lax1dude.eaglercraft.EaglerAdapter;
import net.minecraft.client.Minecraft;

public class RenderPlayer extends RenderLiving {
	private ModelBiped modelBipedMain;
	private ModelBiped modelBipedMainNewSkin;
	private ModelBiped modelArmorChestplate;
	private ModelBiped modelArmor;
	private static final String[] armorFilenamePrefix = new String[] { "cloth", "chain", "iron", "diamond", "gold" };

	public RenderPlayer() {
		super(new ModelBiped(0.0F), 0.5F);
		this.modelBipedMain = (ModelBiped) this.mainModel;
		this.modelBipedMainNewSkin = new ModelBipedNewSkins(0.0F, false);
		this.modelArmorChestplate = new ModelBiped(1.0F);
		this.modelArmor = new ModelBiped(0.5F);
	}

	/**
	 * Set the specified armor model as the player model. Args: player, armorSlot,
	 * partialTick
	 */
	protected int setArmorModel(EntityPlayer par1EntityPlayer, int par2, float par3) {
		if(!DefaultSkinRenderer.isPlayerStandard(par1EntityPlayer)) {
			return -1;
		}
		ItemStack var4 = par1EntityPlayer.inventory.armorItemInSlot(3 - par2);

		if (var4 != null) {
			Item var5 = var4.getItem();

			if (var5 instanceof ItemArmor) {
				ItemArmor var6 = (ItemArmor) var5;
				this.loadTexture("/armor/" + armorFilenamePrefix[var6.renderIndex] + "_" + (par2 == 2 ? 2 : 1) + ".png");
				ModelBiped var7 = par2 == 2 ? this.modelArmor : this.modelArmorChestplate;
				var7.bipedHead.showModel = par2 == 0;
				var7.bipedHeadwear.showModel = par2 == 0;
				var7.bipedBody.showModel = par2 == 1 || par2 == 2;
				var7.bipedRightArm.showModel = par2 == 1;
				var7.bipedLeftArm.showModel = par2 == 1;
				var7.bipedRightLeg.showModel = par2 == 2 || par2 == 3;
				var7.bipedLeftLeg.showModel = par2 == 2 || par2 == 3;
				this.setRenderPassModel(var7);

				if (var7 != null) {
					var7.onGround = this.mainModel.onGround;
				}

				if (var7 != null) {
					var7.isRiding = this.mainModel.isRiding;
				}

				if (var7 != null) {
					var7.isChild = this.mainModel.isChild;
				}

				float var8 = 1.0F;

				if (var6.getArmorMaterial() == EnumArmorMaterial.CLOTH) {
					int var9 = var6.getColor(var4);
					float var10 = (float) (var9 >> 16 & 255) / 255.0F;
					float var11 = (float) (var9 >> 8 & 255) / 255.0F;
					float var12 = (float) (var9 & 255) / 255.0F;
					EaglerAdapter.glColor3f(var8 * var10, var8 * var11, var8 * var12);

					if (var4.isItemEnchanted()) {
						return 31;
					}

					return 16;
				}

				EaglerAdapter.glColor3f(var8, var8, var8);

				if (var4.isItemEnchanted()) {
					return 15;
				}

				return 1;
			}
		}

		return -1;
	}

	protected void func_82439_b(EntityPlayer par1EntityPlayer, int par2, float par3) {
		ItemStack var4 = par1EntityPlayer.inventory.armorItemInSlot(3 - par2);

		if (var4 != null) {
			Item var5 = var4.getItem();

			if (var5 instanceof ItemArmor) {
				ItemArmor var6 = (ItemArmor) var5;
				this.loadTexture("/armor/" + armorFilenamePrefix[var6.renderIndex] + "_" + (par2 == 2 ? 2 : 1) + "_b.png");
				float var7 = 1.0F;
				EaglerAdapter.glColor3f(var7, var7, var7);
			}
		}
	}
	
	private boolean renderPass2 = false;

	public void renderPlayer(EntityPlayer par1EntityPlayer, double par2, double par4, double par6, float par8, float par9) {
		if(DefaultSkinRenderer.isPlayerStandard(par1EntityPlayer)) {
			float var10 = 1.0F;
			EaglerAdapter.glColor3f(var10, var10, var10);
			ItemStack var11 = par1EntityPlayer.inventory.getCurrentItem();
			this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = this.modelBipedMainNewSkin.heldItemRight = var11 != null ? 1 : 0;
	
			if (var11 != null && par1EntityPlayer.getItemInUseCount() > 0) {
				EnumAction var12 = var11.getItemUseAction();
	
				if (var12 == EnumAction.block) {
					this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = this.modelBipedMainNewSkin.heldItemRight = 3;
				} else if (var12 == EnumAction.bow) {
					this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = this.modelBipedMainNewSkin.aimedBow = true;
				}
			}
	
			this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = this.modelBipedMainNewSkin.isSneak = par1EntityPlayer.isSneaking();
			double var14 = par4 - (double) par1EntityPlayer.yOffset;
	
			if (par1EntityPlayer.isSneaking() && !(par1EntityPlayer instanceof EntityPlayerSP)) {
				var14 -= 0.125D;
			}
			
			this.mainModel = (DefaultSkinRenderer.isPlayerNewSkin(par1EntityPlayer) ? this.modelBipedMainNewSkin : this.modelBipedMain);
			this.mainModel.isChild = false;
			super.doRenderLiving(par1EntityPlayer, par2, var14, par6, par8, par9);
			this.mainModel = this.modelBipedMain;
			this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = this.modelBipedMainNewSkin.aimedBow = false;
			this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = this.modelBipedMainNewSkin.isSneak = false;
			this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = this.modelBipedMainNewSkin.heldItemRight = 0;
		}else {
			int renderType = DefaultSkinRenderer.getPlayerRenderer(par1EntityPlayer);
			if(DefaultSkinRenderer.isZombieModel(renderType)) {
				if(DefaultSkinRenderer.zombieRenderer == null) DefaultSkinRenderer.zombieRenderer = new ModelZombie(0.0F, true);
				this.mainModel = DefaultSkinRenderer.zombieRenderer;
				this.mainModel.isChild = false;
				DefaultSkinRenderer.zombieRenderer.isSneak = par1EntityPlayer.isSneaking();
				DefaultSkinRenderer.zombieRenderer.isRiding = par1EntityPlayer.isRiding();
				double var14 = par4 - (double) par1EntityPlayer.yOffset;
				if (par1EntityPlayer.isSneaking() && !(par1EntityPlayer instanceof EntityPlayerSP)) var14 -= 0.125D;
				super.doRenderLiving(par1EntityPlayer, par2, var14, par6, par8, par9);
				DefaultSkinRenderer.zombieRenderer.isSneak = false;
				DefaultSkinRenderer.zombieRenderer.isRiding = false;
				this.mainModel = this.modelBipedMain;
			}else {
				switch(renderType) {
				case 32:
					if(DefaultSkinRenderer.villagerRenderer == null) DefaultSkinRenderer.villagerRenderer = new ModelVillager(0.0F);
					DefaultSkinRenderer.villagerRenderer.isChild = false;
					this.mainModel = DefaultSkinRenderer.villagerRenderer;
					super.doRenderLiving(par1EntityPlayer, par2, par4 - (double) par1EntityPlayer.yOffset, par6, par8, par9);
					this.mainModel = this.modelBipedMain;
					break;
				case 19:
					if(DefaultSkinRenderer.endermanRenderer == null) DefaultSkinRenderer.endermanRenderer = new ModelEnderman();
					DefaultSkinRenderer.endermanRenderer.isChild = false;
					DefaultSkinRenderer.endermanRenderer.isCarrying = (par1EntityPlayer.inventory.getCurrentItem() != null && par1EntityPlayer.inventory.getCurrentItem().itemID < 256);
					this.mainModel = DefaultSkinRenderer.endermanRenderer;
					super.doRenderLiving(par1EntityPlayer, par2, par4 - (double) par1EntityPlayer.yOffset + 0.05f, par6, par8, par9);
					
					RenderEnderman.tex_eyes.bindTexture();
					
					EaglerAdapter.glPushMatrix();
					EaglerAdapter.glDisable(EaglerAdapter.GL_CULL_FACE);
					EaglerAdapter.glEnable(EaglerAdapter.GL_BLEND);
					EaglerAdapter.glBlendFunc(EaglerAdapter.GL_SRC_ALPHA, EaglerAdapter.GL_ONE);
					EaglerAdapter.glDisable(EaglerAdapter.GL_LIGHTING);
					EaglerAdapter.glTranslatef((float)par2, (float)par4 - par1EntityPlayer.yOffset + 0.05f, (float)par6);
					float var13 = this.handleRotationFloat(par1EntityPlayer, par9);
					float var10 = par1EntityPlayer.prevRenderYawOffset + (par1EntityPlayer.renderYawOffset - par1EntityPlayer.prevRenderYawOffset) * par9;
					float var11 = par1EntityPlayer.prevRotationYawHead + (par1EntityPlayer.rotationYawHead - par1EntityPlayer.prevRotationYawHead) * par9;
					float var12 = par1EntityPlayer.prevRotationPitch + (par1EntityPlayer.rotationPitch - par1EntityPlayer.prevRotationPitch) * par9;
					this.rotateCorpse(par1EntityPlayer, var13, var10, par9);
					EaglerAdapter.glEnable(EaglerAdapter.GL_RESCALE_NORMAL);
					EaglerAdapter.glScalef(-0.95F, -1.0F, 0.95F); //?
					EaglerAdapter.glTranslatef(0.0F, -24.0F * 0.0625F - 0.0078125F + 0.1606f, 0.0F);

					char var5 = 61680;
					int var6 = var5 % 65536;
					int var7 = var5 / 65536;
					EaglerAdapter.glColor4f(2.3F, 2.3F, 2.3F, par1EntityPlayer.isInvisible() ? 0.3f : 1.0f);
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) var6 / 1.0F, (float) var7 / 1.0F);
					DefaultSkinRenderer.endermanRenderer.render(null, 0f, 0f, var13, var11 - var10, var12, 0.0625f);
					EaglerAdapter.glDisable(EaglerAdapter.GL_RESCALE_NORMAL);
					EaglerAdapter.glBlendFunc(EaglerAdapter.GL_SRC_ALPHA, EaglerAdapter.GL_ONE_MINUS_SRC_ALPHA);
					EaglerAdapter.glEnable(EaglerAdapter.GL_ALPHA_TEST);
					EaglerAdapter.glEnable(EaglerAdapter.GL_CULL_FACE);
					EaglerAdapter.glPopMatrix();
					EaglerAdapter.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					
					DefaultSkinRenderer.endermanRenderer.isCarrying = false;
					this.mainModel = this.modelBipedMain;
					break;
				case 20:
					if(DefaultSkinRenderer.skeletonRenderer == null) DefaultSkinRenderer.skeletonRenderer = new ModelSkeleton(0.0F);
					DefaultSkinRenderer.skeletonRenderer.isChild = false;
					this.mainModel = DefaultSkinRenderer.skeletonRenderer;
					super.doRenderLiving(par1EntityPlayer, par2, par4 - (double) par1EntityPlayer.yOffset, par6, par8, par9);
					this.mainModel = this.modelBipedMain;
					break;
				case 21:
					if(DefaultSkinRenderer.blazeRenderer == null) DefaultSkinRenderer.blazeRenderer = new ModelBlaze();
					DefaultSkinRenderer.blazeRenderer.isChild = false;
					this.mainModel = DefaultSkinRenderer.blazeRenderer;
					super.doRenderLiving(par1EntityPlayer, par2, par4 - (double) par1EntityPlayer.yOffset, par6, par8, par9);
					this.mainModel = this.modelBipedMain;
					break;
				}
			}
		}
	}
	
	private static final TextureLocation lax1dude_cape = new TextureLocation("/misc/laxcape.png");

	/**
	 * Method for adding special render rules
	 */
	protected void renderSpecials(EntityPlayer par1EntityPlayer, float par2) {
		float var3 = 1.0F;
		EaglerAdapter.glColor3f(var3, var3, var3);
		super.renderEquippedItems(par1EntityPlayer, par2);
		super.renderArrowsStuckInEntity(par1EntityPlayer, par2);
		ItemStack var4 = par1EntityPlayer.inventory.armorItemInSlot(3);
		
		boolean isNew = DefaultSkinRenderer.isPlayerNewSkin(par1EntityPlayer);
		int renderType = DefaultSkinRenderer.getPlayerRenderer(par1EntityPlayer);

		if (var4 != null) {
			EaglerAdapter.glPushMatrix();
			(isNew ? this.modelBipedMainNewSkin : this.modelBipedMain).bipedHead.postRender(0.0625F);
			float var5;

			if (var4.getItem().itemID < 256) {
				if (RenderBlocks.renderItemIn3d(Block.blocksList[var4.itemID].getRenderType())) {
					var5 = 0.625F;
					EaglerAdapter.glTranslatef(0.0F, -0.25F, 0.0F);
					EaglerAdapter.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
					EaglerAdapter.glScalef(var5, -var5, -var5);
				}

				this.renderManager.itemRenderer.renderItem(par1EntityPlayer, var4, 0);
			} else if (var4.getItem().itemID == Item.skull.itemID) {
				var5 = 1.0625F;
				EaglerAdapter.glScalef(var5, -var5, -var5);
				String var6 = "";

				if (var4.hasTagCompound() && var4.getTagCompound().hasKey("SkullOwner")) {
					var6 = var4.getTagCompound().getString("SkullOwner");
				}

				TileEntitySkullRenderer.skullRenderer.func_82393_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, var4.getItemDamage(), var6);
			}

			EaglerAdapter.glPopMatrix();
		}

		float var7;
		float var8;
		
		/*
		if (par1EntityPlayer.username.equals("deadmau5") && this.loadDownloadableImageTexture(par1EntityPlayer.skinUrl, (String) null)) {
			for (int var20 = 0; var20 < 2; ++var20) {
				float var23 = par1EntityPlayer.prevRotationYaw + (par1EntityPlayer.rotationYaw - par1EntityPlayer.prevRotationYaw) * par2
						- (par1EntityPlayer.prevRenderYawOffset + (par1EntityPlayer.renderYawOffset - par1EntityPlayer.prevRenderYawOffset) * par2);
				var7 = par1EntityPlayer.prevRotationPitch + (par1EntityPlayer.rotationPitch - par1EntityPlayer.prevRotationPitch) * par2;
				EaglerAdapter.glPushMatrix();
				EaglerAdapter.glRotatef(var23, 0.0F, 1.0F, 0.0F);
				EaglerAdapter.glRotatef(var7, 1.0F, 0.0F, 0.0F);
				EaglerAdapter.glTranslatef(0.375F * (float) (var20 * 2 - 1), 0.0F, 0.0F);
				EaglerAdapter.glTranslatef(0.0F, -0.375F, 0.0F);
				EaglerAdapter.glRotatef(-var7, 1.0F, 0.0F, 0.0F);
				EaglerAdapter.glRotatef(-var23, 0.0F, 1.0F, 0.0F);
				var8 = 1.3333334F;
				EaglerAdapter.glScalef(var8, var8, var8);
				this.modelBipedMain.renderEars(0.0625F);
				EaglerAdapter.glPopMatrix();
			}
		}
		*/

		float var11;
		
		if (par1EntityPlayer.username.equals("LAX1DUDE") && !par1EntityPlayer.isInvisible() && !par1EntityPlayer.getHideCape() && renderType != 21) {
			lax1dude_cape.bindTexture();
			EaglerAdapter.glPushMatrix();
			EaglerAdapter.glTranslatef(0.0F, 0.0F, 0.125F);
			double var21 = par1EntityPlayer.field_71091_bM + (par1EntityPlayer.field_71094_bP - par1EntityPlayer.field_71091_bM) * (double) par2
					- (par1EntityPlayer.prevPosX + (par1EntityPlayer.posX - par1EntityPlayer.prevPosX) * (double) par2);
			double var24 = par1EntityPlayer.field_71096_bN + (par1EntityPlayer.field_71095_bQ - par1EntityPlayer.field_71096_bN) * (double) par2
					- (par1EntityPlayer.prevPosY + (par1EntityPlayer.posY - par1EntityPlayer.prevPosY) * (double) par2);
			double var9 = par1EntityPlayer.field_71097_bO + (par1EntityPlayer.field_71085_bR - par1EntityPlayer.field_71097_bO) * (double) par2
					- (par1EntityPlayer.prevPosZ + (par1EntityPlayer.posZ - par1EntityPlayer.prevPosZ) * (double) par2);
			var11 = par1EntityPlayer.prevRenderYawOffset + (par1EntityPlayer.renderYawOffset - par1EntityPlayer.prevRenderYawOffset) * par2;
			double var12 = (double) MathHelper.sin(var11 * (float) Math.PI / 180.0F);
			double var14 = (double) (-MathHelper.cos(var11 * (float) Math.PI / 180.0F));
			float var16 = (float) var24 * 10.0F;

			if (var16 < -6.0F) {
				var16 = -6.0F;
			}

			if (var16 > 32.0F) {
				var16 = 32.0F;
			}

			float var17 = (float) (var21 * var12 + var9 * var14) * 100.0F;
			float var18 = (float) (var21 * var14 - var9 * var12) * 100.0F;

			if (var17 < 0.0F) {
				var17 = 0.0F;
			}

			float var19 = par1EntityPlayer.prevCameraYaw + (par1EntityPlayer.cameraYaw - par1EntityPlayer.prevCameraYaw) * par2;
			var16 += MathHelper.sin((par1EntityPlayer.prevDistanceWalkedModified + (par1EntityPlayer.distanceWalkedModified - par1EntityPlayer.prevDistanceWalkedModified) * par2) * 6.0F) * 32.0F * var19;

			if (par1EntityPlayer.isSneaking()) {
				var16 += 25.0F;
			}

			EaglerAdapter.glRotatef(6.0F + var17 / 2.0F + var16, 1.0F, 0.0F, 0.0F);
			EaglerAdapter.glRotatef(var18 / 2.0F, 0.0F, 0.0F, 1.0F);
			EaglerAdapter.glRotatef(-var18 / 2.0F, 0.0F, 1.0F, 0.0F);
			EaglerAdapter.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			this.modelBipedMain.renderCloak(0.0625F);
			EaglerAdapter.glPopMatrix();
		}

		ItemStack var22 = par1EntityPlayer.inventory.getCurrentItem();

		if (var22 != null) {
			EaglerAdapter.glPushMatrix();
			
			if(DefaultSkinRenderer.isZombieModel(renderType) || renderType == 20) {
				((ModelBiped)this.mainModel).bipedRightArm.postRender(0.0625F);
			}else {
				(isNew ? this.modelBipedMainNewSkin : this.modelBipedMain).bipedRightArm.postRender(0.0625F);
			}
			
			EaglerAdapter.glTranslatef(-0.0625F, 0.4375F, 0.0625F);

			if (par1EntityPlayer.fishEntity != null) {
				var22 = new ItemStack(Item.stick);
			}

			EnumAction var25 = null;

			if (par1EntityPlayer.getItemInUseCount() > 0) {
				var25 = var22.getItemUseAction();
			}

			if (var22.itemID < 256 && RenderBlocks.renderItemIn3d(Block.blocksList[var22.itemID].getRenderType())) {
				var7 = 0.5F;
				EaglerAdapter.glTranslatef(0.0F, 0.1875F, -0.3125F);
				var7 *= 0.75F;
				EaglerAdapter.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
				EaglerAdapter.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				EaglerAdapter.glScalef(-var7, -var7, var7);
			} else if (var22.itemID == Item.bow.itemID) {
				var7 = 0.625F;
				EaglerAdapter.glTranslatef(0.0F, 0.125F, 0.3125F);
				EaglerAdapter.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
				EaglerAdapter.glScalef(var7, -var7, var7);
				EaglerAdapter.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				EaglerAdapter.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else if (Item.itemsList[var22.itemID].isFull3D()) {
				var7 = 0.625F;

				if (Item.itemsList[var22.itemID].shouldRotateAroundWhenRendering()) {
					EaglerAdapter.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
					EaglerAdapter.glTranslatef(0.0F, -0.125F, 0.0F);
				}

				if (par1EntityPlayer.getItemInUseCount() > 0 && var25 == EnumAction.block) {
					EaglerAdapter.glTranslatef(0.05F, 0.0F, -0.1F);
					EaglerAdapter.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
					EaglerAdapter.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
					EaglerAdapter.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
				}

				EaglerAdapter.glTranslatef(0.0F, 0.1875F, 0.0F);
				EaglerAdapter.glScalef(var7, -var7, var7);
				EaglerAdapter.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				EaglerAdapter.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else {
				var7 = 0.375F;
				EaglerAdapter.glTranslatef(0.25F, 0.1875F, -0.1875F);
				EaglerAdapter.glScalef(var7, var7, var7);
				EaglerAdapter.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
				EaglerAdapter.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				EaglerAdapter.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
			}

			float var10;
			int var27;
			float var28;

			if (var22.getItem().requiresMultipleRenderPasses()) {
				for (var27 = 0; var27 <= 1; ++var27) {
					int var26 = var22.getItem().getColorFromItemStack(var22, var27);
					var28 = (float) (var26 >> 16 & 255) / 255.0F;
					var10 = (float) (var26 >> 8 & 255) / 255.0F;
					var11 = (float) (var26 & 255) / 255.0F;
					EaglerAdapter.glColor4f(var28, var10, var11, 1.0F);
					this.renderManager.itemRenderer.renderItem(par1EntityPlayer, var22, var27);
				}
			} else {
				var27 = var22.getItem().getColorFromItemStack(var22, 0);
				var8 = (float) (var27 >> 16 & 255) / 255.0F;
				var28 = (float) (var27 >> 8 & 255) / 255.0F;
				var10 = (float) (var27 & 255) / 255.0F;
				EaglerAdapter.glColor4f(var8, var28, var10, 1.0F);
				this.renderManager.itemRenderer.renderItem(par1EntityPlayer, var22, 0);
			}

			EaglerAdapter.glPopMatrix();
		}
	}

	protected void renderPlayerScale(EntityPlayer par1EntityPlayer, float par2) {
		float var3 = 0.9375F;
		EaglerAdapter.glScalef(var3, var3, var3);
	}

	protected void func_96450_a(EntityPlayer par1EntityPlayer, double par2, double par4, double par6, String par8Str, float par9, double par10) {
		if (par10 < 100.0D) {
			Scoreboard var12 = par1EntityPlayer.getWorldScoreboard();
			ScoreObjective var13 = var12.func_96539_a(2);

			if (var13 != null) {
				Score var14 = var12.func_96529_a(par1EntityPlayer.getEntityName(), var13);

				if (par1EntityPlayer.isPlayerSleeping()) {
					this.renderLivingLabel(par1EntityPlayer, var14.func_96652_c() + " " + var13.getDisplayName(), par2, par4 - 1.5D, par6, 64);
				} else {
					this.renderLivingLabel(par1EntityPlayer, var14.func_96652_c() + " " + var13.getDisplayName(), par2, par4, par6, 64);
				}

				par4 += (double) ((float) this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * par9);
			}
		}

		super.func_96449_a(par1EntityPlayer, par2, par4, par6, par8Str, par9, par10);
	}

	public void renderFirstPersonArm(EntityPlayer par1EntityPlayer) {
		float var2 = 1.0F;
		EaglerAdapter.glColor4f(var2, var2, var2, 1.0F);
		int i = DefaultSkinRenderer.getPlayerRenderer(par1EntityPlayer);
		if(DefaultSkinRenderer.isStandardModel(i) || DefaultSkinRenderer.isZombieModel(i)) {
			boolean isNew = DefaultSkinRenderer.isPlayerNewSkin(par1EntityPlayer);
			(isNew ? this.modelBipedMainNewSkin : this.modelBipedMain).onGround = 0.0F;
			(isNew ? this.modelBipedMainNewSkin : this.modelBipedMain).setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, par1EntityPlayer);
			(isNew ? this.modelBipedMainNewSkin : this.modelBipedMain).bipedRightArm.render(0.0625F);
		}
	}

	/**
	 * Renders player with sleeping offset if sleeping
	 */
	protected void renderPlayerSleep(EntityPlayer par1EntityPlayer, double par2, double par4, double par6) {
		if (par1EntityPlayer.isEntityAlive() && par1EntityPlayer.isPlayerSleeping()) {
			super.renderLivingAt(par1EntityPlayer, par2 + (double) par1EntityPlayer.field_71079_bU, par4 + (double) par1EntityPlayer.field_71082_cx, par6 + (double) par1EntityPlayer.field_71089_bV);
		} else {
			super.renderLivingAt(par1EntityPlayer, par2, par4, par6);
		}
	}

	/**
	 * Rotates the player if the player is sleeping. This method is called in
	 * rotateCorpse.
	 */
	protected void rotatePlayer(EntityPlayer par1EntityPlayer, float par2, float par3, float par4) {
		if (par1EntityPlayer.isEntityAlive() && par1EntityPlayer.isPlayerSleeping()) {
			EaglerAdapter.glRotatef(par1EntityPlayer.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
			EaglerAdapter.glRotatef(this.getDeathMaxRotation(par1EntityPlayer), 0.0F, 0.0F, 1.0F);
			EaglerAdapter.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
		} else {
			super.rotateCorpse(par1EntityPlayer, par2, par3, par4);
		}
	}

	protected void func_96449_a(EntityLiving par1EntityLiving, double par2, double par4, double par6, String par8Str, float par9, double par10) {
		this.func_96450_a((EntityPlayer) par1EntityLiving, par2, par4, par6, par8Str, par9, par10);
	}

	/**
	 * Allows the render to do any OpenGL state modifications necessary before the
	 * model is rendered. Args: entityLiving, partialTickTime
	 */
	protected void preRenderCallback(EntityLiving par1EntityLiving, float par2) {
		this.renderPlayerScale((EntityPlayer) par1EntityLiving, par2);
	}

	protected void func_82408_c(EntityLiving par1EntityLiving, int par2, float par3) {
		this.func_82439_b((EntityPlayer) par1EntityLiving, par2, par3);
	}

	/**
	 * Queries whether should render the specified pass or not.
	 */
	protected int shouldRenderPass(EntityLiving par1EntityLiving, int par2, float par3) {
		return this.setArmorModel((EntityPlayer) par1EntityLiving, par2, par3);
	}

	private static final TextureLocation terrain = new TextureLocation("/terrain.png");
	
	protected void renderEquippedItems(EntityLiving par1EntityLiving, float par2) {
		if(!renderPass2) {
			EntityPlayer p = (EntityPlayer) par1EntityLiving;
			int renderType = DefaultSkinRenderer.getPlayerRenderer(p);
			if(DefaultSkinRenderer.isPlayerStandard(p) || DefaultSkinRenderer.isZombieModel(renderType) || renderType == 20) {
				this.renderSpecials(p, par2);
			}else {
				if(renderType == 19) {
					ItemStack s = p.inventory.getCurrentItem();
					if(s != null && s.itemID < 256) {
						EaglerAdapter.glEnable(EaglerAdapter.GL_RESCALE_NORMAL);
						EaglerAdapter.glPushMatrix();
						float var3 = 0.5F;
						EaglerAdapter.glTranslatef(0.0F, 0.6875F, -0.75F);
						var3 *= 1.0F;
						EaglerAdapter.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
						EaglerAdapter.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
						EaglerAdapter.glScalef(-var3, -var3, var3);
						int var4 = p.getBrightnessForRender(par2);
						int var5 = var4 % 65536;
						int var6 = var4 / 65536;
						OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) var5 / 1.0F, (float) var6 / 1.0F);
						EaglerAdapter.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
						terrain.bindTexture();
						this.renderBlocks.renderBlockAsItem(Block.blocksList[s.itemID], s.getItemDamage(), 1.0F);
						EaglerAdapter.glPopMatrix();
						EaglerAdapter.glDisable(EaglerAdapter.GL_RESCALE_NORMAL);
					}
				}
			}
		}
	}

	protected void rotateCorpse(EntityLiving par1EntityLiving, float par2, float par3, float par4) {
		this.rotatePlayer((EntityPlayer) par1EntityLiving, par2, par3, par4);
	}

	/**
	 * Sets a simple glTranslate on a LivingEntity.
	 */
	protected void renderLivingAt(EntityLiving par1EntityLiving, double par2, double par4, double par6) {
		this.renderPlayerSleep((EntityPlayer) par1EntityLiving, par2, par4, par6);
	}

	public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
		this.renderPlayer((EntityPlayer) par1EntityLiving, par2, par4, par6, par8, par9);
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method,
	 * always casting down its argument and then handing it off to a worker function
	 * which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void
	 * doRender(T entity, double d, double d1, double d2, float f, float f1). But
	 * JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.renderPlayer((EntityPlayer) par1Entity, par2, par4, par6, par8, par9);
	}

	private static final TextureLocation entityTexture = new TextureLocation("/mob/char.png");

	@Override
	protected void bindTexture(EntityLiving par1EntityLiving) {
		if(par1EntityLiving instanceof EntityClientPlayerMP) {
			if(EaglerProfile.presetSkinId < 0) {
				Minecraft.getMinecraft().renderEngine.bindTexture(EaglerProfile.glTex.get(EaglerProfile.customSkinId));
			}else {
				DefaultSkinRenderer.defaultVanillaSkins[EaglerProfile.presetSkinId].bindTexture();
			}
		}else if(par1EntityLiving instanceof EntityOtherPlayerMP) {
			if(!DefaultSkinRenderer.bindSyncedSkin((EntityOtherPlayerMP)par1EntityLiving)) {
				entityTexture.bindTexture();
			}
		}else {
			entityTexture.bindTexture();
		}
	}
}
