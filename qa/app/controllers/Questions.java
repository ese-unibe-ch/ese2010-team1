package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import models.Answer;
import models.Comment;
import models.Entry;
import models.FileEntry;
import models.MajorEntry;
import models.ProfileItem;
import models.Question;
import models.Search;
import models.Tag;
import models.User;
import models.Vote;
import models.Entry.ContentState;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;

/**
 * View questions.
 */
public class Questions extends Controller {

	/** The Constant NUMBER_OF_LOADED_QUESTIONS. */
	public static final int NUMBER_OF_LOADED_QUESTIONS = 20;

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

	/**
	 * ** VIEWS ***.
	 */

	/**
	 * List and show questions.
	 */

	public static void home() {
		List<Question> questions = Question.questions(
				NUMBER_OF_LOADED_QUESTIONS, 1);
		render(questions);
	}

	/**
	 * Question.
	 * 
	 * @param id
	 *            the id
	 */
	public static void question(long id) {
		Question display = Question.findById(id);
		List<Question> questions = Question.questions(
				NUMBER_OF_LOADED_QUESTIONS, 1);
		render("Questions/home.html", questions, display);
	}

	/**
	 * * AJAX **.
	 * 
	 * @param id
	 *            the id
	 */

	/**
	 * Get a specific question by id.
	 */
	public static void get(long id) {
		Question question = Question.findById(id);
		if (question == null) {
			render("Questions/question.html");
		} else {
			List<Answer> answers = question.answers();
			render("Questions/question.html", question, answers);
		}
	}

	/**
	 * Returns a given number of hot questions.
	 * 
	 * @param page
	 *            the page
	 */
	public static void hot(int page) {
		List<Question> questions = Question.questions(
				NUMBER_OF_LOADED_QUESTIONS, page);
		render("Questions/list.html", questions);
	}

	/**
	 * Returns a given number of active questions.
	 * 
	 * @param page
	 *            the page
	 */
	public static void active(int page) {
		Set<Question> questions = Question.recentQuestions(
				NUMBER_OF_LOADED_QUESTIONS, page);
		render("Questions/list.html", questions);
	}

	/**
	 * Returns a given number of mine questions.
	 * 
	 * @param page
	 *            the page
	 */
	public static void mine(int page) {
		if (!Security.isConnected()) {
			badRequest();
		} else {
			List<Question> questions = ((User) User.find("byName",
					Security.connected()).first()).questions(
					NUMBER_OF_LOADED_QUESTIONS, page);
			render("Questions/list.html", questions);
		}
	}

	/**
	 * Returns every questions / entries matching the search string.
	 * 
	 * @param string
	 *            the search string
	 * @param page
	 *            the page
	 */
	public static void search(String string, int page) {
		Set<Question> questions = Search.searchQuestions(string,
				NUMBER_OF_LOADED_QUESTIONS, page);
		render(questions);
	}

	/**
	 * Returns users matching the search string.
	 * 
	 * @param string
	 *            the search string
	 */
	public static void searchUsers(String string) {
		List<User> users = Search.searchUsers(string);
		render("Questions/search.html", users);
	}

	/**
	 * Returns a form to edit the entry.
	 * 
	 * @param type
	 *            the type
	 * @param id
	 *            the id
	 */
	public static void form(String type, long id) {
		Entry entry = Entry.findById(id);
		render(type, entry);
	}

	/**
	 * Returns a single entry.
	 * 
	 * @param id
	 *            the id
	 */
	public static void entry(long id) {
		Entry entry = Entry.findById(id);
		render("Questions/entry.html", entry);
	}

