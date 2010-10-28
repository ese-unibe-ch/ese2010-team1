package models;

import java.util.Comparator;

/**
 * Compares two <code>Entrys</code> by their timestamp.
 */
public class EntryComperator implements Comparator<Entry> {

	public int compare(Entry o1, Entry o2) {

		if (o2.rating() - o1.rating() > 0) {
			return 1;
		} else if (o2.rating() - o1.rating() < 0) {
			return -1;
		}
		return 0;
	}
}
