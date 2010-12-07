package model.fraudpointscale;

import java.util.Date;

import models.User;
import play.db.jpa.Model;

public class FraudPoint extends Model {
	public User user;
	public Date timestamp;
}
