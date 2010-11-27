import models.Comment;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class LikeCommentTest extends UnitTest {

	@Before
	public void setUp() {
		Fixtures.deleteAll();
	}

	@Test
	public void shouldOnlyBePossibleToLikeCommentOncePerUser() {
		User alice = new User("alice", "a@b.com", "pw").save();
		Question question = new Question(alice, "TEST?", "test question")
				.save();
		Comment comment = new Comment(alice, question,
				"This is a comment to like!").save();
		User bob = new User("bob", "b@b.com", "pw").save();
		comment.like(bob);
		assertEquals(1, comment.fans.size());
		comment.unlike(bob);
		assertEquals(0, comment.fans.size());
	}

	@Test
	public void shouldNotBePossibleToLikeOwnComment() {
		User alice = new User("alice", "a@b.com", "pw").save();
		Question question = new Question(alice, "TEST?", "test question")
				.save();
		Comment comment = new Comment(alice, question,
				"This is a comment to like!").save();
		comment.like(alice);
		assertEquals(0, comment.fans.size());
		comment.unlike(alice);
		assertEquals(0, comment.fans.size());
	}
}
