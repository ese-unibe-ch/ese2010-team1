package model.fraudpointscale;

import models.MajorEntry;
import play.db.jpa.Model;

public abstract class FraudPointRule extends Model {
	public FraudPointRule() {
		this.save();
	}

	public abstract void check(MajorEntry entry);
}
