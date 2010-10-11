package controllers;

import models.Question;
import models.User;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Secured extends Controller {

	public static void newQuestion(@Required String content) {
		if (!validation.hasErrors()) {
			Question question = new Question(User.get(Security.connected()),
					content);
			Application.question(question.id());
		} else {
			Application.index();
		}
	}

	public static void newAnswer(int id, @Required String content) {
		if (!validation.hasErrors() && Question.get(id) != null) {
			Question.get(id).answer(User.get(Security.connected()), content);
			Application.question(id);
		} else {
			Application.index();
		}
	}

	public static void voteQuestionUp(int id) {
		if (Question.get(id) != null) {
			Question.get(id).voteUp(User.get(Security.connected()));
			Application.question(id);
		} else {
			Application.index();
		}
	}

	public static void voteQuestionDown(int id) {
		if (Question.get(id) != null) {
			Question.get(id).voteDown(User.get(Security.connected()));
			Application.question(id);
		} else {
			Application.index();
		}
	}

	public static void voteAnswerUp(int question, int id) {
		if (Question.get(question) != null
				&& Question.get(question).getAnswer(id) != null) {
			Question.get(question).getAnswer(id).voteUp(
					User.get(Security.connected()));
			Application.question(question);
		} else {
			Application.index();
		}
	}

	public static void voteAnswerDown(int question, int id) {
		if (Question.get(question) != null
				&& Question.get(question).getAnswer(id) != null) {
			Question.get(question).getAnswer(id).voteDown(
					User.get(Security.connected()));
			Application.question(question);
		} else {
			Application.index();
		}
	}

}
