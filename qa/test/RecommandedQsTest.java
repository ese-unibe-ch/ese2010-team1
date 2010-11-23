import models.Question;
import models.RecommendedQuestions;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class RecommandedQsTest extends UnitTest {

	@Before
	public void setUp() {
		Fixtures.deleteAll();
		User user = new User("Default", "d@d.d", "").save();
		new Question(user, "test", "").save();
		new Question(user, "baum", "").save();
		new Question(user, "noch ein Test", "").save();
		new Question(user, "noch ein baum", "").save();
	}

	@Test
	public void shouldFindRecommandedQuestions() {

		RecommendedQuestions rq = new RecommendedQuestions();
		assertEquals(2, rq.getRelatedQuestions("test").size());
		assertEquals(2, rq.getRelatedQuestions("baum").size());
		assertTrue(rq.getRelatedQuestions("test").contains(
				(Question) Question.find("byTitle", "test").first()));
	}

}
