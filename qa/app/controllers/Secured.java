package controllers;

import java.io.File;

import models.Answer;
import models.Comment;
import models.Entry;
import models.FileEntry;
import models.Question;
import models.User;
import models.Vote;
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
			if (!tags.equals("Tags")) {
				String[] separatedTags = tags.split(", ");

				for (String tag : separatedTags) {
					question.tagItWith(tag);
				}
			}

			Application.question(question.id);
		} else {
			Application.index();
		}
	}

	public static void newAnswer(long id, @Required String content, File file) {
		if (!validation.hasErrors() && Question.findById(id) != null) {
			User user = User.find("byName", Security.connected()).first();
			Answer answer = Question.<Question> findById(id).answer(user,
					content);

			if (file != null && file.exists()) {

				user.addFileToEntry(file, answer);
			}
			Application.question(id);
		}
	}

	public static void newComment(long id, @Required String content) {
		if (!validation.hasErrors() && Entry.findById(id) != null) {
			User user = User.find("byName", Security.connected()).first();
			Entry entry = Entry.findById(id);
			user.addComment(entry, content);

			if (entry instanceof models.Question)
				Application.question(id);
			else
				Application.question(((Answer) entry).question.id);
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

	public static void voteEntryDown(long id) {
		Entry entry = Entry.<Entry> findById(id);
		if (entry != null) {
			User user = User.find("byName", Security.connected()).first();
			entry.voteDown(user);
			Application.question(entry instanceof Question ? id
					: ((Answer) entry).question.id);
		} else {
			Application.index();
		}
	}

	public static void voteEntryUp(long id) {
		Entry entry = Entry.<Entry> findById(id);
		if (entry != null) {
			User user = User.find("byName", Security.connected()).first();
			entry.voteUp(user);
			Application.question(entry instanceof Question ? id
					: ((Answer) entry).question.id);
		} else {
			Application.index();
		}
	}

	public static void removeEntryVote(long id) {
		User user = User.find("byName", Security.connected()).first();
		Entry entry = Entry.findById(id);
		if (entry != null) {
			entry.removeVote((Vote) Vote.find("byOwnerAndEntry", user, entry)
					.first());
			Application.question(entry instanceof Question ? id
					: ((Answer) entry).question.id);
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

	public static void deleteEntry(long id) {

		Entry entry = Entry.findById(id);
		entry.delete();

		Application.index();
	}

	public static void deleteFileEntry(long id, long qid) {

		FileEntry entry = FileEntry.findById(id);
		entry.delete();

		Application.question(qid);
	}

	public static void deleteComment(long id) {

		Comment comment = Comment.findById(id);
		comment.delete();

		Application.index();
	}

	public static void edit(long id, String content) {

		Entry entry = Entry.findById(id);
		entry.content = content;
		entry.save();

		if (entry instanceof Answer) {
			Application.question(((Answer) entry).question.id);
		} else {
			Application.question(entry.id);

		}

	}
}
