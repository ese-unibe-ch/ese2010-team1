package controllers;

import java.util.List;

import models.Answer;
import models.Entry;
import models.Question;
import models.Search;
import models.User;
import models.Vote;
import play.data.validation.Required;
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
	public static void home() {
		List<Question> questions = Question.questions();
		render(questions);
	}

	/*** AJAX ***/

	/**
	 * Get a question.
	 */
	public static void question(long id) {
		Question question = Question.findById(id);
		if (question == null) {
			render();
		} else {
			List<Answer> answers = question.answers();
			render(question, answers);
		}
	}

	public static void hot() {
		List<Question> questions = Question.questions();
		render("Questions/list.html", questions);
	}

	public static void active() {
		List<Question> questions = Question.recentQuestions();
		render("Questions/list.html", questions);
	}

	public static void mine() {
		if (!Security.isConnected()) {
			badRequest();
		} else {
			List<Question> questions = ((User) User.find("byName",
					Security.connected()).first()).questions();
			render("Questions/list.html", questions);
		}
	}

	public static void search(String string) {
		List<Question> questions = Search.searchQuestions(string);
		render("Questions/list.html", questions);
	}

	public static void form(String type) {
		render(type);
	}

	public static void add(@Required String title, @Required String content,
			String tags) {
		User user = User.find("byName", Security.connected()).first();
		if (user != null && !validation.hasErrors()) {
			Question question = user.addQuestion(title, content);
			String[] separatedTags = tags.split(", ");
			for (String tag : separatedTags) {
				if (!tag.equals(""))
					question.tagItWith(tag);
			}

			renderJSON("{\"success\": 1, \"id\": " + question.id + "}");
		} else {
			renderJSON("{\"success\": 0}");
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