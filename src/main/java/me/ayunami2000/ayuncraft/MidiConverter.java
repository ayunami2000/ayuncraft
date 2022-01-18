package me.ayunami2000.ayunAudioStreamer;

import me.ayunami2000.ayuncraft.NoteblockPlayer;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class MidiConverter {
    public static int[] instrument_offsets = new int[] {
            54, //harp
            0, //basedrum
            0, //snare
            0, //hat
            30, //bass
            66, //flute
            78, //bell
            42, //guitar
            78, //chime
            78, //xylophone
            54, //iron xylophone
            66, //cow bell
            30, //didgeridoo
            54, //bit
            54, //banjo
            54, //electric piano
    };

    public static String midiToTxt() {
        String midiTxtSong = "";
        try {
            BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(NoteblockPlayer.songdata));
            TreeMap<Long, ArrayList<Integer>> noteMap = MidiConverter.getMidi(bis);
            for (Map.Entry<Long, ArrayList<Integer>> entry : noteMap.entrySet()) {
                for (int i=0;i<entry.getValue().size();i++) {
                    long time = entry.getKey();
                    int note = entry.getValue().get(i);
                    int velocity = velocityMap.get(entry.getKey()).get(i);
                    midiTxtSong += ((int) Math.floor(20.0*time/1000.0)) + ":" + (note % 25) + ":" + ((int) Math.floor(note / 25)) + ":" + velocity + "\n";
                }
            }
            if (midiTxtSong.endsWith("\n")) midiTxtSong = midiTxtSong.substring(0, midiTxtSong.length() - 1);
        }catch(Exception e){}
        return midiTxtSong;
    }

    public static TreeMap<Long, ArrayList<Integer>> noteMap;
    public static TreeMap<Long, ArrayList<Integer>> velocityMap;

    public static TreeMap<Long, ArrayList<Integer>> getMidi(BufferedInputStream downloadStream) throws Exception {
        noteMap  = new TreeMap<>();
        velocityMap = new TreeMap<>();

        Sequence sequence = MidiSystem.getSequence(downloadStream);

        long tpq = sequence.getResolution();

        ArrayList<MidiEvent> tempoEvents = new ArrayList<>();
        for (Track track : sequence.getTracks()) {
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof MetaMessage) {
                    MetaMessage mm = (MetaMessage) message;
                    if (mm.getType() == 0x51) { //SET_TEMPO
                        tempoEvents.add(event);
                    }
                }
            }
        }

        Collections.sort(tempoEvents, new Comparator<MidiEvent>() {
            @Override
            public int compare(MidiEvent a, MidiEvent b) {
                return (Long.valueOf(a.getTick())).compareTo(b.getTick());
            }
        });

        for (Track track : sequence.getTracks()) {

            long microTime = 0;
            int[] instrumentIds = new int[16];
            //int apparent_mpq = (int) (sequence.getMicrosecondLength()/sequence.getTickLength()*tpq);
            int mpq = 500000;
            int tempoEventIdx = 0;
            long prevTick = 0;

            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();

                while (tempoEventIdx < tempoEvents.size() && event.getTick() > tempoEvents.get(tempoEventIdx).getTick()) {
                    long deltaTick = tempoEvents.get(tempoEventIdx).getTick() - prevTick;
                    prevTick = tempoEvents.get(tempoEventIdx).getTick();
                    microTime += (mpq/tpq) * deltaTick;

                    MetaMessage mm = (MetaMessage) tempoEvents.get(tempoEventIdx).getMessage();
                    byte[] data = mm.getData();
                    int new_mpq = (data[2]&0xFF) | ((data[1]&0xFF)<<8) | ((data[0]&0xFF)<<16);
                    if (new_mpq != 0) mpq = new_mpq;
                    tempoEventIdx++;
                }

                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
                        instrumentIds[sm.getChannel()] = sm.getData1();
                    }
                    else if (sm.getCommand() == ShortMessage.NOTE_ON) {
                        if (sm.getData2() == 0) continue;
                        int key = sm.getData1();
                        int velocity = sm.getData2();
                        long deltaTick = event.getTick() - prevTick;
                        prevTick = event.getTick();
                        microTime += (mpq/tpq) * deltaTick;
                        if (sm.getChannel() == 9) {
                            processMidiNote(128, key, velocity, microTime);
                        }
                        else {
                            processMidiNote(instrumentIds[sm.getChannel()], key, velocity, microTime);
                        }
                    }
                    else {
                    }
                }
            }
        }

        downloadStream.close();

        return noteMap;
    }

    public static void processMidiNote(int midiInstrument, int midiPitch, int midiVelocity, long microTime) {
        int[] noteData=noteConv(midiInstrument,midiPitch);

        long milliTime = microTime / 1000;
        if (noteData[0] >= 0) {
            int noteId = (noteData[1]-instrument_offsets[noteData[0]]) + noteData[0]*25;

            if (!noteMap.containsKey(milliTime)) {
                noteMap.put(milliTime, new ArrayList<Integer>());
                velocityMap.put(milliTime, new ArrayList<Integer>());
            }
            if (!noteMap.get(milliTime).contains(noteId)) {
                noteMap.get(milliTime).add(noteId);
                velocityMap.get(milliTime).add(midiVelocity);
            }
        }

    }

    public static int[] noteConv(int midiInstrument, int midiPitch) {
        int minecraftInstrument = -1;
        if ((midiInstrument >= 0 && midiInstrument <= 7) || (midiInstrument >= 24 && midiInstrument <= 31)) { //normal
            if (midiPitch >= 54 && midiPitch <= 78) {
                minecraftInstrument = 0; //piano
            }
            else if (midiPitch >= 30 && midiPitch <= 54) {
                minecraftInstrument = 4; //bass
            }
            else if (midiPitch >= 78 && midiPitch <= 102) {
                minecraftInstrument = 6; //bells
            }
        }
        else if (midiInstrument >= 8 && midiInstrument <= 15) { //chromatic percussion
            if (midiPitch >= 54 && midiPitch <= 78) {
                minecraftInstrument = 10; //iron xylophone
            }
            else if (midiPitch >= 78 && midiPitch <= 102) {
                minecraftInstrument = 9; //xylophone
            }
            else if (midiPitch >= 30 && midiPitch <= 54) {
                minecraftInstrument = 4; //bass
            }
        }
        else if ((midiInstrument >= 16 && midiInstrument <= 23) || (midiInstrument >= 32 && midiInstrument <= 71) || (midiInstrument >= 80 && midiInstrument <= 111)) { //synth
            if (midiPitch >= 54 && midiPitch <= 78) {
                minecraftInstrument = 13; //bit
            }
            else if (midiPitch >= 30 && midiPitch <= 54) { //didgeridoo
                minecraftInstrument = 12;
            }
            else if (midiPitch >= 78 && midiPitch <= 102) { //bells
                minecraftInstrument = 6;
            }
        }
        else if ((midiInstrument >= 72 && midiInstrument <= 79)) { //woodwind
            if (midiPitch >= 66 && midiPitch <= 90) {
                minecraftInstrument = 5; //flute
            }
            else if (midiPitch >= 30 && midiPitch <= 54) { //didgeridoo
                minecraftInstrument = 12;
            }
            else if (midiPitch >= 54 && midiPitch <= 78) {
                minecraftInstrument = 13; //bit
            }
            else if (midiPitch >= 78 && midiPitch <= 102) { //bells
                minecraftInstrument = 6;
            }
        }
        else if (midiInstrument == 128) {
            if (midiPitch == 35 || midiPitch == 36 || midiPitch == 41 || midiPitch == 43 || midiPitch == 45 || midiPitch == 57) {
                minecraftInstrument = 1; //bass drum
            }
            else if (midiPitch == 38 || midiPitch == 39 || midiPitch == 40 || midiPitch == 54 || midiPitch == 69 || midiPitch == 70 || midiPitch == 73 || midiPitch == 74 || midiPitch == 78 || midiPitch == 79) {
                minecraftInstrument = 2; //snare
            }
            else if (midiPitch == 37 || midiPitch == 42 || midiPitch == 44 || midiPitch == 46 || midiPitch == 49 || midiPitch == 51 || midiPitch == 52 || midiPitch == 55 || midiPitch == 57 || midiPitch == 59) {
                minecraftInstrument = 3; //hat
            }
            midiPitch = 0;
        }

        return new int[]{minecraftInstrument,midiPitch};
    }
}