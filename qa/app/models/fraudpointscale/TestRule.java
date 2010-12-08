package models.fraudpointscale;

import java.util.Date;

public class TestRule extends FraudPointRule {

	public void checkSince(Date lastCheck) {
		addPoint(null);
	}

}
