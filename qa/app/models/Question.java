package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/**
 * A {@link Entry} containing a question as <code>content</code> and
 * {@link Answer}s.
 * 
 */
@Entity
public class Question extends Entry {

	/** The title. */
	public String title;

	@OneToMany(mappedBy = "question", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	private List<Answer> answers;

	// NEW by OLLI

	@ManyToMany(cascade = CascadeType.PERSIST)
	public Set<Tag> tags;

	public boolean isBestAnswerSet = false;

	/**
	 * Create a Question.
	 * 
	 * @param owner
	 *            the {@link User} who posted the <code>Question</code>
	 * @param content
	 *            the question
	 */
	public Question(User owner, String title, String content) {
		super(owner, content);
		this.title = title;
		this.answers = new ArrayList<Answer>();
		this.tags = new TreeSet<Tag>();

	}

	public String title() {
		return this.title;
	}

	/**
	 * Post a {@link Answer} to a <code>Question</code>
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
	 * Checks if a {@link Answer} belongs to a <code>Question</code>
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
	 * Get all {@link Answer}s to a <code>Question</code>
	 * 
	 * @return {@link Collection} of {@link Answers}
	 */
	public List<Answer> answers() {
		List<Answer> list = Answer.find("byQuestion", this).fetch();
		Collections.sort(list, new EntryComperator());
		return list;
	}

	public Question tagItWith(String name) {
		tags.add(Tag.findOrCreateByName(name));
		return this;
	}

	public static List<Question> findTaggedWith(String tag) {
		return Question
				.find("select distinct q from Question q join q.tags as t where t.name = ?",
						tag).fetch();
	}

	/**
	 * Checks if is a best answer set.
	 * 
	 * @return true, if is best answer is set
	 */
	public boolean isBestAnswerSet() {
		return isBestAnswerSet;
	}

	/**
	 * Sets the best answer.
	 * 
	 * @param s
	 *            the new best answer
	 */
	public void setBestAnswerFlag(boolean s) {
		this.isBestAnswerSet = s;
	}

	// TS Replace whitespace by percent symbol to get more hits
	public static List<Entry> searchTitle(String searchString) {
		return Question.find("byTitleLike", "%" + searchString + "%").fetch();
	}

}
