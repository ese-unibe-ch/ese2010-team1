package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Answer;
import models.Entry;
import models.Question;
import models.User;
import play.data.validation.Email;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;

public class Application extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.find("byName", Security.connected()).first();
			renderArgs.put("user", user);
		}
	}

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

	public static void userCheck(String username) {
		boolean exists = User.exists(username);
		render(exists);
	}

	public static void search(@Required String searchString) {

		List<Entry> results = new ArrayList<Entry>();
		if (!validation.hasErrors()) {
			results.addAll(Question.searchTitle(searchString));
			results.addAll(Entry.searchContent(searchString));
		}

		render(searchString, results);
	}
}