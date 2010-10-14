import java.util.List;

import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class QuestionTest extends UnitTest {

	@Before
	public void setUp() {
		Fixtures.deleteAll();
	}

	@Test
	public void shouldCreateQuestion() {

		User user = new User("Jack", "test@mail.com", "password").save();
		user.addQuestion("A title", "My first question");
		assertEquals(1, Question.count());
		user.addQuestion("Second title", "My second question");
		assertEquals(2, Question.count());

		List<Question> questions = Question.find("byOwner", user).fetch();
		assertEquals(2, questions.size());
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

}
