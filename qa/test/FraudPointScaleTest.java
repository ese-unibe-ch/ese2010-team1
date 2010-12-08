import models.Question;
import models.User;
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
		User user = new User("Jack", "test@mail.com", "password").save();
		Question question = user.addQuestion("A title", "My first question");

		controller.run();

		assertEquals(1, FraudPoint.count());
	}

}
