package models.fraudpointscale;

import java.util.Date;

import models.User;

/**
 * The Class FraudPointRule.
 */
public abstract class FraudPointRule {

	/**
	 * Check since.
	 * 
	 * @param lastCheck
	 *            the last check
	 */
	public abstract void checkSince(Date lastCheck);

	/**
	 * Adds the point.
	 * 
	 * @param user
	 *            the user
	 */
	protected void addPoint(User user) {
		new FraudPoint(user, this.getClass()).save();
	}

	public abstract String description();
}
