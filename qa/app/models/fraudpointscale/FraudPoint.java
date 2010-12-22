package models.fraudpointscale;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import models.User;
import play.db.jpa.Model;

/**
 * The Class FraudPoint saves informations about user fraud tries and the rule
 * he broke.
 */
@Entity
public class FraudPoint extends Model {

	/**
	 * Instantiates a new fraud point.
	 * 
	 * @param user
	 *            the user
	 * @param rule
	 *            the rule
	 */
	public FraudPoint(User user, Class<? extends FraudPointRule> rule) {
		this.user = user;
		this.rule = rule;
		this.timestamp = new Date();
		user.fraudPoints++;
		user.save();
	}

	/** The user. */
	@ManyToOne
	public User user;

	/** The rule. */
	public Class<? extends FraudPointRule> rule;

	/** The timestamp. */
	public Date timestamp;

}
