import models.User;
import models.fraudpointscale.FraudPoint;
import models.fraudpointscale.FraudPointController;
import models.fraudpointscale.ImplausibleFrequenceyRule;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class ImplausibleFrequencyTest extends UnitTest {
	private FraudPointController controller;

	@Before
	public void setup() {
		Fixtures.deleteAll();
		controller = FraudPointController.getInstance();
	}

	@Test
	public void harmlessUser() {
		User user1 = new User("User", "bla@bla.com", "bla").save();
		User user2 = new User("User2", "check@check.com", "bla").save();
		User user3 = new User("User3", "check@blub.com", "bla").save();

		user1.addQuestion("test", "blub");
		for (int i = 0; i < 10; i++) {
			user2.addQuestion("test", "blub");
		}
		for (int i = 0; i < 100; i++) {
			user3.addQuestion("test", "blub");
		}

		controller.run();
		assertEquals(22, FraudPoint.count("rule = ?",
				ImplausibleFrequenceyRule.class));
	}
}
