import models.Answer;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class BestAnswerTest extends UnitTest {

	@Before
	public void setUp() {
		Fixtures.deleteAll();
	}

	@Test
	public void setBestAnswerSzenario() {

		User jack = new User("Jack", "test@mail.com", "password").save();
		User bill = new User("Bill", "bill@mail.com", "secret").save();
		Question question = jack.addQuestion("A title", "My first question");
		Answer firstAnswer = question.answer(jack, "first answer").save();
		Answer secondAnswer = question.answer(bill, "second answer").save();
		Answer thirdAnswer = question.answer(bill, "third answer").save();

		assertFalse(firstAnswer.isBestAnswer());
		assertFalse(secondAnswer.isBestAnswer());
		assertFalse(thirdAnswer.isBestAnswer());

		assertTrue(question.canSetBestAnswer());

		assertFalse(firstAnswer.canBeBestAnswer());
		assertTrue(secondAnswer.canBeBestAnswer());
		assertTrue(thirdAnswer.canBeBestAnswer());

		assertFalse(firstAnswer == null);
		assertFalse(secondAnswer == null);
		assertFalse(thirdAnswer == null);

		question.setBestAnswer(secondAnswer);

		assertFalse(firstAnswer.isBestAnswer());
		assertTrue(secondAnswer.isBestAnswer());
		assertFalse(thirdAnswer.isBestAnswer());

		assertTrue(question.canSetBestAnswer());
		question.resetBestAnswer();

		assertFalse(firstAnswer.isBestAnswer());
		assertFalse(secondAnswer.isBestAnswer());
		assertFalse(thirdAnswer.isBestAnswer());
	}

}
