import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import models.Answer;
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
		Set<Question> searchResults = Search.searchTaggedWith("test");
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

	@Test
	public void shouldReturnEntriesSortedByRating() {

		User user = new User("Jack", "test@mail.com", "password").save();
		User user2 = new User("Bob", "test2@mail.com", "password2").save();
		User user3 = new User("Fritz", "test3@mail.com", "password3").save();
		User user4 = new User("Helmut", "test4@mail.com", "password4").save();
		Question question1 = user.addQuestion("title", "dummy content");
		Question question2 = user.addQuestion("title2", "dummy content2");
		Question question3 = user.addQuestion("title3", "dummy content3");
		Question question4 = user.addQuestion("title4", "dummy content4");
		question1.answer(user2, "hackhack");
		question2.voteUp(user2);
		question3.voteUp(user2);
		question3.voteUp(user3);
		question4.voteUp(user2);
		question4.voteUp(user3);
		question4.voteUp(user4);
		List<Entry> listToSort = new ArrayList<Entry>();
		listToSort.add(question1);
		listToSort.add(question3);
		listToSort.add(question2);
		listToSort.add(question4);
		ArrayList<Entry> sortedList = (ArrayList<Entry>) Search
				.sortByRating(listToSort);
		assertEquals(sortedList.get(0), question4);
		assertEquals(sortedList.get(1), question3);
		assertEquals(sortedList.get(2), question2);
		assertEquals(sortedList.get(3), question1);

	}

	@Test
	public void shouldFindEntryByString() {

		User user = new User("Jack", "test@mail.com", "password").save();
		User user2 = new User("Bob", "test2@mail.com", "password2").save();
		User user3 = new User("Fritz", "test3@mail.com", "password3").save();
		User user4 = new User("Helmut", "test4@mail.com", "password4").save();
		Question question1 = user.addQuestion("Flower", "Rose");
		Question question2 = user.addQuestion("Craft", "Warfare");
		Question question3 = user.addQuestion("Car", "Audi");
		Question question4 = user.addQuestion("Rose", "Blossom");
		question1.tagItWith("Garden");
		Answer answer = question1.answer(user2, "Craft");
		question2.voteUp(user2);
		question3.voteUp(user2);
		question3.voteUp(user3);
		question4.voteUp(user2);
		question4.voteUp(user3);
		question4.voteUp(user4);
		assertEquals(4, Question.count());
		assertEquals(1, Search.searchTitle("Craft").size());
		assertEquals(Search.searchEntry("Craft").get(0), question2);
		assertEquals(Search.searchEntry("Craft").get(1), answer);
		assertEquals(Search.searchEntry("Rose").get(0), question4);
		assertEquals(Search.searchEntry("Rose").get(1), question1);
		assertEquals(Search.searchEntry("Garden").get(0), question1);

	}

}
