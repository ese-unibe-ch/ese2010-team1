import models.User;
import models.ActivationToken;

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
		ActivationToken activationToken = new ActivationToken(user);
		assertNotNull(activationToken);
		assertNotNull(activationToken.activationToken);

	}

}
