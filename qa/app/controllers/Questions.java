package controllers;

import java.util.List;

import models.Answer;
import models.Entry;
import models.Question;
import models.User;
import models.Vote;
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
		Entry entry = Entry.<Entry> findById(id);
		User user = User.find("byName", Security.connected()).first();
		if (entry != null && user != null) {
			entry.voteUp(user);
			entry.save();
		}
		render("Questions/entry.html", entry);
	}

	public static void voteDown(long id) {
		Entry entry = Entry.<Entry> findById(id);
		User user = User.find("byName", Security.connected()).first();
		if (entry != null && user != null) {
			entry.voteDown(user);
			entry.save();
		}
		render("Questions/entry.html", entry);
	}

	public static void removeVote(long id) {
		User user = User.find("byName", Security.connected()).first();
		Entry entry = Entry.findById(id);
		if (entry != null && user != null) {
			entry.removeVote((Vote) Vote.find("byOwnerAndEntry", user, entry)
					.first());
		}
		render("Questions/entry.html", entry);
	}

	public static void setBestAnswer(long id) {
		Answer answer = Answer.<Answer> findById(id);
		if (answer == null) {
			badRequest();
		} else {
			if (answer.question.owner.name.equals(Security.connected())
					&& answer.question.canSetBestAnswer()) {
				answer.question.setBestAnswer(answer);
			}
			Question question = answer.question;
			render("Questions/get.html", question);
		}
	}

	public static void resetBestAnswer(long id) {
		Question question = Question.<Question> findById(id);
		if (question == null) {
			badRequest();
		} else {
			if (question.owner.name.equals(Security.connected())
					&& question.canSetBestAnswer()) {
				question.resetBestAnswer();
			}
			render("Questions/get.html", question);
		}
	}

}