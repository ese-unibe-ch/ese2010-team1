package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class RecommendedQuestions {

	/**
	 * Gets all related Questions according to a title.
	 * 
	 * @param title
	 *            the title of a question
	 * @return a list with all related questions with max. length 10
	 */
	public Set<Question> getRelatedQuestions(String title) {

		if (title.length() > 2) {
			Set<Question> relatedQuestions = new HashSet<Question>();
			title = title.toLowerCase();
			StringTokenizer tokenizer = new StringTokenizer(title, " ", false);

			while (relatedQuestions.size() <= 10 && tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (token.length() > 2) {
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
