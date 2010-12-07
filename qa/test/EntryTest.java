import java.util.List;

import models.Entry;
import models.Question;
import models.Search;
import models.User;
import models.Vote;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class EntryTest extends UnitTest {

	@Before
	public void setup() {
		Fixtures.deleteAll();
	}

	@Test
	public void searchEntry() {

		User user = new User("Jack", "test@mail.com", "password").save();
		user.addQuestion("A title", "My first question");
		user.addQuestion("Second title", "My second question");
		Question question = user.addQuestion("title", "a test");
		question.answer(user, "is that the right question?");

		List<Entry> searchResult = Search.searchContent("question");

		assertEquals(3, searchResult.size());

		List<Entry> searchResult2 = Search.searchContent("quest");
		assertEquals(3, searchResult2.size());

		List<Entry> searchResult3 = Search.searchContent("blub");
		assertEquals(0, searchResult3.size());

	}

	@Test
	public void shouldEditContent() {

		User user = new User("Jack", "test@mail.com", "password").save();
		Question question = user.addQuestion("title", "oldContent");
		question.edit("newContent", user);
		assertEquals("newContent", question.content);

	}

	@Test
	public void shouldRemoveVote() {

		User user = new User("Jack", "test@mail.com", "password").save();
		User user2 = new User("John", "john@mail.com", "password").save();
		Question question = user.addQuestion("A title", "My first question");
		assertEquals(0, Vote.count());
		Vote vote = question.voteDown(user2);
		assertEquals(1, Vote.count());
		question.removeVote(vote);
		assertEquals(0, Vote.count());

	}
}
