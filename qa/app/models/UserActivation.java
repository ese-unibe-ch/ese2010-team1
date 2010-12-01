package models;

import java.math.BigInteger;
import java.security.MessageDigest;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class UserActivation extends Model {

	@ManyToOne
	public User user;

	public String activationToken;

	public UserActivation(User user) {

		this.user = user;
		this.activationToken = encrypt(user.name + user.email + user.id);
		System.out.println(this.activationToken);
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
