package models.helper;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * The TimeFreezer is used to set time limits on votes and bestAnswers.
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
	 * Frozen, if an action isn't changeable.
	 * 
	 * @return true, if successful
	 */
	public boolean frozen() {
		return this.timestamp.getTime() + 1000 * this.delay < new Date()
				.getTime();
	}

}
