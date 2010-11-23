import models.Answer;
import models.Comment;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;


public class LikeCommentTest extends UnitTest{
	
	@Before
	public void setUp() {
		Fixtures.deleteAll();
	}
	
	@Test
	public void shouldOnlyBePossibleToLikeCommentOncePerUser() {
		User alice = new User("alice", "a@b.com", "pw");
		Question question = new Question(alice, "TEST?", "test question");
		Comment comment = new Comment(alice, question, "This is a comment to like!");
		User bob = new User("bob", "b@b.com", "pw");
		comment.like(bob);
		assertEquals(1, comment.fans.size());
		comment.dislike(bob);
		assertEquals(0, comment.fans.size());
	}
	
	@Test
	public void shouldNotBePossibleToLikeOwnComment() {
		User alice = new User("alice", "a@b.com", "pw");
		Question question = new Question(alice, "TEST?", "test question");
		Comment comment = new Comment(alice, question, "This is a comment to like!");
		comment.like(alice);
		assertEquals(0, comment.fans.size());
		comment.dislike(alice);
		assertEquals(0, comment.fans.size());
	}
}
