import models.Answer;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class VoteTest extends UnitTest {

	private Question question;
	private Answer answer;
	private User bill;

	@Before
	public void setUp() {
		this.question = new Question(new User("Jack", "test@mail.com",
				"password"), "Why did the chicken cross the road?");
		this.answer = question.answer(new User("James", "test@mail.com",
				"password"), "To get to the other side.");
		this.bill = new User("Bill", "test@mail.com", "password");
	}

	@Test
	public void shoulHaveNoVotes() {
		assertEquals(this.question.upVotes(), 0);
		assertEquals(this.question.downVotes(), 0);
		assertEquals(this.answer.upVotes(), 0);
		assertEquals(this.answer.downVotes(), 0);
	}

	@Test
	public void shouldVoteUp() {
		this.question.voteUp(bill);
		this.answer.voteUp(bill);
		assertEquals(this.question.upVotes(), 1);
		assertEquals(this.question.downVotes(), 0);
		assertEquals(this.answer.upVotes(), 1);
		assertEquals(this.answer.downVotes(), 0);
	}

	@Test
	public void shouldVoteDown() {
		this.question.voteDown(bill);
		this.answer.voteDown(bill);
		assertEquals(this.question.upVotes(), 0);
		assertEquals(this.question.downVotes(), 1);
		assertEquals(this.answer.upVotes(), 0);
		assertEquals(this.answer.downVotes(), 1);
	}

	@Test
	public void shouldCount() {
		for (int i = 0; i < 11; i++) {
			this.answer.voteUp(new User("up" + i, "test@mail.com", "password"));
		}
		for (int i = 0; i < 42; i++) {
			this.answer.voteDown(new User("down" + i, "test@mail.com",
					"password"));
		}
		assertEquals(this.answer.upVotes(), 11);
		assertEquals(this.answer.downVotes(), 42);
	}

	@Test
	public void shouldDeleteOldVote() {
		this.question.voteDown(bill);
		this.question.voteUp(bill);
		this.question.voteUp(bill);
		assertEquals(this.question.upVotes(), 1);
		assertEquals(this.question.downVotes(), 0);
	}

}
