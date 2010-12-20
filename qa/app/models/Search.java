package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.helper.EntryComperator;

/**
 * The Class Search is responsible for searching in different fields of entries.
 */
public class Search {

	/**
	 * Search the titles for the searchString.
	 * 
	 * @param searchString
	 *            the search string
	 * @return the list
	 */
	public static List<Entry> searchTitle(String searchString) {
		return Question.find("byTitleLike",
				"%" + searchString.toLowerCase() + "%").fetch();
	}

	/**
	 * Search the entrys content field for the searchString.
	 * 
	 * @param searchString
	 *            the search string
	 * @return the result list
	 */
	public static List<Entry> searchContent(String searchString) {

		return Entry.find("byContentLike",
				"%" + searchString.toLowerCase() + "%").fetch();

	}

	/**
	 * Search questions tagged with the searchString.
	 * 
	 * @param searchString
	 *            the search string
	 * @return the list
	 */

	public static Set<Question> searchTaggedWith(String searchString) {
		List<Tag> matchingTags = Tag.find("byNameLike",
				"%" + searchString.toLowerCase() + "%").fetch();

		Set<Question> result = new HashSet<Question>();
		for (Tag tag : matchingTags) {
			result.addAll(Question.findTaggedWith(tag.name));
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

		return FileEntry.find("byContentLike",
				"%" + searchString.toLowerCase() + "%").fetch();
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
	public static List<Entry> sortByRating(List<Entry> entrys) {
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

		Set<Entry> foundEntrys = new HashSet<Entry>();

		foundEntrys.addAll(searchTitle(searchString));
		foundEntrys.addAll(searchContent(searchString));
		foundEntrys.addAll(searchTaggedWith(searchString));
		foundEntrys.addAll(searchEntrysWithFilename(searchString));

		List<Entry> results = new ArrayList<Entry>();
		results.addAll(foundEntrys);

		return sortByRating(results);

	}

	/**
	 * Search all questions with the searchString in it or in its answer.
	 * 
	 * @param searchString
	 *            the search string
	 * @return the list of questions
	 */
	public static Set<Question> searchQuestions(String searchString) {
		Set<Question> questions = new HashSet<Question>();
		List<Entry> entries = searchEntry(searchString);
		for (Entry entry : entries) {
			Question question;
			if (entry instanceof Answer) {
				question = ((Answer) entry).question;
			} else {
				question = (Question) entry;
			}
			questions.add(question);
		}
		return questions;
	}

	/**
	 * Search users by name or email.
	 * 
	 * @param searchString
	 *            the search string
	 * @return the list of matching users
	 */
	public static List<User> searchUsers(String searchString) {

		return User.find("name like ? or email like ?",
				"%" + searchString.toLowerCase() + "%",
				"%" + searchString.toLowerCase() + "%").fetch();
	}

}
