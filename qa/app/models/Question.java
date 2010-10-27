package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * A {@link Entry} containing a question as <code>content</code> and.
 * 
 * {@link Answer}s.
 * 
 */
@Entity
public class Question extends Entry {

	/** The title. */
	public String title;

	/** The is best answer set. */
	public boolean isBestAnswerSet;

	/** The answers. */
	@OneToMany(mappedBy = "question", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Answer> answers;

	@OneToOne
	public Answer bestAnswer;

	@OneToOne
	public TimeFreezer bestAnswerFreezer;

	/**
	 * Create a Question.
	 * 
	 * @param owner
	 *            the {@link User} who posted the <code>Question</code>
	 * @param title
	 *            the title
	 * @param content
	 *            the question
	 */
	public Question(User owner, String title, String content) {
		super(owner, content);
		isBestAnswerSet = false;
		this.title = title;
		this.answers = new ArrayList<Answer>();
	}

	/**
	 * Title.
	 * 
	 * @return the string
	 */
	public String title() {
		return this.title;
	}

	/**
	 * Post a {@link Answer} to a <code>Question</code>.
	 * 
	 * @param user
	 *            the {@link User} posting the {@link Answer}
	 * @param content
	 *            the answer
	 * @return an {@link Answer}
	 */
	public Answer answer(User user, String content) {
		Answer answer = new Answer(user, this, content).save();
		this.answers.add(answer);
		return answer;
	}

	/**
	 * Checks if a {@link Answer} belongs to a <code>Question</code>.
	 * 
	 * @param answer
	 *            the {@link Answer} to check
	 * @return true if the {@link Answer} belongs to the <code>Question</code>
	 */
	public boolean hasAnswer(Answer answer) {
		return this.answers.contains(answer);
	}

	/**
	 * Get a <@link Collection} of all <code>Questions</code>.
	 * 
	 * @return all <code>Questions</code>
	 */
	public static List<Question> questions() {
		List<Question> list = new ArrayList();
		list.addAll(Question.<Question> findAll());
		Collections.sort(list, new EntryComperator());
		return list;
	}

	/**
	 * Get all {@link Answer}s to a <code>Question</code>.
	 * 
	 * @return {@link Collection} of {@link Answers}
	 */
	public List<Answer> answers() {
		List<Answer> list = Answer.find("byQuestion", this).fetch();
		Collections.sort(list, new EntryComperator());
		return list;
	}

	public void setBestAnswer(Answer answer) {
		if (this.canSetBestAnswer()) {
			this.bestAnswerFreezer = new TimeFreezer(1 * 60).save();
			this.bestAnswer = answer;
			this.save();
		}
	}

	public void resetBestAnswer() {
		if (this.canSetBestAnswer()) {
			this.bestAnswer = null;
			this.bestAnswerFreezer = null;
			this.save();
		}
	}

	public boolean canSetBestAnswer() {
		return this.bestAnswerFreezer == null
				|| !this.bestAnswerFreezer.frozen();
	}

	// TS Replace whitespace by percent symbol to get more hits
	public static List<Entry> searchTitle(String searchString) {
		return Question.find("byTitleLike", "%" + searchString + "%").fetch();
	}

}
