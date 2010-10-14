import models.Answer;
import models.Question;
import models.User;
import models.Vote;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class VoteTest extends UnitTest {

	@Before
	public void setUp() {
		Fixtures.deleteAll();
	}

	@Test
	public void voteQuestion() {

		User user = new User("Jack", "test@mail.com", "password").save();
		User user2 = new User("John", "john@mail.com", "password").save();
		Question question = user.addQuestion("A title", "My first question");

		assertEquals(0, Vote.count());
		question.voteUp(user2);
		assertEquals(1, Vote.count());
		assertEquals(1, question.rating());
		assertEquals(1, question.numberOfVotes());

	}

	@Test
	public void voteAnswer() {

		User user = new User("Jack", "test@mail.com", "password").save();
		User user2 = new User("John", "john@mail.com", "password").save();
		Question question = user.addQuestion("A title", "My first question");

		Answer answer = question.answer(user2, "my answer");
		assertEquals(0, Vote.count());

		answer.voteDown(user);
		assertEquals(1, Vote.count());
		assertEquals(-1, answer.rating());

	}

	@Test
	public void voteQuestionAndAnswer() {

	}

}
