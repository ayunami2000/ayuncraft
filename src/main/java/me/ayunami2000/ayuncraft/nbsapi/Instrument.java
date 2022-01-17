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
	XYLOPHONE (9);
	
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
	 * @param ID The instrument ID (0-9).
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
		default: throw new IllegalArgumentException("ID must be from 1 to 9.");
		}
	}
}
