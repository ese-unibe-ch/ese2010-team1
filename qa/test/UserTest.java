import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class UserTest extends UnitTest {

	@Before
	public void setup() {

	}

	@Test
	public void shouldCreateUser() {
		User user = new User("Jack", "test@mail.com", "password").save();
		assertTrue(user != null);
		assertEquals(user, User.findById(user.id));
	}

	@Test
	public void shouldBeCalledJack() {
		User user = new User("Jack", "test@mail.com", "password");
		assertEquals(user.name(), "Jack");
	}

	@Test
	public void shouldHaveAPassword() {
		User user = new User("Jack", "test@mail.com", "password");
		assertEquals("password", user.password());

	}

}
