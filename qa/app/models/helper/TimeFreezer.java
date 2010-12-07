package models.helper;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

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
		this.timestamp = new Date();
	}

	/**
	 * Frozen.
	 * 
	 * @return true, if successful
	 */
	public boolean frozen() {
		return this.timestamp.getTime() + 1000 * this.delay < new Date()
				.getTime();
	}

}
