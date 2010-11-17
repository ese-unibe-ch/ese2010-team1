package models;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class RecommendedQuestions {

	/**
	 * Gets all related Questions according to a title.
	 * 
	 * @param title
	 *            the title of a question
	 * @return a list with all related questions with max. length 10
	 */
	public List<Question> getRelatedQuestions(String title) {

		if (title.length() > 2) {
			List<Question> relatedQuestions = new ArrayList<Question>();
			List<Question> recommendedQuestions = new ArrayList<Question>();
			title = title.toLowerCase();
			StringTokenizer tokenizer = new StringTokenizer(title, " ", false);

			while (relatedQuestions.size() <= 10 && tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (token.length() > 2) {
					relatedQuestions.addAll(searchRelatedByTitle(token));
				}
			}
			for (Question q : relatedQuestions) {
				if (!recommendedQuestions.contains(q))
					recommendedQuestions.add(q);
			}
			return recommendedQuestions;
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
