import java.util.List;

import models.Entry;
import models.Question;
import models.Search;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class SearchTest extends UnitTest {

	@Before
	public void setup() {

		Fixtures.deleteAll();
	}

	@Test
	public void searchQuestionTitle() {

		User user = new User("test", "test@test.com", "test").save();
		new Question(user, "test title", "my content").save();

		List<Entry> searchResults = Search.searchTitle("test");
		assertEquals(1, searchResults.size());
		new Question(user, "testtiteldummytext", "my content").save();
		searchResults = Search.searchTitle("test");
		assertEquals(2, searchResults.size());

	}

	@Test
	public void searchQuestionByTags() {

		User user = new User("test", "test@test.com", "test").save();
		Question question = new Question(user, "test title", "my content")
				.save();
		question.tagItWith("anotherteststring").tagItWith("test").tagItWith(
				"hello").tagItWith("world");
		List<Question> searchResults = Search.searchTaggedWith("test");
		assertEquals(1, searchResults.size());
		searchResults = Search.searchTaggedWith("blub");
		assertEquals(0, searchResults.size());
	}

	@Test
	public void searchUserByNameOrEmail() {

		new User("blub", "test@test.com", "test").save();
		new User("test", "blub@blub.com", "blub").save();
		assertEquals(2, User.count());
		List<User> searchResults = Search.searchUsers("test");
		assertEquals(2, searchResults.size());

	}

}
