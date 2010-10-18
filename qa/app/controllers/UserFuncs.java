package controllers;

import java.util.Iterator;
import java.util.List;

import models.ProfileEntry;
import models.ProfileItem;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class UserFuncs extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.find("byName", Security.connected()).first();
			renderArgs.put("user", user);
		}
	}

	public static void showProfile(long id) {

		List<ProfileItem> titles = ProfileItem.findAll();

		User puser = User.findById(id);
		render(puser, titles);
	}

	public static void editProfile(long id) {

		List<ProfileItem> titles = ProfileItem.findAll();

		User puser = User.findById(id);

		render(puser, titles);
	}

	// TODO something works not like it should...

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
