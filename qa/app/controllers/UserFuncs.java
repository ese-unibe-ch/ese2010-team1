package controllers;

import java.util.Iterator;
import java.util.List;

import models.ProfileEntry;
import models.ProfileItem;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

/**
 * The Class UserFuncs is responsible for all secured user actions.
 */
@With(Secure.class)
public class UserFuncs extends Controller {

	/**
	 * Sets the connected user to .
	 */
	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.find("byName", Security.connected()).first();
			renderArgs.put("user", user);
		}
	}

	/**
	 * Shows the user profile for the given user id.
	 * 
	 * @param id
	 *            the user id
	 */
	public static void showProfile(long id) {

		List<ProfileItem> titles = ProfileItem.findAll();

		User puser = User.findById(id);
		render(puser, titles);
	}

	/**
	 * Shows the user profile edit page for a given id.
	 * 
	 * @param id
	 *            the user id
	 */
	public static void editProfile(long id) {

		List<ProfileItem> titles = ProfileItem.findAll();

		User puser = User.findById(id);

		render(puser, titles);
	}

	/**
	 * Save new profile entrys to the database.
	 * 
	 * @param id
	 *            the user id
	 * @param entrys
	 *            the data entrys
	 */
	public static void saveProfile(long id, String[] entrys) {

		User user = User.findById(id);
		List<ProfileItem> titles = ProfileItem.findAll();

		Iterator<ProfileItem> it = titles.iterator();

		for (String entry : entrys) {

			ProfileItem pentry = it.next();
			pentry.editUserEntry(user, entry);

		}

		System.out.print(ProfileEntry.count());

		showProfile(id);

	}
}
