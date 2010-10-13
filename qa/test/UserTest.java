import models.User;

import org.junit.Test;

import play.test.UnitTest;

public class UserTest extends UnitTest {

	@Test
	public void shouldCreateUser() {
		User user = new User("Jack", "test@mail.com", "password");
		assertTrue(user != null);
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
