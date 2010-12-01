import models.User;
import models.UserActivation;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class UserActivationTest extends UnitTest {

	@Before
	public void setup() {

		Fixtures.deleteAll();
	}

	@Test
	public void generateUserActivation() {

		User user = new User("Test", "test@test.com", "test").save();
		UserActivation activationToken = new UserActivation(user);
		assertNotNull(activationToken);
		assertNotNull(activationToken.activationToken);

	}

}
