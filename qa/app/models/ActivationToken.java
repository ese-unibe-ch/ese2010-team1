package models;

import java.math.BigInteger;
import java.security.MessageDigest;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import play.db.jpa.Model;

@Entity
public class ActivationToken extends Model {

	@OneToOne
	public User user;

	public String activationToken;

	public ActivationToken(User user) {

		this.user = user;
		this.activationToken = encrypt(user.name + user.email + user.id
				+ System.currentTimeMillis());
	}

	private static String encrypt(String string) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(string.getBytes());
			return new BigInteger(1, md.digest(string.getBytes())).toString(16);
		} catch (Exception e) {
			return string;
		}
	}

}
