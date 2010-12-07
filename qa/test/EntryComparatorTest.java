import models.Answer;
import models.EntryComperator;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class EntryComparatorTest extends UnitTest {

	@Before
	public void setUp() {
		Fixtures.deleteAll();
	}

	@Test
	public void shouldCompareEntriesWhileFirstIsBest() {

		User user = new User("Jack", "test@mail.com", "password").save();
		User user2 = new User("Bob", "test2@mail.com", "password2").save();
		User user3 = new User("Fritz", "test3@mail.com", "password3").save();
		Question question = user.addQuestion("title", "a test");
		Answer answer = question.answer(user2, "dummy content");
		Answer answer2 = question.answer(user3, "dummy content2");
		question.setBestAnswer(answer);
		EntryComperator comperator = new EntryComperator();
		assertEquals(-1, comperator.compare(answer, answer2));
	}

	@Test
	public void shouldCompareEntriesWhileSecondIsBest() {

		User user = new User("Jack", "test@mail.com", "password").save();
		User user2 = new User("Bob", "test2@mail.com", "password2").save();
		User user3 = new User("Fritz", "test3@mail.com", "password3").save();
		Question question = user.addQuestion("title", "a test");
		Answer answer = question.answer(user2, "dummy content");
		Answer answer2 = question.answer(user3, "dummy content2");
		question.setBestAnswer(answer);
		EntryComperator comperator = new EntryComperator();
		assertEquals(1, comperator.compare(answer2, answer));
	}

	@Test
	public void shouldCompareEntriesWhileRatingDecides() {

		User user = new User("Jack", "test@mail.com", "password").save();
		User user2 = new User("Bob", "test2@mail.com", "password2").save();
		User user3 = new User("Fritz", "test3@mail.com", "password3").save();
		Question question = user.addQuestion("title", "a test");
		Answer answer = question.answer(user2, "dummy content");
		Answer answer2 = question.answer(user3, "dummy content2");
		answer.voteUp(user3);
		EntryComperator comperator = new EntryComperator();
		assertEquals(-1, comperator.compare(answer, answer2));
		answer2.voteUp(user2);
		answer2.voteUp(user);
		System.out.println(answer.rating());
		assertEquals(1, comperator.compare(answer, answer2));

	}

	@Test
	public void shouldCompareEntriesWhileEqual() {

		User user = new User("Jack", "test@mail.com", "password").save();
		User user2 = new User("Bob", "test2@mail.com", "password2").save();
		User user3 = new User("Fritz", "test3@mail.com", "password3").save();
		Question question = user.addQuestion("title", "a test");
		Answer answer = question.answer(user2, "dummy content");
		Answer answer2 = question.answer(user3, "dummy content2");
		answer.voteDown(user);
		answer2.voteDown(user2);
		EntryComperator comperator = new EntryComperator();
		assertEquals(0, comperator.compare(answer2, answer));
	}

}
