import java.util.Date;

import models.Answer;
import models.Comment;
import models.Question;
import models.User;
import models.fraudpointscale.FraudPoint;
import models.fraudpointscale.ReportRule;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class ReportTest extends UnitTest {

	User user1;
	User user2;
	Date date = new Date();

	@Before
	public void setUp() {
		Fixtures.deleteAll();
		user1 = new User("alice", "a@b.com", "pw").save();
		user2 = new User("beat", "a@b.com", "pw").save();
	}

	@Test
	public void shouldReportQuestion() {
		Question question = new Question(user1, "test", "test").save();
		question.report(user1);
		question.save();
		assertTrue(question.reports.isEmpty());
		question.report(user2);
		question.save();
		assertFalse(question.reports.isEmpty());
	}

	@Test
	public void shouldReportAnswer() {
		Question question = new Question(user1, "test", "test").save();
		Answer answer = new Answer(user2, question, "test").save();
		answer.report(user2);
		answer.save();
		assertTrue(answer.reports.isEmpty());
		answer.report(user1);
		answer.save();
		assertFalse(answer.reports.isEmpty());
	}

	@Test
	public void shouldReportComment() {
		Question question = new Question(user1, "test", "test").save();
		Answer answer = new Answer(user2, question, "test").save();
		Comment comment = new Comment(user1, answer, "test").save();
		comment.report(user1);
		comment.save();
		assertTrue(comment.reports.isEmpty());
		comment.report(user2);
		comment.save();
		assertFalse(comment.reports.isEmpty());
	}

	@Test
	public void shouldAddFraudPoints() {

		assertTrue(FraudPoint.find("byUser", user1).fetch().isEmpty());
		Question question = new Question(user1, "test", "test").save();
		question.report(user2);
		question.save();
		ReportRule rule = new ReportRule();
		rule.checkSince(date);
		assertEquals(1, FraudPoint.find("byUser", user1).fetch().size());
	}

}
