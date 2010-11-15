package controllers;

import java.util.List;

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

	public static void get(long id, String theAction) {

		if (theAction.equals("activities")) {

			renderText("activities");

		} else if (theAction.equals("questions")) {

		} else if (theAction.equals("answers")) {

		} else if (theAction.equals("graph")) {

			renderText("<div id=\"graph\"> <div id=\"graphcanvas\" style=\"width:600px;height:300px\"></div></div>");

		} else if (theAction.equals("statistics")) {

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
