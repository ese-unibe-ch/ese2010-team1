package models.fraudpointscale;

import java.util.Date;

import models.User;

abstract class FraudPointRule {
	public abstract void checkSince(Date lastCheck);

	protected void addPoint(User user) {
		new FraudPoint(user, this.getClass()).save();
	}
}
