package models;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class RecommendedQuestions {

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

	public List<Question> searchRelatedByTitle(String title) {
		List<Question> questions = new ArrayList<Question>();
		questions = Question.find("byTitleLike", "%" + title + "%").fetch();

		return questions;
	}

}
