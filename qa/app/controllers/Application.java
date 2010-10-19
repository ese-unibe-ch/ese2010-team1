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
	public static void addUser(@Required String username,
			@Required @Email String email, @Required String password) {
		if (validation.hasErrors()) {
			render("Application/createUser.html", username, email, password);
		}
		if (!User.exists(username)) {

			new User(username, email, password).save();
		}
		Application.index();
	}

}