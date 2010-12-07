package model.fraudpointscale;

import models.MajorEntry;

public class TestRule implements FraudPointRule {

	public void check(MajorEntry entry) {
		FraudPointController.addPoint(new FraudPoint());
	}

}
