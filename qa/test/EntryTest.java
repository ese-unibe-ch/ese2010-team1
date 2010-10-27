import java.util.List;

import models.Entry;
import models.Question;
import models.User;

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

		List<Entry> searchResult = Entry.searchContent("question");

		assertEquals(3, searchResult.size());

		List<Entry> searchResult2 = Entry.searchContent("quest");
		assertEquals(3, searchResult2.size());

		List<Entry> searchResult3 = Entry.searchContent("blub");
		assertEquals(0, searchResult3.size());

	}
}
