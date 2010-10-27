import java.util.List;

import models.Answer;
import models.Question;
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
		assertEquals(user.get(0).name, "Jack");
		assertEquals(user.get(0).email, "test@mail.com");
	}

	@Test
	public void shouldHaveAPassword() {
		new User("Jack", "test@mail.com", "password").save();
		assertTrue(User.connect("Jack", "password") != null);
		assertEquals(null, User.connect("Jack", "PaSsWoRd"));
	}

	@Test
	public void testAuthenticateMethods() {
		User user = new User("Jack", "test@mail.com", "password").save();
		assertEquals(user, User.connect("Jack", "password"));
		assertEquals(null, User.connect("something", "noone"));
	}

	@Test
	public void testExistsMethod() {
		new User("Jack", "test@mail.com", "password").save();
		assertTrue(User.exists("Jack"));
		assertFalse(User.exists("john"));

	}

	@Test
	public void deleteUser() {
		User user = new User("Jack", "test@mail.com", "password").save();
		assertEquals(1, User.count());
		user.delete();
		assertEquals(0, User.count());
	}

	@Test
	public void getNumberOfQuestions() {
		User user = new User("Jack", "test@mail.com", "password").save();
		Question question = user.addQuestion("A title", "My first question");
		Question question2 = user.addQuestion("A title2", "My first question2");
		assertEquals(user.getNumberOfQuestions(), 2);
		assertFalse(user.getNumberOfQuestions() == 3);
	}

	@Test
	public void getNumberOfVotes() {
		User user = new User("Jack", "test@mail.com", "password").save();
		User user2 = new User("John", "tes2t@mail.com", "password2").save();
		Question question = user2.addQuestion("A title", "My first question");
		question.voteUp(user);
		assertEquals(1, user.getNumberOfVotes());
	}

	@Test
	public void getNumberOfAnswers() {
		User user = new User("Jack", "test@mail.com", "password").save();
		Question question = user.addQuestion("A title", "My first question");
		Answer answer = question.answer(user, "an answer");
		assertEquals(1, user.getNumberOfAnswers());
	}
}
