import java.util.Date;

import models.fraudpointscale.FraudPoint;
import models.fraudpointscale.FraudPointController;
import models.fraudpointscale.TestRule;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class FraudPointScaleTest extends UnitTest {
	private FraudPointController controller;

	@Before
	public void setUp() {
		Fixtures.deleteAll();
		controller = FraudPointController.getInstance();
	}

	@Test
	public void testRule() {
		controller.run();
		assertEquals(1, FraudPoint.count());
		FraudPoint point = (FraudPoint) FraudPoint.all().first();
		assertEquals(TestRule.class, point.rule);
		assertFalse(point.timestamp.after(new Date()));
	}

}
