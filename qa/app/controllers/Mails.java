package controllers;

import models.User;
import play.mvc.Mailer;

/**
 * The Class Mails.
 */
public class Mails extends Mailer {

	/**
	 * Activation mail.
	 * 
	 * @param user
	 *            the user
	 */
	public static void activationMail(User user) {

		setSubject("Welcome %s", user.name);
		addRecipient(user.email);
		setFrom("Knowledge Base <ese-team1@iam.unibe.ch>");

		send(user);

	}

	/**
	 * Deactivation mail.
	 * 
	 * @param user
	 *            the user
	 * @param deactivationReason
	 *            the deactivation reason
	 */
	public static void deactivationMail(User user, String deactivationReason) {

		setSubject("Hello %s your account has been deactivated by an admin",
				user.name);
		addRecipient(user.email);
		setFrom("Knowledge Base <ese-team1@iam.unibe.ch>");
		send(user, deactivationReason);

	}

	/**
	 * Reactivation mail.
	 * 
	 * @param user
	 *            the user
	 * @param deactivationReason
	 *            the deactivation reason
	 */
	public static void reactivationMail(User user, String deactivationReason) {
		setSubject("Hello %s your account has been reactivated by an admin",
				user.name);
		addRecipient(user.email);
		setFrom("Knowledge Base <ese-team1@iam.unibe.ch>");
		send(user, deactivationReason);

	}

}
