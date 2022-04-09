package com.baislsl.png.util;

public class ReverseFilter {
	private ReverseFilter() {
	}

	private static int paethPredictor(int a, int b, int c) {
		int p = a + b - c;
		int pa = Math.abs(p - a), pb = Math.abs(p - b), pc = Math.abs(p - c);
		if (pa <= pb && pa <= pc)
			return a;
		if (pb <= pc)
			return b;
		return c;
	}

	// apply reverse Filter Algorithms to byte data
	// bpp = 3
	public static byte[][] apply(byte[] data, int width, int height, int bpp) {
		int[] filterType = new int[height];
		int[][] blocks = new int[height][width * bpp];
		int dataIndex = 0;
		for (int i = 0; i < height; i++) {
			filterType[i] = (int) (data[dataIndex++]) & 0xFF;
			for (int j = 0; j < width * bpp; j++) {
				blocks[i][j] = (int) (data[dataIndex++]) & 0xFF;
			}
		}
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width * bpp; j++) {
				int prior = (i == 0) ? 0 : blocks[i - 1][j];
				int rawBpp = (j < bpp) ? 0 : blocks[i][j - bpp];
				int bppPrior = (i == 0 || j < bpp) ? 0 : blocks[i - 1][j - bpp];
				switch (filterType[i]) {
				case 0: // none
					break;
				case 1: // sub
					blocks[i][j] = blocks[i][j] + rawBpp;
					break;
				case 2: // up
					blocks[i][j] = blocks[i][j] + prior;
					break;
				case 3: // average
					blocks[i][j] = blocks[i][j] + (rawBpp + prior) / 2;
					break;
				case 4: // paeth
					blocks[i][j] = blocks[i][j] + paethPredictor(rawBpp, prior, bppPrior);
					break;
				default:
				}
				blocks[i][j] &= 0xff;
			}
		}

		byte[][] result = new byte[height][width * bpp];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width * bpp; j++) {
				result[i][j] = (byte) blocks[i][j];
			}
		}
		return result;
	}

}
