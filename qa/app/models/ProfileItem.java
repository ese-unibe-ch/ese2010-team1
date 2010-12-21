package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

/**
 * The Class ProfileItem is responsible for all the items you can add on a user
 * profile.
 */
@Entity
public class ProfileItem extends Model {

	/** The title. */
	public String title;

	/** The entrys. */
	@OneToMany(mappedBy = "item", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<ProfileEntry> entrys;

	/**
	 * Instantiates a new profile item.
	 * 
	 * @param title
	 *            the title
	 */
	public ProfileItem(String title) {
		this.title = title;
		this.entrys = new ArrayList<ProfileEntry>();

	}

	/**
	 * Find user entry which belongs to the called item.
	 * 
	 * @param user
	 *            the user
	 * @return the profile entry
	 */
	public ProfileEntry findUserEntry(User user) {
		ProfileEntry entry = ProfileEntry.find("byItemAndUser", this, user)
				.first();
		return entry;

	}

	/**
	 * Checks for user entry.
	 * 
	 * @param user
	 *            the user
	 * @return true, if successful
	 */
	public boolean hasUserEntry(User user) {
		return findUserEntry(user) != null;
	}

	/**
	 * Edits the user entry which belongs to item.
	 * 
	 * @param user
	 *            the user the entry belongs to
	 * @param entry
	 *            the new entry
	 * @return the profile item
	 */
	public ProfileItem editUserEntry(User user, String entry) {

		if (!entry.equals("")) {
			if (!this.hasUserEntry(user)) {
				ProfileEntry pentry = new ProfileEntry(this, entry, user)
						.save();
				this.entrys.add(pentry);
			} else {
				ProfileEntry userEntry = findUserEntry(user);
				userEntry.entry = entry;
				userEntry.save();
			}
		} else if (hasUserEntry(user)) {
			findUserEntry(user).delete();
			entrys.remove(findUserEntry(user));
		}

		this.save();
		return this;
	}
}
