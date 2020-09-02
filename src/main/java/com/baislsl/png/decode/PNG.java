package com.baislsl.png.decode;

import com.baislsl.png.chunk.*;
import com.baislsl.png.util.ReverseFilter;

import net.lax1dude.eaglercraft.EaglerInflater;

import java.io.IOException;

/**
 * Created by baislsl on 17-7-9.
 */
public class PNG {
    //private final static Logger LOG = LoggerFactory.getLogger(PNG.class);
    public IHDR ihdr;
    public IDATManager idats = new IDATManager();
    public PLTE plte;
    public IEND iend;

    public PNG() {
    }

    public PNG(IHDR ihdr, IDATManager idats, PLTE plte, IEND iend) {
        this.ihdr = ihdr;
        this.idats = idats;
        this.plte = plte;
        this.iend = iend;
    }

    public int[] getColor() throws DecodeException {
        byte[] rawData = idats.getIDATData();
        byte[] uncompressData = applyLZ77(rawData);
        byte[][] transferData = applyReverseFilter(uncompressData);
        int[] colors = applyColorTransfer(transferData);
        return colors;
    }

    private int[] applyColorTransfer(byte[][] data) throws DecodeException {
        int bpp = ihdr.getBpp();
        int width = (int) ihdr.getWidth();
        int height = (int) ihdr.getHeight();
        int colorType = ihdr.getColorType();
        int bitDepth = ihdr.getBitDepth();
        int[] colors = new int[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
            	int idx = i * width + j;
                switch (colorType) {
                    case 2:
                        if (bitDepth == 8) {  // bpp = 3
                            colors[idx] = ((int)data[i][bpp * j] & 0xff) << 16 | ((int)data[i][bpp * j + 1] & 0xff) << 8 | ((int)data[i][bpp * j + 2] & 0xff);
                        }else {
                            throw new DecodeException("not supported");
                        }
                        break;
                    case 6:
                        if (bitDepth == 8) {  // bpp = 4
                            colors[idx] = ((int)data[i][bpp * j] & 0xff) << 16 | ((int)data[i][bpp * j + 1] & 0xff) << 8 | ((int)data[i][bpp * j + 2] & 0xff) | ((int)data[i][bpp * j + 3] & 0xff) << 24;
                        }else {
                            throw new DecodeException("not supported");
                        }
                        break;
                    case 3:
                    	int gap = 8 / bitDepth;
                    	int a = (1 << bitDepth) - 1;
                    	colors[idx] = plte.getColor(data[i][j / gap] & a);
                        break;
                    default:
                        throw new DecodeException("Do not support color type " + colorType);
                }
            }
        }

        return colors;
    }

    private byte[] applyLZ77(byte[] data) throws DecodeException {
        byte[] result;
        try {
            result = EaglerInflater.uncompress(data);
        } catch (IOException e) {
            //LOG.error("LZ77 decode error", e);
            throw new DecodeException(e);
        }
        //LOG.info("Size after decode={}", result.length);
        return result;
    }

    private byte[][] applyReverseFilter(byte[] data) {
        int width = (int) ihdr.getWidth(), height = (int) ihdr.getHeight();
        return ReverseFilter.apply(data, width, height, ihdr.getBpp());
    }

    public void setIdats(IDATManager idats) {
        this.idats = idats;
    }

    public void setIhdr(IHDR ihdr) {
        this.ihdr = ihdr;
    }

    public void setPlte(PLTE plte) {
        this.plte = plte;
    }

    public void setIend(IEND iend) {
        this.iend = iend;
    }

    public void add(IDAT idat) throws DecodeException {
        idats.add(idat);
    }

    public long getWidth() {
        return ihdr.getWidth();
    }

    public long getHeight() {
        return ihdr.getHeight();
    }

}
