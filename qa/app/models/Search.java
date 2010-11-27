package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Class Search.
 */
public class Search {

	// TS Replace whitespace by percent symbol to get more hits
	/**
	 * Search the titles for the searchString.
	 * 
	 * @param searchString
	 *            the search string
	 * @return the list
	 */
	public static List<Entry> searchTitle(String searchString) {
		return Question.find("byTitleLike", "%" + searchString + "%").fetch();
	}

	/**
	 * Search the entrys content field for the searchString.
	 * 
	 * @param searchString
	 *            the search string
	 * @return the result list
	 */
	public static List<Entry> searchContent(String searchString) {

		return Entry.find("byContentLike", "%" + searchString + "%").fetch();

	}

	/**
	 * Search questions tagged with the searchString.
	 * 
	 * @param searchString
	 *            the search string
	 * @return the list
	 */

	// TS if possible try to get the list with jpa query
	public static List<Question> searchTaggedWith(String searchString) {
		List<Tag> matchingTags = Tag.find("byNameLike",
				"%" + searchString + "%").fetch();

		List<Question> result = new ArrayList<Question>();
		for (Tag tag : matchingTags) {
			for (Question question : Question.findTaggedWith(tag.name)) {

				if (!result.contains(question)) {
					result.add(question);

				}
			}
		}

		return result;
	}

	/**
	 * Search filename.
	 * 
	 * @param searchString
	 *            the search string
	 * @return the list
	 */
	public static List<FileEntry> searchFilename(String searchString) {

		return FileEntry.find("byUploadFilenameLike", "%" + searchString + "%")
				.fetch();
	}

	/**
	 * Search entrys with filename.
	 * 
	 * @param searchString
	 *            the search string
	 * @return the result list
	 */
	public static List<Entry> searchEntrysWithFilename(String searchString) {

		List<Entry> entrys = new ArrayList<Entry>();

		for (FileEntry entry : searchFilename(searchString)) {
			entrys.add(entry.entry);
		}

		return entrys;
	}

	/**
	 * Sort by rating.
	 * 
	 * @param entrys
	 *            the entrys
	 * @return the list
	 */
	private static List<Entry> sortByRating(List<Entry> entrys) {
		EntryComperator comp = new EntryComperator();
		Collections.sort(entrys, comp);

		return entrys;

	}

	/**
	 * Search all entrys with the searchString in a field.
	 * 
	 * @param searchString
	 *            the search string
	 * @return the list
	 */
	public static List<Entry> searchEntry(String searchString) {

		List<Entry> foundEntrys = new ArrayList<Entry>();
		List<Entry> results = new ArrayList<Entry>();

		foundEntrys.addAll(searchTitle(searchString));
		foundEntrys.addAll(searchContent(searchString));
		foundEntrys.addAll(searchTaggedWith(searchString));
		foundEntrys.addAll(searchEntrysWithFilename(searchString));

		for (Entry entry : foundEntrys) {

			if (!results.contains(entry)) {
				results.add(entry);
			}
		}

		return sortByRating(results);

	}

	/**
	 * Search all questions with the searchString in it or in its answer.
	 * 
	 * @param searchString
	 *            the search string
	 * @return the list of questions
	 */
	public static List<Question> searchQuestions(String searchString) {
		List<Question> questions = new ArrayList<Question>();
		List<Entry> entries = searchEntry(searchString);
		for (Entry entry : entries) {
			Question question;
			if (entry instanceof Answer) {
				question = ((Answer) entry).question;
			} else {
				question = (Question) entry;
			}
			if (!questions.contains(question))
				questions.add(question);
		}
		return questions;
	}

	public static List<User> searchUsers(String searchString) {

		return User.find("name like ? or email like ?",
				"%" + searchString + "%", "%" + searchString + "%").fetch();
	}

}
