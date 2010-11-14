package controllers;

import java.io.File;
import java.util.List;

import models.User;
import models.importer.XMLHandler;
import models.importer.XMLImporter;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@Check("isAdmin")
@With(Secure.class)
public class Admin extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.find("byName", Security.connected()).first();
			renderArgs.put("user", user);
		}
	}

	public static void adminPanel() {

		render();

	}

	public static void showUserlist() {

		List<User> users = User.findAll();

		render(users);

	}

	public static void deleteUser(long id) {

		User user = User.findById(id);
		user.delete();
		Admin.showUserlist();
	}

	public static void toggleAdminState(long id) {
		User user = User.findById(id);

		if (user.isAdmin)
			user.isAdmin = false;
		else
			user.isAdmin = true;

		user.save();

		Admin.showUserlist();

	}

	public static void xmlImporter() {

		render();
	}

	public static void loadXML(@Required File xmlfile) {

		boolean exists = xmlfile.exists();
		int userCount = 0;
		int questionCount = 0;
		String report = "";

		validation.isTrue(isXMLFile(xmlfile)).message(
				"Wrong filetype uploaded!");
		if (isXMLFile(xmlfile)) {

			try {
				XMLImporter importer = new XMLImporter(xmlfile);
				XMLHandler handler = importer.getHandler();
				userCount = handler.getUserCount();
				questionCount = handler.getQuestionCount();
				report = handler.getReport();

			} catch (Exception e) {
				e.printStackTrace();
			}

			List<User> users = User.findAll();

			render(exists, users, userCount, questionCount, report);
		}

	}

	public static void flushDatabase() {

		List<User> users = User.findAll();
		User loggedInUser = User.find("byName", Security.connected()).first();

		for (User u : users) {

			if (!u.equals(loggedInUser)) {
				u.delete();
			}

		}

		User anonymous = new User("Anonymous", "anonymous@qa.local",
				"notAllowedToLogIn").save();

	}

	private static boolean isXMLFile(File xmlfile) {
		// TODO Auto-generated method stub
		return true;
	}

}
