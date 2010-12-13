import models.User;

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

		User user = new User("User", "bla@bla.com", "bla").save();

	}

}
