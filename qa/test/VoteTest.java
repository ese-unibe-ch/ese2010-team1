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

		question.voteDown(user);
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

		User user = new User("Jack", "test@mail.com", "password").save();
		User user2 = new User("John", "john@mail.com", "password").save();
		Question question = user.addQuestion("A title", "My first question");

		assertEquals(0, Vote.count());

		question.voteDown(user2);

		assertEquals(1, Vote.count());
		assertEquals(-1, question.rating());
		Answer answer = question.answer(user2, "my answer");

		answer.voteUp(user);

		assertEquals(2, Vote.count());
		assertEquals(1, answer.rating());

	}

	@Test
	public void voteQuestionUpAndAfterwardsDown() {

		User user = new User("Jack", "test@mail.com", "password").save();
		User user2 = new User("John", "john@mail.com", "password").save();
		Question question = user.addQuestion("A title", "My first question");

		assertEquals(0, Vote.count());

		question.voteUp(user2);
		assertEquals(1, Vote.count());
		assertEquals(1, question.rating());
		assertEquals(1, question.numberOfVotes());

		question.voteDown(user2);
		assertEquals(1, Vote.count());
		assertEquals(-1, question.rating());
		assertEquals(1, question.numberOfVotes());

	}

	@Test
	public void deleteUserWithVotes() {

		User user = new User("Jack", "test@mail.com", "password").save();
		User user2 = new User("John", "john@mail.com", "password").save();
		Question question = user.addQuestion("A title", "My first question");

		assertEquals(0, Vote.count());

		question.voteDown(user2);

		assertEquals(1, Vote.count());
		assertEquals(-1, question.rating());
		Answer answer = question.answer(user2, "my answer");
		Answer answer2 = question.answer(user2, "my second answer");

		answer.voteUp(user);
		answer2.voteDown(user);

		assertEquals(3, Vote.count());
		assertEquals(1, answer.rating());
		assertEquals(-1, answer2.rating());

		user.delete();

		assertEquals(1, User.count());
		assertEquals(0, Vote.count());
		assertEquals(0, Answer.count());
		assertEquals(0, Question.count());

	}

	@Test
	public void deleteQuestionWithVotes() {

		User user = new User("Jack", "test@mail.com", "password").save();
		User user2 = new User("John", "john@mail.com", "password").save();
		Question question = user.addQuestion("A title", "My first question");

		assertEquals(0, Vote.count());

		question.voteDown(user2);

		assertEquals(1, Vote.count());
		assertEquals(-1, question.rating());
		Answer answer = question.answer(user2, "my answer");
		Answer answer2 = question.answer(user2, "my second answer");

		answer.voteUp(user);
		answer2.voteDown(user);

		assertEquals(3, Vote.count());
		assertEquals(1, answer.rating());
		assertEquals(-1, answer2.rating());

		question.delete();

		assertEquals(2, User.count());
		assertEquals(0, Vote.count());
		assertEquals(0, Answer.count());
		assertEquals(0, Question.count());

	}

	@Test
	public void deleteAnswerWithVotes() {

		User user = new User("Jack", "test@mail.com", "password").save();
		User user2 = new User("John", "john@mail.com", "password").save();
		Question question = user.addQuestion("A title", "My first question");

		assertEquals(0, Vote.count());

		question.voteDown(user2);

		assertEquals(1, Vote.count());
		assertEquals(-1, question.rating());
		Answer answer = question.answer(user2, "my answer");
		Answer answer2 = question.answer(user2, "my second answer");

		answer.voteUp(user);
		answer2.voteDown(user);

		assertEquals(3, Vote.count());
		assertEquals(1, answer.rating());
		assertEquals(-1, answer2.rating());

		answer.delete();

		assertEquals(2, User.count());
		assertEquals(2, Vote.count());
		assertEquals(1, Answer.count());
		assertEquals(1, Question.count());

	}

	@Test
	public void removeVote() {
		User user = new User("Jack", "test@mail.com", "password").save();
		User user2 = new User("John", "john@mail.com", "password").save();
		Question question = user.addQuestion("A title", "My first question");

		assertEquals(0, question.rating());
		assertFalse(question.alreadyVoted(user2, false));
		assertFalse(question.alreadyVoted(user2, false));

		Vote vote = question.voteUp(user2);

		assertEquals(1, question.rating());
		assertTrue(question.alreadyVoted(user2, true));
		assertFalse(question.alreadyVoted(user2, false));

		question.removeVote(vote);

		assertEquals(0, question.rating());
		assertFalse(question.alreadyVoted(user2, false));
		assertFalse(question.alreadyVoted(user2, false));
	}

}
