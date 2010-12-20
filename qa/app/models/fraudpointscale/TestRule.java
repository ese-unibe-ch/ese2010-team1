package models.fraudpointscale;

import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class TestRule.
 */
public class TestRule extends FraudPointRule {

	/* (non-Javadoc)
	 * @see models.fraudpointscale.FraudPointRule#checkSince(java.util.Date)
	 */
	public void checkSince(Date lastCheck) {
		addPoint(null);
	}

}
