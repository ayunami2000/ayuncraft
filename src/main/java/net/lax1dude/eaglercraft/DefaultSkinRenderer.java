package net.lax1dude.eaglercraft;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.EntityOtherPlayerMP;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ModelBiped;
import net.minecraft.src.ModelBlaze;
import net.minecraft.src.ModelEnderman;
import net.minecraft.src.ModelSkeleton;
import net.minecraft.src.ModelVillager;
import net.minecraft.src.ModelZombie;
import net.minecraft.src.OpenGlHelper;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.RenderEnderman;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.RenderManager;

public class DefaultSkinRenderer {
	
	public static final TextureLocation[] defaultVanillaSkins = new TextureLocation[] {
			new TextureLocation("/skins/01.default_steve.png"),
			new TextureLocation("/skins/02.default_alex.png"),
			new TextureLocation("/skins/03.tennis_steve.png"),
			new TextureLocation("/skins/04.tennis_alex.png"),
			new TextureLocation("/skins/05.tuxedo_steve.png"),
			new TextureLocation("/skins/06.tuxedo_alex.png"),
			new TextureLocation("/skins/07.athlete_steve.png"),
			new TextureLocation("/skins/08.athlete_alex.png"),
			new TextureLocation("/skins/09.cyclist_steve.png"),
			new TextureLocation("/skins/10.cyclist_alex.png"),
			new TextureLocation("/skins/11.boxer_steve.png"),
			new TextureLocation("/skins/12.boxer_alex.png"),
			new TextureLocation("/skins/13.prisoner_steve.png"),
			new TextureLocation("/skins/14.prisoner_alex.png"),
			new TextureLocation("/skins/15.scottish_steve.png"),
			new TextureLocation("/skins/16.scottish_alex.png"),
			new TextureLocation("/skins/17.dev_steve.png"),
			new TextureLocation("/skins/18.dev_alex.png"),
			new TextureLocation("/skins/19.herobrine.png"),
			new TextureLocation("/mob/enderman.png"),
			new TextureLocation("/mob/skeleton.png"),
			new TextureLocation("/mob/fire.png"),
			new TextureLocation("/skins/20.barney.png"),
			new TextureLocation("/skins/21.slime.png"),
			new TextureLocation("/skins/22.noob.png"),
			new TextureLocation("/skins/23.trump.png"),
			new TextureLocation("/skins/24.notch.png"),
			new TextureLocation("/skins/25.creeper.png"),
			new TextureLocation("/skins/26.zombie.png"),
			new TextureLocation("/skins/27.pig.png"),
			new TextureLocation("/skins/28.squid.png"),
			new TextureLocation("/skins/29.mooshroom.png"),
			new TextureLocation("/mob/villager/villager.png"),
			new TextureLocation("/skins/30.longarms.png"),
			new TextureLocation("/skins/31.laxdude.png")
	};
	
	public static final boolean[] defaultVanillaSkinClassicOrSlimVariants = new boolean[] {
			false, true,
			false, true,
			false, true,
			false, true,
			false, true,
			false, true,
			false, true,
			false, true,
			false, true
	};

	private static final HashMap<Integer,EntityOtherPlayerMP> skinCookies = new HashMap();
	private static final HashMap<EntityOtherPlayerMP,Integer> skinGLUnits = new HashMap();
	private static final HashMap<EntityOtherPlayerMP,Long> skinGLTimeout = new HashMap();
	
	private static long lastClean = 0l;
	
	public static void deleteOldSkins() {
		if(System.currentTimeMillis() - lastClean > 60000l) {
			lastClean = System.currentTimeMillis();
			Iterator<Entry<EntityOtherPlayerMP,Long>> itr = skinGLTimeout.entrySet().iterator();
			while(itr.hasNext()) {
				Entry<EntityOtherPlayerMP,Long> ee = itr.next();
				if(System.currentTimeMillis() - ee.getValue() > 80000l) {
					itr.remove();
					if(skinGLUnits.containsKey(ee.getKey())) {
						Minecraft.getMinecraft().renderEngine.deleteTexture(skinGLUnits.remove(ee.getKey()));
					}
				}
			}
			Iterator<Entry<Integer, EntityOtherPlayerMP>> itr2 = skinCookies.entrySet().iterator();
			while(itr2.hasNext()) {
				Entry<Integer, EntityOtherPlayerMP> e = itr2.next();
				if(e.getValue().isDead) {
					itr2.remove();
				}
			}
		}
	}
	
