package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

/**
 * The Class ProfileEntry declares the Database model for all profile entrys.
 */
@Entity
public class ProfileEntry extends Model {

	@Lob
	public String entry;

	@ManyToOne
	public ProfileItem item;

	public User user;

	/**
	 * Instantiates a new profile entry.
	 * 
	 * @param item
	 *            the item
	 * @param entry
	 *            the entry
	 * @param user
	 *            the user
	 */
	public ProfileEntry(ProfileItem item, String entry, User user) {

		this.item = item;
		this.entry = entry;
		this.user = user;

	}

}
