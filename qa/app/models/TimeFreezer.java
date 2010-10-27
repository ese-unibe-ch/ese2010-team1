package models;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

// TODO: Auto-generated Javadoc

/**
 * The TimeFreezer.
 */
@Entity
public class TimeFreezer extends Model {

	public Date timestamp;

	public int delay;

	/**
	 * Instantiates a new time freezer.
	 * 
	 * @param owner
	 *            the owner
	 * @param delay
	 *            the delay
	 */
	public TimeFreezer(int delay) {
		this.delay = delay;
		this.timestamp = now();
	}

	/**
	 * Frozen.
	 * 
	 * @return true, if successful
	 */
	public boolean frozen() {
		return this.timestamp.getTime() + 1000 * this.delay < now().getTime();
	}

	/**
	 * Get current date.
	 * 
	 * @return the date
	 */
	private static Date now() {
		return new Date();
	}
}
