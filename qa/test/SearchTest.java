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

}
