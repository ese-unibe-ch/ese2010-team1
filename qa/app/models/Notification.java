package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Notification extends Model {

	/** The owner. */
	@ManyToOne
	public User owner;

	/** The entry. */
	@ManyToOne
	public Entry entry;

	/** The timestamp. */
	public Date timestamp;

	/** true if its a new notification. */
	public Boolean isNew;

	/** The activity. */
	public String activity;

	/**
	 * Instantiates a new notification.
	 * 
	 * @param owner
	 *            the owner
	 * @param entry
	 *            the entry
	 * @param activity
	 *            the activity
	 */
	public Notification(User owner, Entry entry, String activity) {

		this.owner = owner;
		this.entry = entry;
		this.timestamp = new Date();
		this.activity = activity;
		this.isNew = true;

	}

	/**
	 * Sets the notification isNew false.
	 * 
	 * @param id
	 *            id of the notification
	 */
	public static void hasBeenRed(long id) {
		Notification notification = Notification.findById(id);
		notification.isNew = false;
		notification.save();
	}
}
