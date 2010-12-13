import java.util.Date;

import models.Answer;
import models.Question;
import models.User;
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
	public void findEntriesWithNewVotes() {

		Date timeBefore = new Date();
		User user = new User("User", "bla@bla.com", "bla").save();
		User user2 = new User("User2", "check@check.com", "bla").save();
		User user3 = new User("User3", "check@blub.com", "bla").save();

		Question question = user.addQuestion("test", "blub");
		question.voteDown(user3);
		Answer answer = question.answer(user2, "bla");
		answer.voteDown(user3);

		VoteUpSameUserRule rule = new VoteUpSameUserRule();
		rule.checkSince(timeBefore);

		assertEquals(2, rule.findEntrysWithNewVotes().size());
		VoteUpSameUserRule rule2 = new VoteUpSameUserRule();
		rule2.checkSince(new Date());
		assertEquals(0, rule2.findEntrysWithNewVotes());

	}
}
