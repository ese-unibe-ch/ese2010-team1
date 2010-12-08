package models.fraudpointscale;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import models.User;
import play.db.jpa.Model;

@Entity
public class FraudPoint extends Model {
	public FraudPoint(User user) {
		this.user = user;
	}

	@ManyToOne
	public User user;

	public Date timestamp;

}
