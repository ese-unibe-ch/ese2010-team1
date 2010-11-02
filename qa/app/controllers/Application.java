package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import models.Answer;
import models.Entry;
import models.FileEntry;
import models.Question;
import models.User;
import play.data.validation.Email;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;

/**
 * The Class Application.
 */
public class Application extends Controller {

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
	 * Index.
	 */
	public static void index() {
		long userCount = User.count();
		List<Question> questions = Question.questions();
		boolean recent = false;
		render(questions, userCount, recent);
	}

	/**
	 * Renders the home screen.
	 */
	public static void recentQuestions() {
		long userCount = User.count();
		List<Question> questions = Question.recentQuestions();
		boolean recent = true;

		render("Application/index.html", questions, userCount, recent);
	}

	/**
	 * Shows the question with the given id.
	 * 
	 * @param id
	 *            the question id
	 */

	public static void question(long id) {
		Question question = Question.find("byID", id).first();
		if (question == null) {
			render();
		} else {
			List<Answer> answers = question.answers();
			render(question, answers);
		}
	}

	/**
	 * Shows the user creation page.
	 */
	public static void createUser() {
		render();
	}

	/**
	 * Save the new user in the database.
	 * 
	 * @param username
	 *            the username
	 * @param email
	 *            the email
	 * @param password
	 *            the password
	 * @param password2
	 *            the confirmation of the password
	 */
	public static void addUser(
			@Required(message = "A valid username is required") String username,
			@Required(message = "A valid e-mail is required") @Email String email,
			@Required(message = "A password is required") String password,
			String password2) {

		// validate all parameters
		if (!password.isEmpty()) {
			validation.equals(password, password2).message(
					"passwords don't match");
		}

		validation.isTrue(!User.exists(username)).message(
				"Username already exists");

		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			Application.createUser();
		}

		new User(username, email, password).save();

		Application.index();
	}

	/**
	 * User check.
	 * 
	 * @param username
	 *            the username
	 */
	public static void userCheck(String username) {
		boolean exists = User.exists(username);
		render(exists);
	}

	/**
	 * Search.
	 * 
	 * @param searchString
	 *            the search string
	 */
	public static void search(@Required String searchString) {

		List<Entry> foundEntrys = new ArrayList<Entry>();
		List<Entry> results = new ArrayList<Entry>();
		if (!validation.hasErrors()) {
			foundEntrys.addAll(Question.searchTitle(searchString));
			foundEntrys.addAll(Entry.searchContent(searchString));
			foundEntrys.addAll(Question.searchTaggedWith(searchString));
			foundEntrys
					.addAll(FileEntry.searchEntrysWithFilename(searchString));

			for (Entry entry : foundEntrys) {

				if (!results.contains(entry)) {
					results.add(entry);
				}
			}

		}

		render(searchString, results);
	}

	public static void getFile(long id) {

		FileEntry entry = FileEntry.findById(id);

		File file = new File(entry.getAbsolutePath());

		renderBinary(file, entry.uploadFilename);
	}
}