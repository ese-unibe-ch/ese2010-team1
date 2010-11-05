import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class NotificationTest extends UnitTest {

	@Before
	public void setup() {

		Fixtures.deleteAll();

	}

	@Test
	public void setNotification() {

		User user = new User("user1", "user1@test.com", "test").save();
		User user2 = new User("tes", "test@test.com", "test").save();

		user.addQuestion("test title", "content");

	}
}
