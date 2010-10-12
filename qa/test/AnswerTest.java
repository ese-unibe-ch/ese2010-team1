import java.util.Date;

import models.Answer;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class AnswerTest extends UnitTest {

	private User james;
	private Question question;
	private Answer answer;

	@Before
	public void setUp() {
		this.james = new User("James", "test@mail.com", "password");
		this.question = new Question(new User("Jack", "test@mail.com",
				"password"), "Why did the chicken cross the road?");
		this.answer = this.question.answer(james, "To get to the other side.");
	}

	@Test
	public void shouldCreateAnswer() {
		assertTrue(answer != null);
	}

	@Test
	public void shouldHaveCorrectContent() {
		assertEquals(answer.content(), "To get to the other side.");
	}

	@Test
	public void shouldHaveOwner() {
		assertEquals(this.answer.owner(), this.james);
	}

	@Test
	public void shouldHaveQuestion() {
		assertEquals(this.answer.question(), this.question);
	}

	@Test
	public void shouldHaveTimestamp() {
		assertTrue(answer.timestamp() != null);
		assertTrue(answer.timestamp().compareTo(new Date()) <= 0);
	}

	@Test
	public void shouldRegisterItself() {
		assertTrue(this.james.hasItem(this.answer));
		assertTrue(this.question.hasAnswer(this.answer));
	}

}
