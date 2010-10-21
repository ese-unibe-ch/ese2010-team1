package models;

import java.util.Comparator;

/**
 * Compares two <code>Entrys</code> by their timestamp.
 */
public class EntryComperator implements Comparator {

	public int compare(Object o1, Object o2) {
		if ((o1 instanceof Entry) && (o2 instanceof Entry))
			if (((Entry) o2).rating() - ((Entry) o1).rating() > 0) {
				return 1;
			} else if (((Entry) o2).rating() - ((Entry) o1).rating() < 0) {
				return -1;
			}

		return 0;
	}
}
