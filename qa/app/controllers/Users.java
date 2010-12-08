package controllers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import models.Answer;
import models.Comment;
import models.Entry;
import models.MajorEntry;
import models.ProfileItem;
import models.Question;
import models.User;
import play.data.validation.Email;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;

/**
 * The Class Users is responsible for all user actions.
 */
public class Users extends Controller {

	/**
	 * Sets the connected user.
	 */
	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.find("byName", Security.connected()).first();
			renderArgs.put("user", user);
		}
	}

	/*** VIEWS ***/

	/**
	 * Shows the user profile for the given user id.
	 * 
	 * @param id
	 *            the user id
	 */
	public static void profile(long id) {

		List<ProfileItem> titles = ProfileItem.findAll();
		User puser = User.findById(id);

		HashMap<String, String> info = new HashMap();
		for (ProfileItem title : titles) {
			if (title.hasUserEntry(puser))
				info.put(title.title, title.findUserEntry(puser).entry);
		}
		render(puser, titles, info);
	}

	public static void createUser() {

		render();
	}

	public static void addUser(
			@Required(message = "A valid username is required") String username,
			@Required(message = "A valid e-mail is required") @Email String email,
			@Required(message = "A password is required") String password,
			String password2) throws Throwable {

		// validate all parameters
		if (!password.isEmpty()) {
			validation.equals(password, password2).message(
					"Passwords don't match");
		}

		validation.isTrue(!User.exists(username)).message(
				"Username already exists");

		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			Users.createUser();
		}

		User user = new User(username, email, password).save();
		user.generateActivationToken();
		Mails mailer = new Mails();
		mailer.activationMail(user);

		render();
	}

	public static void activateUser(long id, String securityToken) {

		User auser = User.findById(id);
		String activationToken = auser.getActivationToken();
		if (activationToken != null && securityToken.equals(activationToken)) {
			auser.activate();
			flash.put("message", "Sucessfully activated");
			session.put("username", auser.name);
		} else {
			flash.put("message", "Activation went wrong!");
		}

		render(auser);
	}

	public static void saveProfile(long id, String[] profileEntry) {

		User user = User.findById(id);
		List<ProfileItem> titles = ProfileItem.findAll();

		Iterator<ProfileItem> it = titles.iterator();
		for (String newEntry : profileEntry) {

			ProfileItem pentry = it.next();
			pentry.editUserEntry(user, newEntry);

		}
		user.calcReputation();

		profile(id);

	}

	public static void get(long id, String theAction) {
		User user = User.findById(id);

		if (theAction.equals("Activities")) {

			render("Users/profileactivities.html", user);

		} else if (theAction.equals("Questions")) {

			render("Users/questions.html", user);

		} else if (theAction.equals("Answers")) {

			render("Users/answers.html", user);

		} else if (theAction.equals("Reputation")) {

			renderText("<div id=\"graph\"> <div id=\"graphcanvas\" style=\"width:600px;height:300px\"></div></div>");

		} else if (theAction.equals("Statistics")) {

			render("Users/statistics.html", user);

		}

	}

	public static void activities(long id) {

		User puser = User.findById(id);

		List<models.Notification> notifications = models.Notification.find(
				"byOwner", puser).fetch();

		render(notifications, puser);
	}

	public static void likeComment(long id) {
		User user = User.find("byName", Security.connected()).first();
		Comment comment = Comment.findById(id);
		comment.like(user);

		Question question = comment.entry instanceof Question ? (Question) comment.entry
				: ((Answer) comment.entry).question;

		Entry entry = comment.entry;
		if (entry instanceof models.Question)
			Questions.question(id);
		else
			Questions.question(((Answer) entry).question.id);

		Questions.question(question.id);
	}

	public static void unlikeComment(long id) {
		User user = User.find("byName", Security.connected()).first();
		Comment comment = Comment.findById(id);
		comment.unlike(user);

		Question question = comment.entry instanceof Question ? (Question) comment.entry
				: ((Answer) comment.entry).question;

		Entry entry = comment.entry;
		if (entry instanceof models.Question)
			Questions.question(id);
		else
			Questions.question(((Answer) entry).question.id);

		Questions.question(question.id);
	}

	public static void report(long id) {
		MajorEntry entry = MajorEntry.findById(id);
		User user = User.find("byName", Security.connected()).first();
		entry.report(user);

		Questions.home();
	}

	/*** AJAX ***/

	public static void graphData(long id) {
		User puser = User.findById(id);
		if (puser != null)
			renderText(puser.graphData());
		else
			renderText("[]");
	}

	public static void checkUserExists(String username) {

		List<User> user = User.find("byName", username).fetch();
		renderJSON(user.size() > 0);

	}

}
