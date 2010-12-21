package controllers;

import models.User;
import play.mvc.Mailer;

public class Mails extends Mailer {

	public static void activationMail(User user) {

		setSubject("Welcome %s", user.name);
		addRecipient(user.email);
		setFrom("Knowledge Base <qa@qa.local>");

		send(user);

	}

	public static void deactivationMail(User user, String deactivationReason) {

		setSubject("Hello %s your account has been deactivated by an admin",
				user.name);
		addRecipient(user.email);
		setFrom("Knowledge Base <qa@qa.local>");
		send(user, deactivationReason);

	}

}
