package models;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class RelatedQuestions {

	public List<Question> getRelatedQuestions(String title) {
		List<Question> relatedQuestions = new ArrayList<Question>();
		StringTokenizer tokenizer = new StringTokenizer(title, " ", false);

		while (relatedQuestions.size() <= 10 && tokenizer.hasMoreTokens()) {
			relatedQuestions
					.addAll(searchRelatedByTitle(tokenizer.nextToken()));
		}
		return relatedQuestions;
	}

	public List<Question> searchRelatedByTitle(String title) {
		return Question.find("byTitleLike", "%" + title + "%").fetch();
	}

}
