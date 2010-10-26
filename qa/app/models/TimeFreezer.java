package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import play.db.jpa.Model;

// TODO: Auto-generated Javadoc
// SM Needs to be Generic

/**
 * The TimeFreezer.
 */
@Entity
public class TimeFreezer extends Model {

	@OneToOne
	public Vote owner;

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
	public TimeFreezer(Vote owner, int delay) {
		if (owner != null)
			this.owner = owner.save();
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
