package me.ayunami2000.ayuncraft.nbsapi;

/**
 * A note in a song.
 * @author Le Duy Quang
 *
 */
public class Note {
	private Instrument instrument;
	private byte pitch;
	
	/**
	 * A note in a song.
	 * @param instrument The instrument of the note.
	 * @param pitch The pitch of the note (0-87).
	 * @throws IllegalArgumentException
	 */
	public Note(Instrument instrument, byte pitch) throws IllegalArgumentException {
		if (pitch < 0 || pitch > 87) throw new IllegalArgumentException("Pitch must be from 0 to 87.");
		setInstrument(instrument);
		setPitch(pitch);
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public byte getPitch() {
		return pitch;
	}

	public void setPitch(byte pitch) throws IllegalArgumentException {
		if (pitch < 0 || pitch > 87) throw new IllegalArgumentException("Pitch must be from 0 to 87.");
		this.pitch = pitch;
	}
}
