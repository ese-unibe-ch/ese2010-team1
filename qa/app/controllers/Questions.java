package controllers;

import java.util.List;

import models.Answer;
import models.Entry;
import models.Question;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;

/**
 * View questions.
 */
public class Questions extends Controller {

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

	/**** VIEWS ****/

	/**
	 * List and show questions.
	 */
	public static void list() {
		List<Question> questions = Question.questions();
		render(questions);
	}

	/*** AJAX ***/

	/**
	 * Get a question.
	 */
	public static void get(long id) {
		Question question = Question.findById(id);
		if (question == null) {
			render();
		} else {
			List<Answer> answers = question.answers();
			render(question, answers);
		}
	}

	public static void voteUp(long id) {

		Entry entry = Entry.findById(id);
		User user = User.find("byName", Security.connected()).first();
		entry.voteUp(user);
		entry.save();

		if (entry instanceof models.Question)
			Questions.get(id);
		else
			Questions.get(((Answer) entry).question.id);

	}

	public static void voteDown(long id) {

		Entry entry = Entry.findById(id);
		User user = User.find("byName", Security.connected()).first();
		entry.voteDown(user);
		entry.save();

		if (entry instanceof models.Question)
			Questions.get(id);
		else
			Questions.get(((Answer) entry).question.id);
	}

}