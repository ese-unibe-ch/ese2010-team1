package models;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import models.helper.Utils;
import play.db.jpa.Model;

/**
 * The Class ActivationToken contains an activation token for every new user.
 */
@Entity
public class ActivationToken extends Model {

	/** The user. */
	@OneToOne
	public User user;

	/** The activation token. */
	public String activationToken;

	/**
	 * Instantiates a new activation token and generates a token of user data.
	 * 
	 * @param user
	 *            the user
	 */
	public ActivationToken(User user) {

		this.user = user;
		this.activationToken = Utils.encryptStringToSHA1(user.name + user.email
				+ user.id + System.currentTimeMillis());
	}

}
