package controllers;

import models.Answer;
import models.Entry;
import models.Question;
import models.User;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Secured extends Controller {

	public static void newQuestion(@Required String title,
			@Required String content) {
		if (!validation.hasErrors()) {
			User user = User.find("byName", Security.connected()).first();
			Question question = user.addQuestion(title, content);
			Application.question(question.id);
		} else {
			Application.index();
		}
	}

	public static void newAnswer(long id, @Required String content) {
		if (!validation.hasErrors() && Question.findById(id) != null) {
			User user = User.find("byName", Security.connected()).first();
			Question.<Question> findById(id).answer(user, content);
			Application.question(id);
		} else {
			Application.index();
		}
	}

	public static void voteQuestionUp(long id) {
		if (Question.findById(id) != null) {
			User user = User.find("byName", Security.connected()).first();
			Question.<Question> findById(id).voteUp(user);
			Application.question(id);
		} else {
			Application.index();
		}
	}

	public static void voteQuestionDown(long id) {
		if (Question.<Question> findById(id) != null) {
			User user = User.find("byName", Security.connected()).first();
			Question.<Question> findById(id).voteDown(user);
			Application.question(id);
		} else {
			Application.index();
		}
	}

	public static void voteAnswerUp(long qid, long aid) {
		User user = User.find("byName", Security.connected()).first();
		Answer answer = Answer.findById(aid);
		Question question = Question.findById(qid);
		if (question != null && question.hasAnswer(answer)) {

			answer.voteUp(user);
			Application.question(qid);

		} else {
			Application.index();
		}
	}

	public static void voteAnswerDown(long qid, long aid) {
		User user = User.find("byName", Security.connected()).first();
		Answer answer = Answer.findById(aid);
		Question question = Question.findById(qid);
		if (question != null && question.hasAnswer(answer)) {
			answer.voteDown(user);
			Application.question(qid);
		} else {
			Application.index();
		}
	}

	public static void setBestAnswer(long id) {
		Answer answer = Answer.<Answer> findById(id);
		if (answer == null
				|| !answer.question.owner.name.equals(Security.connected())) {
			Application.index();
		} else if (answer.question.canSetBestAnswer()) {
			answer.question.setBestAnswer(answer);
			Application.question(answer.question.id);
		}
	}

	public static void resetBestAnswer(long id) {
		Question question = Question.<Question> findById(id);
		if (question == null
				|| !question.owner.name.equals(Security.connected())) {
			Application.index();
		} else if (question.canSetBestAnswer()) {
			question.resetBestAnswer();
			Application.question(question.id);
		}
	}

	public static void toggleAdminState() {
		User user = User.find("byName", Security.connected()).first();

		if (user.isAdmin)
			user.isAdmin = false;
		else
			user.isAdmin = true;

		user.save();

		Application.index();

	}

	public static void deleteEntry(long id) {

		Entry entry = Entry.findById(id);
		entry.delete();

		Application.index();
	}
}
