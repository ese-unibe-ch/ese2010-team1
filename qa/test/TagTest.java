import models.Question;
import models.Tag;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class TagTest extends UnitTest {

	@Before
	public void setUp() {
		Fixtures.deleteAll();
	}

	@Test
	public void testTags() {
		User user = new User("Kraut", "kraut@salat.ch", "password").save();
		Question question = user.addQuestion("A title", "My first question");
		Question question2 = user.addQuestion("A title2", "My second question");

		assertEquals(0, Question.findTaggedWith("1").size());

		question.tagItWith("1").tagItWith("2").save();
		question2.tagItWith("1").tagItWith("3").save();

		assertEquals(2, question.findTaggedWith("1").size());
		assertEquals(1, question.findTaggedWith("2").size());
		assertEquals(1, question.findTaggedWith("3").size());

		assertNotNull(Tag.find("byName", "%1%").fetch());

	}

}
