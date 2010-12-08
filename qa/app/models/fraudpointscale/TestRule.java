package models.fraudpointscale;

public class TestRule implements FraudPointRule {

	public void check() {
		new FraudPoint(null).save();
	}

}
