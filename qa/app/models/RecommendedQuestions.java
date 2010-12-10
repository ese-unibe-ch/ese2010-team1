package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class RecommendedQuestions {

	private static int MIN_TITLE_LENGTH = 2;
	private static int MIN_TOKEN_LENGTH = 2;
	private static int MIN_RELATED_QUESTION = 10;

	/**
	 * Gets all related Questions according to a title.
	 * 
	 * @param title
	 *            the title of a question
	 * @return a list with all related questions with max. length 10
	 */
	public Set<Question> getRelatedQuestions(String title) {

		if (title.length() > MIN_TITLE_LENGTH) {
			Set<Question> relatedQuestions = new HashSet<Question>();
			title = title.toLowerCase();
			StringTokenizer tokenizer = new StringTokenizer(title, " ", false);

			while (relatedQuestions.size() <= MIN_RELATED_QUESTION
					&& tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (token.length() > MIN_TOKEN_LENGTH) {
					relatedQuestions.addAll(searchRelatedByTitle(token));
				}
			}

			return relatedQuestions;
		} else
			return null;
	}

	/**
	 * Search all questions with the searchTitle in its title.
	 * 
	 * @param title
	 *            the title of a question
	 * @return list of questions
	 */
	public List<Question> searchRelatedByTitle(String title) {
		List<Question> questions = new ArrayList<Question>();
		questions = Question.find("byTitleLike", "%" + title + "%").fetch();

		return questions;
	}

}
