import models.Answer;
import models.Question;
import models.User;
import models.Vote;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class UnregisterTest extends UnitTest {

	private User jack;
	private User john;
	private User bill;
	private User kate;
	private Question question;
	private Answer answer;
	private Vote questionVote;
	private Vote answerVote;

	@Before
	public void setUp() {
		this.jack = new User("Jack", "test@mail.com", "password");
		this.john = new User("John", "test@mail.com", "password");
		this.bill = new User("Bill", "test@mail.com", "password");
		this.kate = new User("Kate", "test@mail.com", "password");
		this.question = new Question(this.jack, "title",
				"Why did the chicken cross the road?");
		this.answer = this.question.answer(this.john,
				"To get to the other side.");
		this.questionVote = this.question.voteUp(this.kate);
		this.answerVote = this.answer.voteDown(this.bill);
	}

	@Test
	public void shouldUnregisterAnswer() {
		assertTrue(this.question.hasAnswer(this.answer));
		this.john.delete();
		assertFalse(this.question.hasAnswer(this.answer));
	}

	/*
	 * @Test public void shouldUnregisterAnswersToQuestion() {
	 * assertTrue(this.john.hasItem(this.answer)); this.jack.delete();
	 * assertFalse(this.john.hasItem(this.answer)); }
	 */

	@Test
	public void shouldUnregisterVotesOfUser() {
		assertEquals(this.question.upVotes(), 1);
		this.kate.delete();
		assertEquals(this.question.upVotes(), 0);

		assertEquals(this.answer.downVotes(), 1);
		this.bill.delete();
		assertEquals(this.answer.downVotes(), 0);
	}
	/*
	 * @Test public void shouldUnregisterVotesOfEntry() {
	 * assertTrue(this.kate.hasItem(this.questionVote));
	 * assertTrue(this.bill.hasItem(this.answerVote)); this.jack.delete();
	 * assertFalse(this.kate.hasItem(this.questionVote));
	 * assertFalse(this.bill.hasItem(this.answerVote));
	 * 
	 * }
	 */
}
