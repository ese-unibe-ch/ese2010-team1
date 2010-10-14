import java.util.List;

import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class UserTest extends UnitTest {

	@Before
	public void setup() {
		Fixtures.deleteAll();
	}

	@Test
	public void createUser() {
		User user = new User("Jack", "test@mail.com", "password").save();
		assertTrue(user != null);
		assertEquals(user, User.findById(user.id));
	}

	@Test
	public void shouldBeCalledJack() {
		new User("Jack", "test@mail.com", "password").save();
		List<User> user = User.find("byName", "Jack").fetch();
		assertEquals(1, user.size());
		assertEquals(user.get(0).name(), "Jack");
	}

	@Test
	public void shouldHaveAPassword() {
		new User("Jack", "test@mail.com", "password").save();
		List<User> user = User.find("byNameAndPassword", "Jack", "password")
				.fetch();
		assertEquals("password", user.get(0).password());

	}

	@Test
	public void testAuthenticateMethods() {
		User user = new User("Jack", "test@mail.com", "password").save();
		assertEquals(user, User.connect("Jack", "password"));
		assertEquals(null, User.connect("something", "noone"));
	}

	/*
	 * @Test public void testInitialData() {
	 * 
	 * Fixtures.load("data.yml");
	 * 
	 * User user = User.find("byName", "Bob").first(); assertNotNull(user);
	 * 
	 * }
	 */

}
