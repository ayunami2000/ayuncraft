package net.lax1dude.eaglercraft;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.src.NBTBase;
import net.minecraft.src.NBTTagByteArray;
import net.minecraft.src.NBTTagCompound;

public class EaglerProfile {
	
	public static class EaglerProfileSkin {
		public String name;
		public byte[] data;
		public boolean slim;
		public int glTex;
		public EaglerProfileSkin(String name, byte[] data, boolean slim, int glTex) {
			this.name = name;
			this.data = data;
			this.slim = slim;
			this.glTex = glTex;
		}
	}

	public static String username;
	public static int presetSkinId;
	public static int customSkinId;
	
	public static String myChannel;
	
	public static final int[] SKIN_DATA_SIZE = new int[] { 64*32*4, 64*64*4, 128*64*4, 128*128*4, 2, 64*64*4, 128*128*4 };
	public static ArrayList<EaglerProfileSkin> skins = new ArrayList();
	
	public static final EaglercraftRandom rand;
	
	public static int getSkinSize(int len) {
		for(int i = 0; i < SKIN_DATA_SIZE.length; ++i) {
			if(len == SKIN_DATA_SIZE[i]) {
				return i;
			}
		}
		return -1;
	}
	
	public static byte[] getSkinPacket() {
		if(presetSkinId == -1) {
			byte[] d = skins.get(customSkinId).data;
			byte[] d2 = new byte[1 + d.length];
			d2[0] = (byte) getSkinSize(d.length);
			if(d2[0] == (byte)1 && skins.get(customSkinId).slim) {
				d2[0] = (byte)5;
			}
			if(d2[0] == (byte)3 && skins.get(customSkinId).slim) {
				d2[0] = (byte)6;
			}
			System.arraycopy(d, 0, d2, 1, d.length);
			return d2;
		}else {
			return new byte[] { (byte)4, (byte)presetSkinId };
		}
	}
	
	public static String[] concatArrays(String[] a, String[] b) {
		String[] r = new String[a.length + b.length];
		System.arraycopy(a, 0, r, 0, a.length);
		System.arraycopy(b, 0, r, a.length, b.length);
		return r;
	}
	
	public static int addSkin(String name, byte[] data, boolean slim) {
		int i = -1;
		for(int j = 0, l = skins.size(); j < l; ++j) {
			if(skins.get(j).name.equalsIgnoreCase(name)) {
				i = j;
				break;
			}
		}
		int t = getSkinSize(data.length);
		
		if(t == -1) {
			return -1;
		}
		
		int w, h;
		
		switch(t) {
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
		
		int im = Minecraft.getMinecraft().renderEngine.setupTextureRaw(data, w, h);
		if(i == -1) {
			i = skins.size();
			skins.add(new EaglerProfileSkin(name, data, slim, im));
		}else {
			skins.get(i).glTex = im;
			skins.get(i).data = data;
			skins.get(i).slim = slim;
		}
		return i;
		
	}
	
	static {
		String[] usernameDefaultWords = ConfigConstants.profanity ? new String[] {
				"Eagler",
				"Eagler",
				"Bitch",
				"Cock",
				"Milf",
				"Milf",
				"Yeer",
				"Groon",
				"Eag",
				"Deevis",
				"Chode",
				"Deev",
				"Deev",
				"Fucker",
				"Fucking",
				"Dumpster",
				"Dumpster",
				"Cum",
				"Chad",
				"Egg",
				"Fudgler",
				"Fudgli",
				"Yee",
				"Yee",
				"Yee",
				"Yeet",
				"Flumpter",
				"Darvy",
				"Darver",
				"Darver",
				"Fuck",
				"Fuck",
				"Frick",
				"Eagler",
				"Vigg",
				"Vigg",
				"Cunt",
				"Darvig"
		} : new String[] {
				"Yeeish",
				"Yeeish",
				"Yee",
				"Yee",
				"Yeer",
				"Yeeler",
				"Eagler",
				"Eagl",
				"Darver",
				"Darvler",
				"Vool",
				"Vigg",
				"Vigg",
				"Deev",
				"Yigg",
				"Yeeg"
		};
		
		rand = new EaglercraftRandom();
		
		do {
			username = usernameDefaultWords[rand.nextInt(usernameDefaultWords.length)] + usernameDefaultWords[rand.nextInt(usernameDefaultWords.length)] + (10 + rand.nextInt(90));
		}while(username.length() > 16);
		
		presetSkinId = rand.nextInt(GuiScreenEditProfile.defaultOptions.length);
		myChannel = username + "_" + (100 + rand.nextInt(900));
		customSkinId = -1;
	}

	public static void loadFromStorage() {
		if(!LocalStorageManager.profileSettingsStorage.hasNoTags()) {
			presetSkinId = LocalStorageManager.profileSettingsStorage.getInteger("ps");
			customSkinId = LocalStorageManager.profileSettingsStorage.getInteger("cs");
			username = LocalStorageManager.profileSettingsStorage.getString("name");
			myChannel = username + "_" + (100 + rand.nextInt(900));
			NBTTagCompound n = LocalStorageManager.profileSettingsStorage.getCompoundTag("skins");
			for(Object s : NBTTagCompound.getTagMap(n).keySet()) {
				String s2 = (String)s;
				NBTBase k = n.getTag(s2);
				if(k.getId() == (byte)7) {
					addSkin(s2, ((NBTTagByteArray)k).byteArray, false);
				}else if(k.getId() == (byte)10) {
					addSkin(s2, ((NBTTagCompound)k).getByteArray("data"), ((NBTTagCompound)k).getBoolean("slim"));
				}
			}
		}
	}
	
}
