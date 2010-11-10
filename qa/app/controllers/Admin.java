package controllers;

import java.util.List;

import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Admin extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.find("byName", Security.connected()).first();
			renderArgs.put("user", user);
		}
	}

	@Check("isAdmin")
	public static void adminPanel() {

		render();

	}

	@Check("isAdmin")
	public static void showUserlist() {

		List<User> users = User.findAll();

		render(users);

	}

	@Check("isAdmin")
	public static void deleteUser(long id) {

		User user = User.findById(id);
		user.delete();
		Admin.showUserlist();
	}

	@Check("isAdmin")
	public static void toggleAdminState(long id) {
		User user = User.findById(id);

		if (user.isAdmin)
			user.isAdmin = false;
		else
			user.isAdmin = true;

		user.save();

		Admin.showUserlist();

	}

}
