package model.fraudpointscale;

import models.MajorEntry;

public class TestRule extends FraudPointRule {

	@Override
	public void check(MajorEntry entry) {
		if (entry.id == 1) {
			FraudPointController.addPoint(new FraudPoint());
		}
	}

}
