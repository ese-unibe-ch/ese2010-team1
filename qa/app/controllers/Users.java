package controllers;

import java.util.List;

import models.ProfileItem;
import models.User;
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

	/*** AJAX ***/

	public static void graphData(long id) {
		User puser = User.findById(id);
		if (puser != null)
			renderText(puser.graphData());
		else
			renderText("[]");
	}

}