	public static boolean bindSyncedSkin(EntityPlayer p) {
		if(p instanceof EntityClientPlayerMP) {
			return false;
		}else if(p instanceof EntityOtherPlayerMP) {
			EntityOtherPlayerMP pp = (EntityOtherPlayerMP) p;
			if(pp.skinPacket != null) {
				if(((int)pp.skinPacket[0] & 0xFF) != 4) {
					if(!skinGLUnits.containsKey(pp)) {
						byte[] skinToLoad = new byte[pp.skinPacket.length - 1];
						System.arraycopy(pp.skinPacket, 1, skinToLoad, 0, skinToLoad.length);
						int w, h;
						
						switch((int)pp.skinPacket[0] & 0xFF) {
						default:
						case 0:
							w = 64;
							h = 32;
							break;
						case 1:
						case 5:
							w = 64;
							h = 64;
							break;
						case 2:
							w = 128;
							h = 64;
							break;
						case 3:
						case 6:
							w = 128;
							h = 128;
							break;
						}
						
						if(skinToLoad.length / 4 == w * h) {
							skinGLUnits.put(pp, Minecraft.getMinecraft().renderEngine.setupTextureRaw(skinToLoad, w, h));
						}
					}
					skinGLTimeout.put(pp, System.currentTimeMillis());
					Integer i = skinGLUnits.get(pp);
					if(i != null && i.intValue() > 0) {
						Minecraft.getMinecraft().renderEngine.bindTexture(i.intValue());
					}else {
						defaultVanillaSkins[0].bindTexture();
					}
				}else {
					if(((int)pp.skinPacket[1] & 0xFF) < defaultVanillaSkins.length) {
						defaultVanillaSkins[(int)pp.skinPacket[1] & 0xFF].bindTexture();
					}
				}
				return true;
			}else {
				if(!skinCookies.containsValue(pp)) {
					int cookie = (int)(System.nanoTime() % 65536);
					skinCookies.put(cookie, pp);
					byte[] n = pp.username.getBytes();
					byte[] pkt = new byte[n.length + 2];
					System.arraycopy(n, 0, pkt, 2, n.length);
					pkt[0] = (byte)(cookie & 0xFF);
					pkt[1] = (byte)((cookie >> 8) & 0xFF);
					Minecraft.getMinecraft().getNetHandler().addToSendQueue(new Packet250CustomPayload("EAG|FetchSkin", pkt));
				}
			}
			return false;
		}else {
			return false;
		}
	}
	
	public static void skinResponse(byte[] data) {
		int cookie = ((int)data[0] & 0xFF) | (((int)data[1] & 0xFF) << 8);
		if(skinCookies.containsKey(cookie) && (data.length > 3)) {
			EntityOtherPlayerMP p = skinCookies.remove(cookie);
			byte[] packet = new byte[data.length - 2];
			System.arraycopy(data, 2, packet, 0, packet.length);
			p.skinPacket = packet;
		}
	}
	
	public static boolean isNewSkin(int id) {
		return !(id == 0 || id == 2 || id == 4 || id == 6 || id == 8 || id == 10 || id == 12 || id == 14 || id == 18 || id == 28);
	}
	
	public static boolean isAlexSkin(int id) {
		return id < defaultVanillaSkinClassicOrSlimVariants.length && defaultVanillaSkinClassicOrSlimVariants[id];
	}
	
	public static boolean isStandardModel(int id) {
		return !isZombieModel(id) && !(id == 19 || id == 20 || id == 21 || id == 32 || id == 33 || id == 34);
	}
	
	public static boolean isZombieModel(int id) {
		return id == 18 || id == 28;
	}
	
