package me.ayunami2000.ayuncraft.nbsapi;

/**
 * A note in a song.
 * @author Le Duy Quang
 *
 */
public class Note {
	private Instrument instrument;
	private byte pitch;
	private byte velocity;
	private int panning;
	private short precisePitch;

	/**
	 * A note in a song.
	 * @param instrument The instrument of the note.
	 * @param pitch The pitch of the note (0-87).
	 * @param velocity The velocity of the note (0-100).
	 * @param panning The panning of the note (0-100).
	 * @param precisePitch The precise pitch of the note (-32767-32767).
	 * @throws IllegalArgumentException
	 */
	public Note(Instrument instrument, byte pitch, byte velocity, int panning, short precisePitch) throws IllegalArgumentException {
		if (pitch < 0 || pitch > 87) throw new IllegalArgumentException("Pitch must be from 0 to 87.");
		if (velocity < 0 || velocity > 100) throw new IllegalArgumentException("Velocity must be from 0 to 100.");
		if (panning < 0 || panning > 200) throw new IllegalArgumentException("Panning must be from 0 to 100.");
		if (precisePitch < -32767 || precisePitch > 32767) throw new IllegalArgumentException("Precise pitch must be from -32767 to 32767.");
		setInstrument(instrument);
		setPitch(pitch);
		setVelocity(velocity);
		setPanning(panning);
		setPrecisePitch(precisePitch);
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

	public byte getVelocity() {
		return velocity;
	}

	public void setVelocity(byte velocity) throws IllegalArgumentException {
		if (velocity < 0 || velocity > 100) throw new IllegalArgumentException("Velocity must be from 0 to 100.");
		this.velocity = velocity;
	}

	public int getPanning() {
		return panning;
	}

	public void setPanning(int panning) throws IllegalArgumentException {
		if (panning < 0 || panning > 200) throw new IllegalArgumentException("Panning must be from 0 to 100.");
		this.panning = panning;
	}

	public short getPrecisePitch() {
		return precisePitch;
	}

	public void setPrecisePitch(short precisePitch) throws IllegalArgumentException {
		if (precisePitch < -32767 || precisePitch > 32767) throw new IllegalArgumentException("Precise pitch must be from -32767 to 32767.");
		this.precisePitch = precisePitch;
	}
}