package net.lax1dude.eaglercraft;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;

import net.lax1dude.eaglercraft.EaglerAdapter;
import net.lax1dude.eaglercraft.EaglerImage;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.GLAllocation;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.RenderManager;

//supports only 16x16 textures, mipmap is four levels deep
public class TextureTerrainMap implements IconRegister {
	
	private static class TerrainIcon implements Icon {

		public final TextureTerrainMap map;
		public final String name;
		public final int id;
		private EaglerImage[][] frames = null;
		private int[] framesIdx = null;

		protected int originX;
		protected int originY;
		private float minU;
		private float maxU;
		private float minV;
		private float maxV;
		
		protected int originX_center;
		protected int originY_center;
		private float minU_center;
		private float maxU_center;
		private float minV_center;
		private float maxV_center;

		protected int frameCounter = 0;
		protected int frameCurrent = 0;
		
		private TerrainIcon(int id, TextureTerrainMap map, String name) {
			this.id = id;
			this.map = map;
			this.name = name;
			this.originX = (id % (map.width / 48)) * 48;
			this.originY = (id / (map.width / 48)) * 48;
			this.minU = (float)originX / (float)map.width;
			this.minV = (float)originY / (float)map.height;
			this.maxU = (float)(originX + 48) / (float)map.width;
			this.maxV = (float)(originY + 48) / (float)map.height;
			this.originX_center = originX + 16;
			this.originY_center = originY + 16;
			this.minU_center = (float)originX_center / (float)map.width;
			this.minV_center = (float)originY_center / (float)map.height;
			this.maxU_center = (float)(originX_center + 16) / (float)map.width;
			this.maxV_center = (float)(originY_center + 16) / (float)map.height;
		}

		@Override
		public int getOriginX() {
			return originX_center;
		}

		@Override
		public int getOriginY() {
			return originY_center;
		}

		@Override
		public float getMinU() {
			return minU_center;
		}

		@Override
		public float getMaxU() {
			return maxU_center;
		}

		@Override
		public float getInterpolatedU(double var1) {
			float var3 = this.maxU_center - this.minU_center;
			return this.minU_center + var3 * ((float) var1 / 16.0F);
		}

		@Override
		public float getMinV() {
			return minV_center;
		}

		@Override
		public float getMaxV() {
			return maxV_center;
		}

		@Override
		public float getInterpolatedV(double var1) {
			float var3 = this.maxV_center - this.minV_center;
			return this.minV_center + var3 * ((float) var1 / 16.0F);
		}

		@Override
		public String getIconName() {
			return name == null ? "missingno" : name;
		}

		@Override
		public int getSheetWidth() {
			return map.width;
		}

		@Override
		public int getSheetHeight() {
			return map.height;
		}
		
		private void updateAnimation() {
			if(frames != null) {
				int var4 = this.frameCounter;
				this.frameCounter = (this.frameCounter + 1) % this.framesIdx.length;
				int i = framesIdx[this.frameCounter];
				if (this.frameCurrent != i) {
					this.frameCurrent = i;
					map.replaceTexture(this, frames[i]);
				}
			}
		}
		
		private void loadData() {
			byte[] data = EaglerAdapter.loadResourceBytes("/" + map.basePath + name + ".png");
			if(data == null) {
				map.replaceTexture(this, map.missingData);
			}else {
				EaglerImage img = EaglerImage.loadImage(data);
				if(img == null) {
					map.replaceTexture(this, map.missingData);
				}else {
					int divs = img.h / 16;
					if(divs == 1) {
						this.frames = null;
						this.framesIdx = null;
						map.replaceTexture(this, generateMip(img));
					}else {
						frames = new EaglerImage[divs][];
						for(int i = 0; i < divs; ++i) {
							frames[i] = generateMip(img.getSubImage(0, i * 16, 16, 16));
						}
						String dat = EaglerAdapter.fileContents("/" + map.basePath + name + ".txt");
						if(dat != null) System.out.println("Found animation info for: " + map.basePath + name + ".png");
						if(dat == null || (dat = dat.trim()).isEmpty()) {
							framesIdx = new int[frames.length];
							for(int i = 0; i < frames.length; ++i) {
								framesIdx[i] = i;
							}
						}else {
							String[] fd = dat.split(",");
							int len = 0;
							for(int i = 0; i < fd.length; ++i) {
								int j = fd[i].indexOf('*');
								len += (j == -1 ? 1 : Integer.parseInt(fd[i].substring(j + 1)));
							}
							framesIdx = new int[len];
							len = 0;
							for(int i = 0; i < fd.length; ++i) {
								int j = fd[i].indexOf('*');
								if(j == -1) {
									framesIdx[len++] = Integer.parseInt(fd[i]);
								}else {
									int c = Integer.parseInt(fd[i].substring(0, j));
									int l = Integer.parseInt(fd[i].substring(j + 1));
									for(int k = 0; k < l; ++k) {
										framesIdx[len++] = c;
									}
								}
							}
						}
						map.replaceTexture(this, this.frames[framesIdx[0]]);
					}
				}
			}
		}
		
	}
	
