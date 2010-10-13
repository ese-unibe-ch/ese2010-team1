import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class QuestionTest extends UnitTest {
	private User user;
	private Question question;

	@Before
	public void setUp() {
		this.user = new User("Jack", "test@mail.com", "password");
		this.question = new Question(user, "title",
				"Why did the chicken cross the road?");
	}

	@Test
	public void shouldCreateQuestion() {
		assertTrue(question != null);
	}

	@Test
	public void shouldHaveOwner() {
		assertEquals(question.owner(), user);
	}

	@Test
	public void shouldHaveCorrectContent() {
		assertEquals(question.content(), "Why did the chicken cross the road?");
	}
	/*
	 * @Test public void shouldRegisterItself() {
	 * assertTrue(user.hasItem(question)); }
	 * 
	 * @Test public void shouldBeInPublicList() { List q = Question.questions();
	 * assertFalse(q == null); assertTrue(q.contains(this.question)); int size =
	 * Question.questions().size(); new Question(this.user, "title",
	 * "What is the answer to life the universe and everything?");
	 * assertEquals(size + 1, Question.questions().size());
	 * this.question.unregister(); assertEquals(size,
	 * Question.questions().size()); }
	 */
}
