package models.helper;

import java.util.ArrayList;
import java.util.List;

import models.MajorEntry;
import models.User;

/**
 * The Class UserEntryFrequency represents a set of entries belonging to a user.
 */
public class UserEntryFrequency {

	private static final int minEntries = 2;

	private static final int normalDelay = 20;
	private static final int allowedNormal = 5;

	private static final int shortDelay = 1;
	private static final int allowedShort = 3;

	private static final int maxEntries = 20;

	private User user;
	private List<MajorEntry> entries;

	/**
	 * Instantiates a new user entry frequency.
	 * 
	 * @param entry
	 *            the first entry
	 */
	public UserEntryFrequency(MajorEntry entry) {
		user = entry.owner;
		entries = new ArrayList();
		add(entry);
	}

	/**
	 * Adds a new entry to the set.
	 * 
	 * @param entry
	 *            the entry
	 */
	public void add(MajorEntry entry) {
		entries.add(entry);
	}

	public User user() {
		return user;
	}

	/**
	 * Checks if the set of entries is suspicious.
	 * 
	 * @return true, if suspicious
	 */
	public int suspicious() {
		if (entries.size() < minEntries)
			return 0;
		if (entries.size() > maxEntries)
			return 20;

		int shortCount = 0;
		int normalCount = 0;

		for (int i = 0; i < entries.size() - 1; i++) {
			long delay = entries.get(i + 1).timestamp.getTime()
					- entries.get(i).timestamp.getTime();
			if (delay < normalDelay * 1000) {
				normalCount++;
				if (normalCount > allowedNormal)
					return 1;
				if (delay < shortDelay * 1000) {
					shortCount++;
					if (shortCount > allowedShort)
						return 2;
				}
			}
		}

		return 0;
	}
}