	private final String basePath;
	private final int width;
	private final int height;
	private TerrainIcon missingImage;
	private ArrayList<TerrainIcon> iconList;
	public final int texture;
	private final EaglerImage[] missingData;
	
	private static final IntBuffer uploadBuffer = EaglerAdapter.isWebGL ? IntBuffer.wrap(new int[4096]) : ByteBuffer.allocateDirect(4096 << 2).order(ByteOrder.nativeOrder()).asIntBuffer();
	
	private int nextSlot = 0;
	
	public TextureTerrainMap(int size, String par2, String par3Str, EaglerImage par4BufferedImage) {
		this.width = size;
		this.height = size;
		this.basePath = par3Str;
		this.missingImage = new TerrainIcon(nextSlot++, this, null);
		this.iconList = new ArrayList();
		this.texture = EaglerAdapter.glGenTextures();
		EaglerAdapter.glBindTexture(EaglerAdapter.GL_TEXTURE_2D, texture);
		int levelW = width;
		int levelH = height;
		IntBuffer blank = GLAllocation.createDirectIntBuffer(levelW * levelH);
		for(int i = 0; i < blank.limit(); ++i) {
			blank.put(i, ((i / width + (i % width)) % 2 == 0) ? 0xffff00ff : 0xff000000);
		}
		EaglerAdapter.glTexParameteri(EaglerAdapter.GL_TEXTURE_2D, EaglerAdapter.GL_TEXTURE_MIN_FILTER, EaglerAdapter.GL_NEAREST_MIPMAP_LINEAR);
		EaglerAdapter.glTexParameteri(EaglerAdapter.GL_TEXTURE_2D, EaglerAdapter.GL_TEXTURE_MAG_FILTER, EaglerAdapter.GL_NEAREST);
		EaglerAdapter.glTexParameteri(EaglerAdapter.GL_TEXTURE_2D, EaglerAdapter.GL_TEXTURE_WRAP_S, EaglerAdapter.GL_CLAMP);
		EaglerAdapter.glTexParameteri(EaglerAdapter.GL_TEXTURE_2D, EaglerAdapter.GL_TEXTURE_WRAP_T, EaglerAdapter.GL_CLAMP);
		EaglerAdapter.glTexParameteri(EaglerAdapter.GL_TEXTURE_2D, EaglerAdapter.GL_TEXTURE_MAX_LEVEL, 4);
		EaglerAdapter.glTexParameterf(EaglerAdapter.GL_TEXTURE_2D, EaglerAdapter.GL_TEXTURE_MAX_ANISOTROPY, 1.0f);
		for(int i = 0; i < 5; ++i) {
			blank.clear().limit(levelW * levelH);
			EaglerAdapter.glTexImage2D(EaglerAdapter.GL_TEXTURE_2D, i, EaglerAdapter.GL_RGBA, levelW, levelH, 0, EaglerAdapter.GL_RGBA, EaglerAdapter.GL_UNSIGNED_BYTE, blank);
			levelW /= 2;
			levelH /= 2;
		}
		replaceTexture(missingImage, missingData = generateMip(par4BufferedImage));
	}
	
	public static EaglerImage[] generateMip(EaglerImage src16x16) {
		EaglerImage[] ret = new EaglerImage[5];
		ret[0] = populateAlpha(src16x16);
		ret[1] = generateLevel(ret[0]); ret[0] = create3x3(ret[0]);
		ret[2] = generateLevel(ret[1]); ret[1] = create3x3(ret[1]);
		ret[3] = generateLevel(ret[2]); ret[2] = create3x3(ret[2]);
		ret[4] = create3x3_2(generateLevel(ret[3])); ret[3] = create3x3(ret[3]);
		return ret;
	}
	
