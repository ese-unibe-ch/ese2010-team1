package controllers;

import models.Answer;
import models.BestAnswerSetter;
import models.Entry;
import models.Question;
import models.User;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Secured extends Controller {

	public static void newQuestion(@Required String title,
			@Required String content, String tags) {
		if (!validation.hasErrors()) {
			User user = User.find("byName", Security.connected()).first();
			Question question = user.addQuestion(title, content);
			// neu
			String[] separatedTags = tags.split(",");
			for (String tag : separatedTags) {
				question.tagItWith(tag).save();
			}

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
		if (Answer.<Answer> findById(id) != null) {
			Answer answer = Answer.<Answer> findById(id);
			User user = User.find("byName", Security.connected()).first();
			assert (user == answer.question().owner());
			BestAnswerSetter bestAnswerSetter = new BestAnswerSetter(id);
			Application.question(answer.question().id);

		} else {
			Application.index();
		}
	}

	public static void undoBestAnswer(long id) {
		if (Answer.<Answer> findById(id) != null) {
			Answer answer = Answer.<Answer> findById(id);
			User user = User.find("byName", Security.connected()).first();
			assert (user == answer.question().owner());
			answer.bestAnswerSetter().undo();
			answer.save();
			answer.question().save();
			Application.question(answer.question().id);
		} else {
			Application.index();
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
