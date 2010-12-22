import java.util.Date;

import models.Question;
import models.User;
import models.fraudpointscale.FraudPoint;
import models.fraudpointscale.MinimalContentLengthRule;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class MinimalContentLengthTest extends UnitTest {

	@Before
	public void setup() {

		Fixtures.deleteAll();
	}

	@Test
	public void testJustAnotherRule() {

		Date timebefore = new Date();
		User user = new User("bla", "bla@bla.com", "bla").save();
		User user2 = new User("blub", "blub@blub.com", "blub").save();

		Question question = user.addQuestion("blub", "blub");
		question.answer(user2, "john");

		MinimalContentLengthRule rule = new MinimalContentLengthRule();

		rule.checkSince(timebefore);

		assertEquals(2, FraudPoint.count());

	}

}