	public static boolean isPlayerNewSkin(EntityPlayer p) {
		if(p instanceof EntityClientPlayerMP) {
			if(EaglerProfile.presetSkinId <= -1) {
				int type = EaglerProfile.getSkinSize(EaglerProfile.skins.get(EaglerProfile.customSkinId).data.length);
				return (type == 1 || type == 3);
			}else {
				return isNewSkin(EaglerProfile.presetSkinId);
			}
		}else if(p instanceof EntityOtherPlayerMP) {
			EntityOtherPlayerMP pp = (EntityOtherPlayerMP) p;
			if(pp.skinPacket != null) {
				if(pp.skinPacket[0] != (byte)4) {
					return (pp.skinPacket[0] == (byte)1) || (pp.skinPacket[0] == (byte)3) || (pp.skinPacket[0] == (byte)5) || (pp.skinPacket[0] == (byte)6);
				}else {
					return isNewSkin((int)pp.skinPacket[1] & 0xFF);
				}
			}
		}
		return false;
	}
	
	public static boolean isPlayerNewSkinSlim(EntityPlayer p) {
		if(p instanceof EntityClientPlayerMP) {
			if(EaglerProfile.presetSkinId == -1) {
				return EaglerProfile.skins.get(EaglerProfile.customSkinId).slim;
			}else {
				return isAlexSkin(EaglerProfile.presetSkinId);
			}
		}else if(p instanceof EntityOtherPlayerMP) {
			EntityOtherPlayerMP pp = (EntityOtherPlayerMP) p;
			if(pp.skinPacket != null) {
				if(pp.skinPacket[0] != (byte)4) {
					return (pp.skinPacket[0] == (byte)5) || (pp.skinPacket[0] == (byte)6);
				}else {
					return isAlexSkin((int)pp.skinPacket[1] & 0xFF);
				}
			}
		}
		return false;
	}
	
	public static boolean isPlayerStandard(EntityPlayer p) {
		if(p instanceof EntityClientPlayerMP) {
			if(EaglerProfile.presetSkinId == -1) {
				return true;
			}else {
				return isStandardModel(EaglerProfile.presetSkinId);
			}
		}else if(p instanceof EntityOtherPlayerMP) {
			EntityOtherPlayerMP pp = (EntityOtherPlayerMP) p;
			if(pp.skinPacket != null) {
				if(pp.skinPacket[0] != (byte)4) {
					return true;
				}else {
					return isStandardModel((int)pp.skinPacket[1] & 0xFF);
				}
			}
		}
		return true;
	}
	
	public static int getPlayerRenderer(EntityPlayer p) {
		if(p instanceof EntityClientPlayerMP) {
			if(EaglerProfile.presetSkinId == -1) {
				return 0;
			}else {
				return EaglerProfile.presetSkinId;
			}
		}else if(p instanceof EntityOtherPlayerMP) {
			EntityOtherPlayerMP pp = (EntityOtherPlayerMP) p;
			if(pp.skinPacket != null) {
				if(pp.skinPacket[0] != (byte)4) {
					return 0;
				}else {
					return (int)pp.skinPacket[1] & 0xFF;
				}
			}
		}
		return 0;
	}

	public static ModelBiped oldSkinRenderer = null;
	public static ModelBipedNewSkins newSkinRenderer = null;
	public static ModelBipedNewSkins newSkinRendererSlim = null;
	public static ModelZombie zombieRenderer = null;
	public static ModelVillager villagerRenderer = null;
	public static ModelEnderman endermanRenderer = null;
	public static ModelBlaze blazeRenderer = null;
	public static ModelSkeleton skeletonRenderer = null;
	
