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

	public String title;

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
	 * Adds a new entry to the item.
	 * 
	 * @param entry
	 *            the entry
	 * @return the profile item
	 */
	private ProfileItem addEntry(ProfileEntry entry) {
		entrys.add(entry);
		this.save();
		return this;
	}

	/**
	 * Find user entry which belongs to the called item.
	 * 
	 * @param user
	 *            the user
	 * @return the profile entry
	 */
	public ProfileEntry findUserEntry(User user) {

		return ProfileEntry.find("byItemAndUser", this, user).first();

	}

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

		if (!hasUserEntry(user)) {
			ProfileEntry pentry = new ProfileEntry(this, entry, user).save();
			this.addEntry(pentry);
		} else {
			ProfileEntry userEntry = findUserEntry(user);
			userEntry.entry = entry;
			userEntry.save();
		}

		this.save();
		return this;
	}
}
