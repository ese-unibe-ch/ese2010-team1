package models.fraudpointscale;

import java.util.Date;

import models.User;

// TODO: Auto-generated Javadoc
/*
 * Note to team members:
 * 
 * Extend this class to add a new rule. To add fraud points call the addPoint(..) method.
 * To register your new rule run the bash script in /qa/conf/service-config.sh
 * 
 * You can take a look at TestRule and FraudPointScaleTest.testRule() for inspiration.
 * 
 */

/**
 * The Class FraudPointRule.
 */
public abstract class FraudPointRule {
	
	/**
	 * Check since.
	 *
	 * @param lastCheck the last check
	 */
	public abstract void checkSince(Date lastCheck);

	/**
	 * Adds the point.
	 *
	 * @param user the user
	 */
	protected void addPoint(User user) {
		new FraudPoint(user, this.getClass()).save();
	}
}
