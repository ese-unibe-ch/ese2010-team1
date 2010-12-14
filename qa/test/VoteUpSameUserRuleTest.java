import java.util.Date;

import models.Answer;
import models.Question;
import models.User;
import models.fraudpointscale.FraudPoint;
import models.fraudpointscale.VoteUpSameUserRule;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class VoteUpSameUserRuleTest extends UnitTest {

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

		Question question = user.addQuestion("test", "blub");
		question.voteDown(user3);
		question.voteUp(user2);
		Answer answer = question.answer(user, "bla");
		answer.voteDown(user3);
		answer.voteUp(user2);

		VoteUpSameUserRule rule = new VoteUpSameUserRule();
		rule.checkSince(timeBefore);

		assertEquals(2, rule.findPotentialCheaters().size());
		assertEquals(2, FraudPoint.count());

	}
}