	public static EaglerImage generateLevel(EaglerImage src) {
		EaglerImage e = new EaglerImage(src.w / 2, src.h / 2, true);
		for(int y = 0; y < e.h; ++y) {
			for(int x = 0; x < e.w; ++x) {
				int x2 = x * 2;
				int y2 = y * 2;
				int a = src.data[y2 * src.w + x2];
				int b = src.data[y2 * src.w + x2 + 1];
				int c = src.data[(y2 + 1) * src.w + x2];
				int d = src.data[(y2 + 1) * src.w + x2 + 1];
				int ca = (((a >> 24) & 255) + ((b >> 24) & 255) + ((c >> 24) & 255) + ((d >> 24) & 255)) >> 2;
				int cr = (((a >> 16) & 255) + ((b >> 16) & 255) + ((c >> 16) & 255) + ((d >> 16) & 255)) >> 2;
				int cg = (((a >> 8) & 255) + ((b >> 8) & 255) + ((c >> 8) & 255) + ((d >> 8) & 255)) >> 2;
				int cb = ((a & 255) + (b & 255) + (c & 255) + (d & 255)) >> 2;
				e.data[y * e.w + x] = (ca << 24) | (cr << 16) | (cg << 8) | cb;
			}
		}
		return e;
	}
	
	public static EaglerImage premultiplyAlpha(EaglerImage src) {
		EaglerImage e = new EaglerImage(src.w, src.h, true);
		for(int i = 0; i < src.data.length; ++i) {
			int x = src.data[i];
			int a = (x >> 24) & 255;
			int r = (x >> 16) & 255;
			int g = (x >> 8) & 255;
			int b = x & 255;
			r = (r * a) / 255;
			g = (g * a) / 255;
			b = (b * a) / 255;
			e.data[i] = (a << 24) | (r << 16) | (g << 8) | b;
		}
		return e;
	}
	
	public static EaglerImage populateAlpha(EaglerImage src) {
		EaglerImage ret = new EaglerImage(src.w, src.h, true);
		int reducedR = 0;
		int reducedG = 0;
		int reducedB = 0;
		int divisor = 0;
		int[] array = src.data;
		for(int i = 0; i < array.length; ++i) {
			int x = array[i];
			int a = (x >> 24) & 255;
			if(a > 2) {
				reducedR += (x >> 16) & 255;
				reducedG += (x >> 8) & 255;
				reducedB += x & 255;
				++divisor;
			}
		}
		if(divisor == 0) {
			reducedR = 0;
			reducedG = 0;
			reducedB = 0;
		}else {
			reducedR /= divisor;
			reducedG /= divisor;
			reducedB /= divisor;
		}
		int reducedR2, reducedG2, reducedB2, blend1, blend2, blend3, blend4, j;
		int alpha = (reducedR << 16) | (reducedG << 8) | reducedB;
		for(int i = 0; i < array.length; ++i) {
			int x = array[i];
			int a = (x >> 24) & 255;
			if(a < 2) {
				reducedR2 = 0;
				reducedG2 = 0;
				reducedB2 = 0;
				divisor = 0;
				blend1 = i + 1;
				blend2 = i - 1;
				blend3 = i + src.w;
				blend4 = i - src.w;
				if(blend1 >= 0 && blend1 < array.length) {
					j = array[blend1];
					if(((x >> 24) & 255) > 2){
						reducedR2 += (x >> 16) & 255;
						reducedG2 += (x >> 8) & 255;
						reducedB2 += x & 255;
						++divisor;
					}
				}
				if(blend2 >= 0 && blend2 < array.length) {
					j = array[blend2];
					if(((x >> 24) & 255) > 2){
						reducedR2 += (x >> 16) & 255;
						reducedG2 += (x >> 8) & 255;
						reducedB2 += x & 255;
						++divisor;
					}
				}
				if(blend3 >= 0 && blend3 < array.length) {
					j = array[blend3];
					if(((x >> 24) & 255) > 2){
						reducedR2 += (x >> 16) & 255;
						reducedG2 += (x >> 8) & 255;
						reducedB2 += x & 255;
						++divisor;
					}
				}
				if(blend4 >= 0 && blend4 < array.length) {
					j = array[blend4];
					if(((x >> 24) & 255) > 2){
						reducedR2 += (x >> 16) & 255;
						reducedG2 += (x >> 8) & 255;
						reducedB2 += x & 255;
						++divisor;
					}
				}
				if(divisor == 0) {
					ret.data[i] = alpha;
				}else {
					ret.data[i] = ((reducedR2 / divisor) << 16) | ((reducedG2 / divisor) << 8) | (reducedB2 / divisor);
				}
			}else {
				ret.data[i] = src.data[i];
			}
		}
		return ret;
	}
	
