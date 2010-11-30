import java.util.List;

import models.Question;
import models.Tag;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class QuestionTest extends UnitTest {

	@Before
	public void setUp() {
		Fixtures.deleteAll();
		new User("Anonym", "anonymous@example.com", "itsasecret").save();
	}

	@Test
	public void shouldCreateQuestion() {

		User user = new User("Jack", "test@mail.com", "password").save();
		Question question = user.addQuestion("A title", "My first question");

		// check question attributes
		assertEquals("A title", question.title);
		assertEquals("My first question", question.content);
		assertNotNull(question.timestamp);

		// check number of questions
		assertEquals(1, Question.count());
		user.addQuestion("Second title", "My second question");
		assertEquals(2, Question.count());

		// check search by owner
		List<Question> questions = Question.find("byOwner", user).fetch();
		assertEquals(2, questions.size());
		assertNotNull(Question.questions());
		assertEquals(2, Question.questions().size());

		// check answers
		assertNotNull(question.answers());
		assertEquals(0, question.answers().size());
	}

	@Test
	public void deleteQuestion() {

		User user = new User("Jack", "test@mail.com", "password").save();
		user.addQuestion("A title", "My first question");
		user.addQuestion("Second title", "My second question");

		List<Question> questions = Question.find("byOwner", user).fetch();

		Question question = questions.get(0);
		question.delete();

		questions = Question.find("byOwner", user).fetch();

		assertEquals(1, questions.size());

	}

	@Test
	public void deleteUser() {

		User user = new User("Jack", "test@mail.com", "password").save();
		user.addQuestion("A title", "My first question");
		user.addQuestion("Second title", "My second question");

		assertEquals(2, User.count());
		assertEquals(2, Question.count());
		user.delete();

		assertEquals(1, User.count());
		assertEquals(2, Question.count());

	}

	@Test
	public void tagQuestions() {

		User user = new User("Jack", "test@mail.com", "password").save();
		Question question = user.addQuestion("A title", "My first question");
		question.tagItWith("Hello").tagItWith("World");
		assertEquals(2, Tag.count());
		assertEquals("Hello, World", question.tagsToString());

		assertEquals(2, question.tags.size());
		question.removeAllTags();
		assertEquals(0, question.tags.size());

	}

}
