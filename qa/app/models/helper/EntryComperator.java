package models.helper;

import java.util.Comparator;

import models.Answer;
import models.Entry;

public class EntryComperator implements Comparator<Entry> {

	/**
	 * Compares two entries by its rating, best answer is always higher than the
	 * rating.
	 * 
	 * @param o1
	 *            first entry
	 * 
	 * @param second
	 *            entry
	 * 
	 * @return 1 if o2 is better, -1 if o1 is better, 0 if they are equal.
	 */
	public int compare(Entry o1, Entry o2) {
		if (o1 instanceof Answer && ((Answer) o1).isBestAnswer()) {
			return -1;
		} else if (o2 instanceof Answer && ((Answer) o2).isBestAnswer()) {
			return 1;
		} else if (o2.rating() - o1.rating() > 0) {
			return 1;
		} else if (o2.rating() - o1.rating() < 0) {
			return -1;
		}
		return 0;
	}
}
