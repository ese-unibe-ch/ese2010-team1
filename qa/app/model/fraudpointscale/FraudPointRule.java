package model.fraudpointscale;

import javax.persistence.Entity;

import models.MajorEntry;
import play.db.jpa.Model;

@Entity
abstract class FraudPointRule extends Model {
	abstract void check(MajorEntry entry);
}
