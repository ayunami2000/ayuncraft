package me.ayunami2000.ayuncraft.nbsapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The utilities used in this API. Not something for you... :/
 * @author Le Duy Quang
 *
 */
public class Utils {
	/**
	 * Converts a song board into a list of notes so that it can be written into a .nbs file.
	 * @param board The song board.
	 * @return The note list.
	 */
	protected static List<WritableNote> convertToWritable(List<Layer> board) {
		List<Integer> ticks = new ArrayList<Integer>();
		for (Layer l : board) {
			for (int n : l.getNoteList().keySet()) {
				if (!ticks.contains(n)) ticks.add(n);
			}
		}
		Collections.sort(ticks);
		
		List<WritableNote> result = new ArrayList<WritableNote>();
		List<WritableNote> thisTick = new ArrayList<WritableNote>();
		for (int i : ticks) {
			thisTick.clear();
			int currentLayer = -1;
			for (Layer l : board) {
				currentLayer ++;
				if (l.getNoteList().containsKey(i)) {
					Note n = l.getNoteList().get(i);
					thisTick.add(new WritableNote(n.getInstrument(), n.getPitch(), currentLayer, i));
				}
			}
			Collections.sort(thisTick, Comparator.comparing(WritableNote::getLayer));
			result.addAll(thisTick);
		}
		return result;
	}
}
