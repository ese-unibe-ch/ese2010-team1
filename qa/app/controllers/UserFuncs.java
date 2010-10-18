package controllers;

import java.util.List;

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

		User puser = User.findById(id);

		render(puser);
	}

	public static void saveProfile(long id, String biography,
			String lifePhilosophy, String editorOfChoice) {

		User user = User.findById(id);

		user.biography = biography;
		user.lifePhilosophy = lifePhilosophy;
		user.editorOfChoice = editorOfChoice;

		user.save();

		showProfile(id);

	}
}
