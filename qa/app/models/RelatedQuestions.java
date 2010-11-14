package models;

import java.util.ArrayList;
import java.util.List;

public class RelatedQuestions {

	public List<String> getRelatedQuestions(String title) {
		List<String> relatedQuestions = new ArrayList<String>();
		// StringTokenizer tokenizer = new StringTokenizer(title, " ", false);
		//
		// while (relatedQuestions.size() <= 10 && tokenizer.hasMoreTokens()) {
		relatedQuestions.addAll(searchRelatedByTitle(title));
		// }
		return relatedQuestions;
	}

	public List<String> searchRelatedByTitle(String title) {
		List<Question> questions = new ArrayList<Question>();
		questions = Question.find("byTitleLike", "%" + title + "%").fetch();

		List<String> questionsTitles = new ArrayList<String>();
		for (Question q : questions) {
			questionsTitles.add(q.title);
		}
		return questionsTitles;
	}

}
