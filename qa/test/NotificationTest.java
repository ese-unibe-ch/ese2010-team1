import models.Notification;
import models.Question;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class NotificationTest extends UnitTest {

	@Before
	public void setup() {

		Fixtures.deleteAll();

	}

	@Test
	public void setNotification() {

		User user = new User("user1", "user1@test.com", "test").save();
		User user2 = new User("tes", "test@test.com", "test").save();

		Question question = user.addQuestion("test title", "content");
		question.answer(user2, "mycontent");

		assertTrue(user.hasNewNotifications());
		assertEquals(1, user.getNewNotifications().size());

		question.addComment(user2, "my comment");

		assertTrue(user.hasNewNotifications());
		assertEquals(2, user.getNewNotifications().size());

	}

	@Test
	public void readNotification() {

		User user = new User("user1", "user1@test.com", "test").save();
		User user2 = new User("tes", "test@test.com", "test").save();

		Question question = user.addQuestion("test title", "content");
		question.answer(user2, "mycontent");

		assertTrue(user.hasNewNotifications());
		assertEquals(1, user.getNewNotifications().size());

		question.addComment(user2, "my comment");

		assertTrue(user.hasNewNotifications());
		assertEquals(2, user.getNewNotifications().size());

		Notification notification = user.getNewNotifications().get(0);
		Notification.hasBeenRed(notification.id);

		assertEquals(1, user.getNewNotifications().size());

	}
}