	/**
	 * Adds a new question to the database.
	 * 
	 * @param title
	 *            the title
	 * @param content
	 *            the content
	 * @param tags
	 *            the tags
	 */
	public static void add(@Required String title, @Required String content,
			String tags) {
		checkAuthenticity();

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

	/**
	 * Answer a question.
	 * 
	 * @param id
	 *            the id
	 * @param content
	 *            the content
	 * @param file
	 *            the file
	 */
	public static void answer(long id, @Required String content, File file) {
		checkAuthenticity();

		User user = User.find("byName", Security.connected()).first();
		Question question = Question.findById(id);
		if (user != null && question != null && !validation.hasErrors()) {
			Answer answer = question.answer(user, content);
			if (file != null && file.exists()) {
				user.addFileToEntry(file, answer);
				question(id);
			}
			renderJSON("{\"success\": 1, \"id\": " + answer.id + "}");
		} else {
			renderJSON("{\"success\": 0}");
		}
	}

	/**
	 * Edits the question.
	 * 
	 * @param id
	 *            the id
	 * @param content
	 *            the content
	 * @param tags
	 *            the tags
	 * @param file
	 *            the file
	 */
	public static void edit(long id, String content, String tags, File file) {
		checkAuthenticity();

		User user = (User) User.find("byName", Security.connected()).first();
		Entry entry = Entry.findById(id);
		entry.edit(content, user);
		if (entry instanceof Question) {
			((Question) entry).removeAllTags();
			if (!tags.equals("Tags")) {
				String[] separatedTags = tags.split(", ");

				for (String tag : separatedTags) {
					((Question) entry).tagItWith(tag);
				}

			}
		} else if (file != null && file.exists()) {
			user.addFileToEntry(file, entry);
		}

		entry.save();

		if (entry instanceof Answer) {
			question(((Answer) entry).question.id);
		} else {
			question(entry.id);

		}
	}

	/**
	 * Vote up.
	 * 
	 * @param id
	 *            the id
	 */
	public static void voteUp(long id) {
		checkAuthenticity();

		Entry entry = Entry.<Entry> findById(id);
		User user = User.find("byName", Security.connected()).first();

		if (user.reputation >= ProfileItem.count()) {
			if (entry != null && user != null) {
				entry.voteUp(user);
				entry.save();
			}
		}
		render("Questions/entry.html", entry);
	}

	/**
	 * Vote down.
	 * 
	 * @param id
	 *            the id
	 */
	public static void voteDown(long id) {
		checkAuthenticity();

		Entry entry = Entry.<Entry> findById(id);
		User user = User.find("byName", Security.connected()).first();
		if (user.reputation >= ProfileItem.count()) {
			if (entry != null && user != null) {
				entry.voteDown(user);
				entry.save();
			}
		}
		render("Questions/entry.html", entry);
	}

	/**
	 * Removes the vote.
	 * 
	 * @param id
	 *            the id
	 */
	public static void removeVote(long id) {
		checkAuthenticity();

		User user = User.find("byName", Security.connected()).first();
		Entry entry = Entry.findById(id);
		if (entry != null && user != null) {
			entry.removeVote((Vote) Vote.find("byOwnerAndEntry", user, entry)
					.first());
			entry.owner.calcReputation();
			entry.owner.save();
		}

		render("Questions/entry.html", entry);
	}

	/**
	 * Sets the best answer.
	 * 
	 * @param id
	 *            the new best answer
	 */
	public static void setBestAnswer(long id) {
		checkAuthenticity();

		Answer answer = Answer.<Answer> findById(id);
		if (answer == null) {
			badRequest();
		} else {
			if (answer.question.owner.name.equals(Security.connected())
					&& answer.question.canSetBestAnswer()) {
				answer.question.setBestAnswer(answer);
			}
			Question question = answer.question;
			render("Questions/question.html", question);
		}
	}

	/**
	 * Reset best answer.
	 * 
	 * @param id
	 *            the id
	 */
	public static void resetBestAnswer(long id) {
		checkAuthenticity();

		Question question = Question.<Question> findById(id);
		if (question == null) {
			badRequest();
		} else {
			if (question.owner.name.equals(Security.connected())
					&& question.canSetBestAnswer()) {
				question.resetBestAnswer();
			}
			render("Questions/question.html", question);
		}
	}

	/**
	 * Delete.
	 * 
	 * @param id
	 *            the id
	 */
	public static void delete(long id) {
		checkAuthenticity();

		Entry entry = Entry.findById(id);
		User owner = entry.owner;
		if (entry == null)
			home();

		entry.delete();
		owner.calcReputation();
		owner.save();

		if (entry instanceof Question)
			home();

		question(((Answer) entry).question.id);
	}

	/**
	 * Sets the notification as red.
	 * 
	 * @param id
	 *            the new notification as red
	 */
	public static void setNotificationAsRed(long id) {
		checkAuthenticity();

		models.Notification.hasBeenRead(id);
	}

	/**
	 * Delete entry.
	 * 
	 * @param id
	 *            the id
	 */
	public static void deleteEntry(long id) {
		checkAuthenticity();

		Entry entry = Entry.findById(id);
		User owner = entry.owner;
		entry.delete();
		owner.calcReputation();
		owner.save();
		Question question = entry instanceof Question ? (Question) entry
				: ((Answer) entry).question;

		render("Questions/question.html", question);
	}

	/**
	 * Delete file entry.
	 * 
	 * @param id
	 *            the id
	 * @param qid
	 *            the qid
	 */
	public static void deleteFileEntry(long id, long qid) {
		checkAuthenticity();

		FileEntry entry = FileEntry.findById(id);
		if (entry != null)
			entry.delete();
		question(qid);
	}

	/**
	 * Gets the file.
	 * 
	 * @param id
	 *            the id
	 * @return the file
	 */
	public static void getFile(long id) {

		FileEntry entry = FileEntry.findById(id);

		File file = new File(entry.getAbsolutePath());

		renderBinary(file, entry.getFilename());
	}

	/**
	 * Gets an older version of the content.
	 * 
	 * @param id
	 *            the id
	 */
	public static void version(long id) {
		ContentState state = ContentState.findById(id);
		if (state != null) {
			renderText(state.content);
		}
	}

	/**
	 * Comment the question.
	 * 
	 * @param id
	 *            the id
	 * @param content
	 *            the content
	 */
	public static void comment(long id, @Required String content) {
		checkAuthenticity();

		User user = User.find("byName", Security.connected()).first();
		Entry entry = Entry.findById(id);

		if (!validation.hasErrors() && entry != null && user != null) {
			user.addComment(entry, content);
		}

		render("Questions/entry.html", entry);
	}

	/**
	 * Delete comment.
	 * 
	 * @param id
	 *            the id
	 */
	public static void deleteComment(long id) {
		checkAuthenticity();

		Comment comment = Comment.findById(id);
		if (comment != null)
			comment.delete();

		renderText("");
	}

	/**
	 * Gets the tag list.
	 * 
	 * @return the tag list
	 */
	public static void getTagList() {

		List<Tag> tags = Tag.findAll();
		List<String> tagList = new ArrayList<String>();
		for (Tag tag : tags) {

			tagList.add(tag.name);
		}
		renderJSON(tagList);

	}

	/**
	 * Report question as spam.
	 * 
	 * @param id
	 *            the id
	 */
	public static void report(long id) {
		MajorEntry entry = MajorEntry.findById(id);
		User user = User.find("byName", Security.connected()).first();
		entry.report(user);
		if (entry instanceof Comment) {
			Comment comment = (Comment) entry;
			entry = comment.entry;
		}

		render("Questions/entry.html", entry);
	}

	/**
	 * Like comment.
	 * 
	 * @param id
	 *            the id
	 */
	public static void likeComment(long id) {
		User user = User.find("byName", Security.connected()).first();
		Comment comment = Comment.findById(id);
		comment.like(user);

		Entry entry = comment.entry;
		if (entry instanceof models.Question)
			render("Questions/entry.html", entry);
		else {
			Answer answer = (Answer) entry;
			entry = answer;
			render("Questions/entry.html", entry);
		}

	}

	/**
	 * Unlike comment.
	 * 
	 * @param id
	 *            the id
	 */
	public static void unlikeComment(long id) {
		User user = User.find("byName", Security.connected()).first();
		Comment comment = Comment.findById(id);
		comment.unlike(user);

		Entry entry = comment.entry;
		if (entry instanceof models.Question)
			render("Questions/entry.html", entry);
		else {
			Answer answer = (Answer) entry;
			entry = answer;
			render("Questions/entry.html", entry);
		}
	}

}