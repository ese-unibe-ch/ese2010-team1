import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;
import controllers.Mails;

public class MailsTest extends UnitTest {

	@Before
	public void setup() {

		Fixtures.deleteAll();
	}

	@Test
	public void sendMail() {

		User user = new User("Tobias", "schmid.tobias@bluewin.ch", "secret")
				.save();

		Mails mailer = new Mails();

		mailer.activationMail(user);

	}

}
