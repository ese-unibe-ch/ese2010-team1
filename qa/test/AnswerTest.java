import models.Answer;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class AnswerTest extends UnitTest {

	@Before
	public void setUp() {
		Fixtures.deleteAll();
	}

	@Test
	public void createAnswer() {
		User user = new User("Jack", "test@mail.com", "password").save();
		User user2 = new User("John", "john@mail.com", "password").save();
		Question question = user.addQuestion("A title", "My first question");
		assertEquals(1, Question.count());

		Answer answer = question.answer(user2, "an answer");
		assertEquals(1, Answer.count());
		question.answer(user, "another answer");

		assertEquals(answer.content(), "an answer");
		assertEquals(answer.question(), question);
	}

	@Test
	public void twoQuestionsWithAnswers() {

		User user = new User("Jack", "test@mail.com", "password").save();
		User user2 = new User("John", "john@mail.com", "password").save();
		Question question = user.addQuestion("A title", "My first question");
		Question question2 = user2.addQuestion("Another title",
				"Why i don't like to write tests?");
		assertEquals(2, Question.count());

		Answer answer = question.answer(user2, "an answer");
		assertEquals(1, Answer.count());
		Answer answer2 = question.answer(user, "another answer");

		assertEquals(answer.content(), "an answer");

		Answer answer3 = question2
				.answer(user2, "I also not really likt it...");

		assertEquals(3, Answer.count());
		Answer userAnswer = Answer.find("byOwner", user).first();

		assertEquals(userAnswer, answer2);

	}

	@Test
	public void deleteAnswer() {

	}

	@Test
	public void deleteQuestion() {

	}

	@Test
	public void deleteUser() {

	}

}
