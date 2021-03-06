package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import models.helper.EntryComperator;
import models.helper.TimeFreezer;

/**
 * A {@link Entry} containing a question as <code>content</code> and.
 * 
 * {@link Answer}s.
 */
@Entity
public class Question extends Entry {

	/** The title. */
	public String title;

	/** The answers. */
	@OneToMany(mappedBy = "question", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Answer> answers;

	/** The tags. */
	@ManyToMany(cascade = CascadeType.PERSIST)
	public Set<Tag> tags;

	/** The is best answer set. */
	public boolean isBestAnswerSet = false;

	/** The best answer. */
	@OneToOne
	public Answer bestAnswer;

	/** The best answer freezer. */
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
		this.title = title;
		this.answers = new ArrayList<Answer>();
		this.tags = new TreeSet<Tag>();

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
	 * Get a {@link Collection} of all <code>Questions</code> sorted by creation
	 * time.
	 * 
	 * @param page
	 * 
	 * @return all <code>Questions</code>
	 */
	public static List<Question> questions(int numberOfQuestions, int page) {

		if (numberOfQuestions == 0)
			return Question.find("order by rating DESC").fetch();
		else
			return Question.find("order by rating DESC").fetch(page,
					numberOfQuestions);
	}

	/**
	 * Get a {@link Collection} of all <code>Questions</code> sorted by last
	 * activity.
	 * 
	 * @param page
	 * 
	 * @return all <code>Questions</code>
	 */
	public static Set<Question> recentQuestions(int count, int page) {
		List<Entry> entrys = Entry.find("order by timestamp desc").fetch();
		Set<Question> skip = new HashSet<Question>();
		Set<Question> set = new HashSet<Question>();
		boolean skipped = page == 1;
		for (Entry entry : entrys) {
			Question question = (entry instanceof Question) ? (Question) entry
					: ((Answer) entry).question;

			if (skipped && !skip.contains(question))
				set.add(question);

			skip.add(question);

			if (!skipped && skip.size() == count * (page - 1)) {
				skipped = true;
			} else if (count > 0 && set.size() >= count) {
				break;
			}
		}
		return set;
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

	/**
	 * Tag it with.
	 * 
	 * @param name
	 *            the name
	 * @return the question
	 */
	public Question tagItWith(String name) {
		tags.add(Tag.findOrCreateByName(name));
		this.save();
		return this;
	}

	/**
	 * Find questions tagged with tagname.
	 * 
	 * @param tag
	 *            the tag
	 * @return the list
	 */
	public static List<Question> findTaggedWith(String tag) {
		return Question
				.find(
						"select distinct q from Question q join q.tags as t where t.name = ?",
						tag).fetch();
	}

	/**
	 * Sets the best answer.
	 * 
	 * @param answer
	 *            the new best answer
	 */
	public void setBestAnswer(Answer answer) {
		if (answer == null) {
			this.bestAnswer = null;
			this.bestAnswerFreezer = null;
		} else if (answer.canBeBestAnswer()) {
			this.bestAnswerFreezer = new TimeFreezer(1 * 60).save();
			this.bestAnswer = answer;
			answer.owner.calcReputation();
		}
		this.save();
	}

	/**
	 * Reset best answer.
	 */
	public void resetBestAnswer() {
		if (this.canSetBestAnswer()) {
			User user = this.bestAnswer.owner;
			this.setBestAnswer(null);
			user.calcReputation();
		}
	}

	/**
	 * Can set best answer.
	 * 
	 * @return true, if successful
	 */
	public boolean canSetBestAnswer() {
		return this.bestAnswerFreezer == null
				|| !this.bestAnswerFreezer.frozen();
	}

	/**
	 * Removes all tags.
	 */
	public void removeAllTags() {
		this.tags = new TreeSet<Tag>();
	}

	/**
	 * Creates a String with all tags separated with , .
	 * 
	 * @return the string
	 */
	public String tagsToString() {

		StringBuffer buffer = new StringBuffer();
		int i = 1;
		for (Tag tag : tags) {

			buffer.append(tag.name);
			if (i < tags.size()) {
				buffer.append(", ");
			}
			i++;

		}

		return buffer.toString();

	}

}
