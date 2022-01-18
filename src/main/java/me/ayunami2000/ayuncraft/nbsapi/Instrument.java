package me.ayunami2000.ayuncraft.nbsapi;

/**
 * Note block's instrument.
 * @author Le Duy Quang
 *
 */
public enum Instrument {
	HARP (0),
	BASS (1),
	DRUM (2),
	SNARE (3),
	CLICK (4),
	GUITAR (5),
	FLUTE (6),
	BELL (7),
	CHIME (8),
	XYLOPHONE (9),
	IRON_XYLOPHONE (10),
	COW_BELL (11),
	DIDGERIDOO (12),
	BIT (13),
	BANJO (14),
	PLING (15),
	CUSTOM (-1);

	private final int ID;
	private Instrument(int ID) {
		this.ID = ID;
	}

	/**
	 * Returns an ID of the instrument to be written in NBS files.
	 * @return The ID.
	 */
	public int getID() {
		return ID;
	}

	/**
	 * Determines the instrument from its NBS file ID.
	 * @param ID The instrument ID (0-15).
	 * @return The corresponding instrument.
	 * @throws IllegalArgumentException
	 */
	public static Instrument fromID(int ID) throws IllegalArgumentException {
		switch (ID) {
			case 0: return HARP;
			case 1: return BASS;
			case 2: return DRUM;
			case 3: return SNARE;
			case 4: return CLICK;
			case 5: return GUITAR;
			case 6: return FLUTE;
			case 7: return BELL;
			case 8: return CHIME;
			case 9: return XYLOPHONE;
			case 10: return IRON_XYLOPHONE;
			case 11: return COW_BELL;
			case 12: return DIDGERIDOO;
			case 13: return BIT;
			case 14: return BANJO;
			case 15: return PLING;
			default: return CUSTOM;//throw new IllegalArgumentException("ID must be from 0 to 15.");
		}
	}
}