import models.fraudpointscale.FraudPointController;

import org.junit.Before;

import play.test.Fixtures;
import play.test.UnitTest;

public class FraudPointScaleTest extends UnitTest {
	private FraudPointController controller;

	@Before
	public void setUp() {
		Fixtures.deleteAll();
		controller = FraudPointController.getInstance();
	}

}
