import models.User;
import models.helper.UserPair;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class UserPairTest extends UnitTest {

	@Before
	public void setup() {

		Fixtures.deleteAll();
	}

	@Test
	public void checkHashCode() {

		User user = new User("Bla", "blub@john.com", "bla").save();
		User user2 = new User("Bla2", "blub@blub.com", "bla").save();

		UserPair pair = new UserPair(user, user2);
		UserPair pair2 = new UserPair(user2, user);

		assertFalse(pair.hashCode() == pair2.hashCode());
	}

}
