package controllers;

import models.User;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class UserFuncs extends Controller {

	public static void showProfile(long id) {

		User user = User.getById(id);
		render(user);
	}

	public static void editProfile(long id) {

		User user = User.getById(id);

		render(user);
	}

	public static void saveProfile(long id) {

	}
}