	public static EaglerImage create3x3(EaglerImage src) {
		EaglerImage ret = new EaglerImage(src.w * 3, src.h * 3, true);
		for(int y = 0; y < src.h; ++y) {
			for(int x = 0; x < src.w; ++x) {
				int pixel = src.data[y * src.w + x];
				
				if(y != src.h - 1) {
					ret.data[(src.h - 1 - y) * ret.w + (x)] = pixel;
					ret.data[(src.h - 1 - y) * ret.w + (x + src.w)] = pixel;
					ret.data[(src.h - 1 - y) * ret.w + (x + src.w*2)] = pixel;
				}
				
				ret.data[(y + src.h) * ret.w + (x)] = pixel;
				ret.data[(y + src.h) * ret.w + (x + src.w)] = pixel;
				ret.data[(y + src.h) * ret.w + (x + src.w*2)] = pixel;

				if(y != 0) {
					ret.data[(src.h*3 - 1 - y) * ret.w + (x)] = pixel;
					ret.data[(src.h*3 - 1 - y) * ret.w + (x + src.w)] = pixel;
					ret.data[(src.h*3 - 1 - y) * ret.w + (x + src.w*2)] = pixel;
				}
			}
		}
		return ret;
	}
	
	public static EaglerImage create3x3_2(EaglerImage src) {
		EaglerImage ret = new EaglerImage(3, 3, true);
		ret.data[0] = src.data[0];
		ret.data[1] = src.data[0];
		ret.data[2] = src.data[0];
		ret.data[3] = src.data[0];
		ret.data[4] = src.data[0];
		ret.data[5] = src.data[0];
		ret.data[6] = src.data[0];
		ret.data[7] = src.data[0];
		ret.data[8] = src.data[0];
		return ret;
	}

	public void refreshTextures() {
		iconList.clear();
		nextSlot = 1;
		Block[] var1 = Block.blocksList;
		int var2 = var1.length;

		for (int var3 = 0; var3 < var2; ++var3) {
			Block var4 = var1[var3];

			if (var4 != null) {
				var4.registerIcons(this);
			}
		}

		Minecraft.getMinecraft().renderGlobal.registerDestroyBlockIcons(this);
		RenderManager.instance.updateIcons(this);
		
		for(TerrainIcon t : iconList) {
			t.loadData();
		}
	}
	
	private void replaceTexture(TerrainIcon icon, EaglerImage[] textures) {
		int levelW = width;
		int levelH = height;
		int divisor = 1;
		EaglerAdapter.glBindTexture(EaglerAdapter.GL_TEXTURE_2D, texture);
		for(int i = 0; i < 5; i++) {
			uploadBuffer.clear();
			uploadBuffer.put(textures[i].data);
			uploadBuffer.flip();
			EaglerAdapter.glTexSubImage2D(EaglerAdapter.GL_TEXTURE_2D, i, icon.originX / divisor, icon.originY / divisor, 48 / divisor, 48 / divisor, EaglerAdapter.GL_RGBA, EaglerAdapter.GL_UNSIGNED_BYTE, uploadBuffer);
			levelW /= 2;
			levelH /= 2;
			divisor *= 2;
		}
	}

	public void updateAnimations() {
		for(TerrainIcon t : iconList) {
			t.updateAnimation();
		}
	}

	public Icon registerIcon(String par1Str) {
		if(par1Str != null) {
			for(TerrainIcon t : iconList) {
				if(par1Str.equals(t.name)) {
					return t;
				}
			}
			TerrainIcon ret = new TerrainIcon(nextSlot++, this, par1Str);
			iconList.add(ret);
			return ret;
		}else{
			return missingImage;
		}
	}

	public Icon getMissingIcon() {
		return missingImage;
	}
}
