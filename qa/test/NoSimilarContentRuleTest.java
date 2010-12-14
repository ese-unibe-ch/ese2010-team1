import java.util.Date;

import models.Answer;
import models.Question;
import models.User;
import models.fraudpointscale.NoSimilarContentRule;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class NoSimilarContentRuleTest extends UnitTest {

	@Before
	public void setup() {

		Fixtures.deleteAll();
	}

	@Test
	public void findPotentialCheaters() {

		Date timeBefore = new Date();
		User user = new User("User", "bla@bla.com", "bla").save();
		User user2 = new User("User2", "check@check.com", "bla").save();
		User user3 = new User("User3", "check@blub.com", "bla").save();

		Question question = user.addQuestion("test", "fraudContent");
		Question question2 = user.addQuestion("test2", "fraudContent");
		Answer answer = question.answer(user, "fraudContent");

		NoSimilarContentRule rule = new NoSimilarContentRule();
		rule.checkSince(timeBefore);

		assertEquals(3, rule.findPotentialCheaters().size());

	}
}
