package controllers;

import java.util.List;

import models.Answer;
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

	/**
	 * Renders the home screen.
	 */
	public static void index() {

		long userCount = User.count();
		List<Question> questions = Question.questions();
		render(questions, userCount);
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
	 */
	public static void addUser(
			@Required(message = "A valid username is required") String username,
			@Required(message = "A valid e-mail is required") @Email String email,
			@Required(message = "A password is required") String password,
			String password2) {
		if (!password.isEmpty())
			validation.isTrue(password.equals(password2)).message(
					"passwords don't match");

		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			Application.createUser();
		}
		if (!User.exists(username)) {

			new User(username, email, password).save();
		} else {
			// TS generate method for notificate username exists already
		}
		Application.index();
	}

}