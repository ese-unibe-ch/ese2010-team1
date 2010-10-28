package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * A {@link Entry} containing an answer to a {@link Question}
 * 
 * 
 */
@Entity
public class Answer extends Entry {

	@ManyToOne
	public Question question;

	/**
	 * Create an <code>Answer</code> to a {@link Question}.
	 * 
	 * @param owner
	 *            the {@link User} who posted the <code>Answer</code>
	 * @param question
	 *            the {@link Question} this <code>Answer</code> belongs to
	 * @param content
	 *            the answer
	 */
	public Answer(User owner, Question question, String content) {
		super(owner, content);
		this.question = question;
		owner.addAnswer(this);
	}

	public boolean isBestAnswer() {
		return this.question.bestAnswer == this;
	}

	public boolean canBeBestAnswer() {
		assert this.question != null;
		return this.question.canSetBestAnswer()
				&& this.owner != this.question.owner;
	}

}
