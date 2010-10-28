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
import javax.persistence.OneToOne;

/**
 * A {@link Entry} containing a question as <code>content</code> and
 * {@link Answer}s.
 * 
 */
@Entity
public class Question extends Entry {

	private static final int recentQuestionCount = 10;

	/** The title. */
	public String title;

	@OneToMany(mappedBy = "question", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	private List<Answer> answers;

	@ManyToMany(cascade = CascadeType.PERSIST)
	public Set<Tag> tags;

	public boolean isBestAnswerSet = false;

	@OneToOne
	public Answer bestAnswer;

	@OneToOne
	public TimeFreezer bestAnswerFreezer;

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
	 * Get a {@link Collection} of all <code>Questions</code> sorted by creation
	 * time.
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
	 * Get a {@link Collection} of all <code>Questions</code> sorted by last
	 * activity.
	 * 
	 * @return all <code>Questions</code>
	 */
	public static List<Question> recentQuestions() {
		List<Entry> entrys = Entry.find("order by timestamp desc").fetch();
		List<Question> list = new ArrayList();
		for (Entry entry : entrys) {
			Question question = (entry instanceof Question) ? (Question) entry
					: ((Answer) entry).question;
			if (!list.contains(question)) {
				list.add(question);
				if (list.size() == recentQuestionCount)
					break;
			}
		}
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

	public Question tagItWith(String name) {
		tags.add(Tag.findOrCreateByName(name));
		return this;
	}

	public static List<Question> findTaggedWith(String tag) {
		return Question
				.find(
						"select distinct q from Question q join q.tags as t where t.name = ?",
						tag).fetch();
	}

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
