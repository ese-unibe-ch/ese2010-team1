package controllers;

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

	// TODO would be easy with JPA so do it when the model works fine!

	/*
	 * public static void voteAnswerUp(long qid, long id) { if
	 * (Question.<Question> findById(qid) != null && Question.<Question>
	 * findById(qid).getAnswer(id) != null) { Question.<Question>
	 * findById(qid).getAnswer(id).voteUp( User.get(Security.connected()));
	 * Application.question(qid); } else { Application.index(); } }
	 * 
	 * public static void voteAnswerDown(long qid, long id) { User user =
	 * User.find("byName", Security.connected()).first(); if
	 * (Question.<Question> findById(qid) != null && Question.<Question>
	 * findById(qid).getAnswer(id) != null) { Question.<Question>
	 * findById(qid).getAnswer(id).voteDown(user); Application.question(qid); }
	 * else { Application.index(); } }
	 */

}
