package controllers;

import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class UserFuncs extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.get(Security.connected());
			renderArgs.put("user", user);
		}
	}

	public static void showProfile(long id) {

		User puser = User.getById(id);
		render(puser);
	}

	public static void editProfile(long id) {

		User puser = User.getById(id);

		render(puser);
	}

	public static void saveProfile(long id) {

	}
}
