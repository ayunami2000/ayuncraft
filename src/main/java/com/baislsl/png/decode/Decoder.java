package com.baislsl.png.decode;

import com.baislsl.png.chunk.ChunkType;
import com.baislsl.png.util.CRC;
import com.baislsl.png.util.ByteHandler;

import java.io.IOException;
import java.io.InputStream;

import static com.baislsl.png.util.ByteHandler.byteToLong;

/**
 * Created by baislsl on 17-7-9.
 */
public class Decoder {
	// private final static Logger LOG = LoggerFactory.getLogger(Decoder.class);
	private final InputStream in;

	private final static char[] head = { 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a };

	public Decoder(InputStream in) {
		this.in = in;
	}

	private void readHeader() throws DecodeException, IOException {
		byte[] header = readBytes(8);
		for (int i = 0; i < 8; i++) {
			if ((header[i] & 0xff) != (int) head[i])
				throw new DecodeException("It seems that this is not a PNG files");
		}
		// LOG.info(ByteHandler.byteToString(header));
	}

	private boolean readChunk(PNG png, String chunkName, byte[] length, byte[] type, byte[] data, byte[] crc)
			throws IOException, DecodeException {
		for (ChunkType chunkType : ChunkType.values()) {
			if (chunkType.name().equalsIgnoreCase(chunkName)) {
				chunkType.apply(png, length, type, data, crc);
				return true;
			}
		}
		return false;
	}

	private boolean checkCrc(byte[] data, long crcNumber) {
		return crcNumber == CRC.crc(data, data.length);
	}

	private boolean checkCrc(byte[] type, byte[] data, byte[] crc) {
		long crcNumber = byteToLong(crc);
		byte[] crcData = new byte[4 + data.length];
		System.arraycopy(type, 0, crcData, 0, 4);
		System.arraycopy(data, 0, crcData, 4, data.length);

		return checkCrc(crcData, crcNumber);
	}

	public PNG readInPNG() throws IOException, DecodeException {
		PNG png = new PNG();
		readHeader();

		String chunkName;
		do {
			byte[] length = readBytes(4);
			long size = byteToLong(length);
			byte[] type = readBytes(4);
			chunkName = ByteHandler.byteToString(type).toUpperCase();
			if ("IEND".equals(chunkName)) {
				break;
			}
			byte[] data = readBytes((int) size);
			byte[] crc = readBytes(4);
			// LOG.info(ByteHandler.byteToString(type));

			boolean found = readChunk(png, chunkName, length, type, data, crc);
			if (!found) {
				// LOG.info("Not support chunk name {}", chunkName);
			}

			boolean crcMatch = checkCrc(type, data, crc);
			if (!crcMatch) {
				throw new DecodeException("Error data stream for incorrect crc");
			}
		} while (!"IEND".equals(chunkName));
		return png;
	}

	private byte[] readBytes(int size) throws IOException {
		byte[] result = new byte[size];
		int ret = in.read(result, 0, size);
		if (ret == -1)
			return null;
		return result;
	}

}
