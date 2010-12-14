package controllers;

import java.io.File;
import java.util.List;

import models.Answer;
import models.Question;
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

		showUserlist();

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

		user.isAdmin = !user.isAdmin;
		user.save();

		Admin.showUserlist();

	}

	public static void xmlImporter() {

		render();
	}

	public static void loadXML(@Required File xmlfile) {

		int userCount = 0;
		int questionCount = 0;
		int answerCount = 0;
		long parseTime = 0;
		String report = "";

		validation.isTrue(isXMLFile(xmlfile)).message(
				"Wrong filetype uploaded!");
		if (isXMLFile(xmlfile)) {

			try {
				XMLImporter importer = new XMLImporter(xmlfile);
				XMLHandler handler = importer.getHandler();
				parseTime = importer.getParseTime();
				userCount = handler.getUserCount();
				questionCount = handler.getQuestionCount();
				answerCount = handler.getAnswerCount();
				report = handler.getReport();

			} catch (Exception e) {
				e.printStackTrace();
			}

			List<User> users = User.findAll();
			List<Question> questions = Question.findAll();
			List<Answer> answers = Answer.findAll();

			render(users, questions, answers, userCount, questionCount,
					answerCount, parseTime, report);
		}

		validation.keep();

		render("Admin/xmlImporter.html");

	}

	private static boolean isXMLFile(File xmlfile) {
		String s = xmlfile.getName();
		String extension = s.substring(s.length() - 4).toLowerCase();

		return extension.equals(".xml");
	}

	public static void activateUser() {

	}

}
