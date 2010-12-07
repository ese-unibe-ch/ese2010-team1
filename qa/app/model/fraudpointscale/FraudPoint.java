package model.fraudpointscale;

import java.util.Date;

import javax.persistence.ManyToOne;

import models.User;
import play.db.jpa.Model;

public class FraudPoint extends Model {
	@ManyToOne
	public User user;

	public Date timestamp;

	@ManyToOne
	public FraudPointRule rule;
}
