package controllers;

import java.io.File;
import java.util.ArrayList;
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

/**
 * The Class Admin contains all the admin controller methods.
 */
@Check("isAdmin")
@With(Secure.class)
public class Admin extends Controller {

	/** The Constant NUMBER_OF_USERS_PER_PAGE. */
	public static final int NUMBER_OF_USERS_PER_PAGE = 35;

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

	/**
	 * Show userlist.
	 * 
	 * @param page
	 *            the page
	 */
	public static void showUserlist(int page) {
		if (page <= 0) {
			page = 1;
		}
		int pages = (int) Math.ceil((double) User.count()
				/ (double) NUMBER_OF_USERS_PER_PAGE);
		int fetchFrom = (page - 1) * NUMBER_OF_USERS_PER_PAGE;
		List<User> users = User.all().from(fetchFrom).fetch(
				NUMBER_OF_USERS_PER_PAGE);
		List<Integer> pageList = new ArrayList<Integer>();
		for (int i = 1; i <= pages; i++) {
			pageList.add(i);
		}
		render(users, pageList, page);

	}

	/**
	 * Delete user.
	 * 
	 * @param id
	 *            the id
	 */
	public static void deleteUser(long id) {

		User user = User.findById(id);
		user.delete();
		Admin.showUserlist(1);
	}

	/**
	 * Toggle admin state.
	 * 
	 * @param id
	 *            the id
	 */
	public static void toggleAdminState(long id, int page) {
		User user = User.findById(id);

		user.isAdmin = !user.isAdmin;
		user.save();

		Admin.showUserlist(page);

	}

	/**
	 * Xml importer.
	 */
	public static void xmlImporter() {

		render();
	}

	/**
	 * Load an xml file in the database.
	 * 
	 * @param xmlfile
	 *            the xmlfile
	 */
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

	/**
	 * Checks if is xML file.
	 * 
	 * @param xmlfile
	 *            the xmlfile
	 * @return true, if is xML file
	 */
	private static boolean isXMLFile(File xmlfile) {
		String s = xmlfile.getName();
		String extension = s.substring(s.length() - 4).toLowerCase();

		return extension.equals(".xml");
	}

	/**
	 * Toggle activate user.
	 * 
	 * @param id
	 *            the id
	 */
	public static void toggleActivateUser(long id, String deactivationReason,
			int page) {
		User user = User.findById(id);
		user.isActivated = !user.isActivated;
		user.save();
		if (!user.isActivated) {
			Mails.deactivationMail(user, deactivationReason);
		} else {
			Mails.reactivationMail(user, deactivationReason);
		}
		Admin.showUserlist(page);
	}

	/**
	 * Lists the fraud point violations of a given user.
	 * 
	 * @param id
	 *            the userid
	 * @return the fraud point violations
	 */
	public static void getFraudPointViolations(long id) {

		User user = User.findById(id);

		renderText(user.fraudPointViolations());

	}
}
