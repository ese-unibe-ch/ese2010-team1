package models.fraudpointscale;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import models.User;
import play.db.jpa.Model;

@Entity
public class FraudPoint extends Model {
	public FraudPoint(User user, Class<? extends FraudPointRule> rule) {
		this.user = user;
		this.rule = rule;
		this.timestamp = new Date();
	}

	@ManyToOne
	public User user;

	public Class<? extends FraudPointRule> rule;

	public Date timestamp;

}