	public static void renderPlayerPreview(int x, int y, int mx, int my, int id2) {
		int id = id2 - EaglerProfile.skins.size();
		
		EaglerAdapter.glEnable(EaglerAdapter.GL_TEXTURE_2D);
		EaglerAdapter.glDisable(EaglerAdapter.GL_BLEND);
		EaglerAdapter.glDisable(EaglerAdapter.GL_CULL_FACE);
		EaglerAdapter.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		EaglerAdapter.glPushMatrix();
		EaglerAdapter.glTranslatef((float) x, (float) (y - 80), 100.0F);
		EaglerAdapter.glScalef(50.0f, 50.0f, 50.0f);
		EaglerAdapter.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
		EaglerAdapter.glEnable(EaglerAdapter.GL_RESCALE_NORMAL);
		EaglerAdapter.glScalef(1.0F, -1.0F, 1.0F);
		RenderHelper.enableGUIStandardItemLighting();
		EaglerAdapter.glTranslatef(0.0F, 1.0F, 0.0F);
		EaglerAdapter.glRotatef(((y - my) * -0.06f), 1.0f, 0.0f, 0.0f);
		EaglerAdapter.glRotatef(((x - mx) * 0.06f), 0.0f, 1.0f, 0.0f);
		EaglerAdapter.glTranslatef(0.0F, -1.0F, 0.0F);
		
		if(id < 0) {
			Minecraft.getMinecraft().renderEngine.bindTexture(EaglerProfile.skins.get(id2).glTex);
		}else {
			defaultVanillaSkins[id].bindTexture();
		}
		
		if(isStandardModel(id) || id < 0) {
			if(oldSkinRenderer == null) oldSkinRenderer = new ModelBiped(0.0F, 0.0F, 64, 32);
			if(newSkinRenderer == null) newSkinRenderer = new ModelBipedNewSkins(0.0F, false);
			if(newSkinRendererSlim == null) newSkinRendererSlim = new ModelBipedNewSkins(0.0F, true);
			oldSkinRenderer.isChild = false;
			newSkinRenderer.isChild = false;
			newSkinRendererSlim.isChild = false;
			boolean isNew = isNewSkin(id);
			if(id < 0) {
				int type = EaglerProfile.getSkinSize(EaglerProfile.skins.get(id2).data.length);
				isNew = (type == 1 || type == 3);
			}
			if(isNew) {
				if((id < 0 && EaglerProfile.skins.get(id2).slim) || (id >= 0 && isAlexSkin(id))) {
					newSkinRendererSlim.render(null, 0.0f, 0.0f, (float)(System.currentTimeMillis() % 100000) / 50f, ((x - mx) * 0.06f), ((y - my) * -0.1f), 0.0625F);
				}else {
					newSkinRenderer.render(null, 0.0f, 0.0f, (float)(System.currentTimeMillis() % 100000) / 50f, ((x - mx) * 0.06f), ((y - my) * -0.1f), 0.0625F);
				}
			}else {
				oldSkinRenderer.render(null, 0.0f, 0.0f, (float)(System.currentTimeMillis() % 100000) / 50f, ((x - mx) * 0.06f), ((y - my) * -0.1f), 0.0625F);
			}
		}else if(isZombieModel(id)) {
			if(zombieRenderer == null) zombieRenderer = new ModelZombie(0.0F, true);
			zombieRenderer.isChild = false;
			zombieRenderer.render(null, 0.0f, 0.0f, (float)(System.currentTimeMillis() % 100000) / 50f, ((x - mx) * 0.06f), ((y - my) * -0.1f), 0.0625F);
		}else if(id == 32) {
			if(villagerRenderer == null) villagerRenderer = new ModelVillager(0.0F);
			villagerRenderer.isChild = false;
			villagerRenderer.render(null, 0.0f, 0.0f, (float)(System.currentTimeMillis() % 100000) / 50f, ((x - mx) * 0.06f), ((y - my) * -0.1f), 0.0625F);
		}else if(id == 19) {
			if(endermanRenderer == null) endermanRenderer = new ModelEnderman();
			endermanRenderer.isChild = false;
			endermanRenderer.render(null, 0.0f, 0.0f, (float)(System.currentTimeMillis() % 100000) / 50f, ((x - mx) * 0.06f), ((y - my) * -0.1f), 0.0625F);
			EaglerAdapter.glColor4f(1.4f, 1.4f, 1.4f, 1.0f);
			//EaglerAdapter.glEnable(EaglerAdapter.GL_BLEND);
			//EaglerAdapter.glDisable(EaglerAdapter.GL_ALPHA_TEST);
			//EaglerAdapter.glBlendFunc(EaglerAdapter.GL_ONE, EaglerAdapter.GL_ONE);
			EaglerAdapter.glDisable(EaglerAdapter.GL_LIGHTING);
			EaglerAdapter.glEnable(EaglerAdapter.GL_TEXTURE_2D);
			EaglerAdapter.glDisable(EaglerAdapter.GL_DEPTH_TEST);
			RenderEnderman.tex_eyes.bindTexture();
			endermanRenderer.render(null, 0.0f, 0.0f, (float)(System.currentTimeMillis() % 100000) / 50f, ((x - mx) * 0.06f), ((y - my) * -0.1f), 0.0625F);
			EaglerAdapter.glBlendFunc(EaglerAdapter.GL_SRC_ALPHA, EaglerAdapter.GL_ONE_MINUS_SRC_ALPHA);
			EaglerAdapter.glEnable(EaglerAdapter.GL_ALPHA_TEST);
			EaglerAdapter.glEnable(EaglerAdapter.GL_DEPTH_TEST);
			EaglerAdapter.glDisable(EaglerAdapter.GL_TEXTURE_2D);
			EaglerAdapter.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		}else if(id == 20) {
			if(skeletonRenderer == null) skeletonRenderer = new ModelSkeleton(0.0F);
			skeletonRenderer.isChild = false;
			skeletonRenderer.render(null, 0.0f, 0.0f, (float)(System.currentTimeMillis() % 100000) / 50f, ((x - mx) * 0.06f), ((y - my) * -0.1f), 0.0625F);
		}else if(id == 21) {
			if(blazeRenderer == null) blazeRenderer = new ModelBlaze();
			blazeRenderer.isChild = false;
			EaglerAdapter.glColor4f(1.5f, 1.5f, 1.5f, 1.0f);
			blazeRenderer.render(null, 0.0f, 0.0f, (float)(System.currentTimeMillis() % 100000) / 50f, ((x - mx) * 0.06f), ((y - my) * -0.1f), 0.0625F);
		}
		
		EaglerAdapter.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		EaglerAdapter.glPopMatrix();
		EaglerAdapter.glDisable(EaglerAdapter.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		EaglerAdapter.glDisable(EaglerAdapter.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		EaglerAdapter.glDisable(EaglerAdapter.GL_LIGHTING);
	}
	
	public static void renderAlexOrSteve(int x, int y, int mx, int my, boolean alex) {
		ModelBipedNewSkins bp;
		if(alex) {
			if(newSkinRendererSlim == null) {
				newSkinRendererSlim = new ModelBipedNewSkins(0.0F, true);
			}
			bp = newSkinRendererSlim;
		}else {
			if(newSkinRenderer == null) {
				newSkinRenderer = new ModelBipedNewSkins(0.0F, false);
			}
			bp = newSkinRenderer;
		}
		
		EaglerAdapter.glEnable(EaglerAdapter.GL_TEXTURE_2D);
		EaglerAdapter.glDisable(EaglerAdapter.GL_BLEND);
		EaglerAdapter.glDisable(EaglerAdapter.GL_CULL_FACE);
		EaglerAdapter.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		EaglerAdapter.glPushMatrix();
		EaglerAdapter.glTranslatef((float) x, (float) (y - 80), 100.0F);
		EaglerAdapter.glScalef(50.0f, 50.0f, 50.0f);
		EaglerAdapter.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
		EaglerAdapter.glEnable(EaglerAdapter.GL_RESCALE_NORMAL);
		EaglerAdapter.glScalef(1.0F, -1.0F, 1.0F);
		RenderHelper.enableGUIStandardItemLighting();
		EaglerAdapter.glTranslatef(0.0F, 1.0F, 0.0F);
		EaglerAdapter.glRotatef(((y - my) * -0.06f), 1.0f, 0.0f, 0.0f);
		EaglerAdapter.glRotatef(((x - mx) * 0.06f), 0.0f, 1.0f, 0.0f);
		EaglerAdapter.glTranslatef(0.0F, -1.0F, 0.0F);
		
		bp.isChild = false;
		bp.render(null, 0.0f, 0.0f, (float)(System.currentTimeMillis() % 100000) / 50f, ((x - mx) * 0.06f), ((y - my) * -0.1f), 0.0625F);
		
		EaglerAdapter.glPopMatrix();
		EaglerAdapter.glDisable(EaglerAdapter.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		EaglerAdapter.glDisable(EaglerAdapter.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		EaglerAdapter.glDisable(EaglerAdapter.GL_LIGHTING);
	}

	public static boolean isPlayerPreviewNew(int id2) {
		int id = id2 - EaglerProfile.skins.size();
		if(id < 0) {
			return EaglerProfile.skins.get(id2).data.length == EaglerProfile.SKIN_DATA_SIZE[1] || EaglerProfile.skins.get(id2).data.length == EaglerProfile.SKIN_DATA_SIZE[3];
		}else {
			return isNewSkin(id);
		}
	}
	
}
