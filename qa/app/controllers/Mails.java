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

}
