package models;

import java.util.Date;

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

	public boolean isBestAnswer = false;
	public BestAnswerSetter bestAnswerSetter = null;

	// SM should be replaced with generic TimeFreezer
	public Date bestAnswerTime;

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

	/**
	 * Get the {@link Question} belonging to the <code>Answer</code>.
	 * 
	 * @return the {@link Question} this <code>Answer</code> belongs to
	 */
	public Question question() {
		return this.question;
	}

	public boolean isBestAnswer() {
		return isBestAnswer;
	}

	public void isBestAnswer(boolean b) {
		isBestAnswer = b;
	}

	public BestAnswerSetter bestAnswerSetter() {
		return bestAnswerSetter;
	}

	public void bestAnswerSetter(BestAnswerSetter bestAnswerSetter) {
		this.bestAnswerSetter = bestAnswerSetter;

	}

}
