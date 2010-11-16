package controllers;

import java.util.Iterator;
import java.util.List;

import models.ProfileEntry;
import models.ProfileItem;
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

		List<ProfileEntry> entrys = ProfileEntry.findAll();

		for (ProfileEntry entry : entrys) {

			System.out.println(entry.user.name + " " + entry.entry + " "
					+ entry.item.title);
		}

		User puser = User.findById(id);

		render(puser, titles);
	}

	public static void createUser() {

		render();
	}

	public static void addUser(
			@Required(message = "A valid username is required") String username,
			@Required(message = "A valid e-mail is required") @Email String email,
			@Required(message = "A password is required") String password,
			String password2) {

		// validate all parameters
		if (!password.isEmpty()) {
			validation.equals(password, password2).message(
					"passwords don't match");
		}

		validation.isTrue(!User.exists(username)).message(
				"Username already exists");

		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			Users.createUser();
		}

		new User(username, email, password).save();

		Questions.home();
	}

	public static void saveProfile(long id, String[] profileEntry) {

		User user = User.findById(id);
		List<ProfileItem> titles = ProfileItem.findAll();

		Iterator<ProfileItem> it = titles.iterator();
		for (String newEntry : profileEntry) {

			ProfileItem pentry = it.next();
			pentry.editUserEntry(user, newEntry);

		}

		profile(id);

	}

	public static void get(long id, String theAction) {
		User user = User.findById(id);

		if (theAction.equals("Activities")) {

			render("Users/activities.html", user);

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

	/*** AJAX ***/

	public static void graphData(long id) {
		User puser = User.findById(id);
		if (puser != null)
			renderText(puser.graphData());
		else
			renderText("[]");
	}

}
