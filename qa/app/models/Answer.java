package models;
/**
 * A {@link Entry} containing an answer to a {@link Question}
 * 
 * @author Simon Marti
 * @author Mirco Kocher
 *
 */
public class Answer extends Entry {

	private Question question;
	private int id;
	
	/**
	 * Create an <code>Answer</code> to a {@link Question}.
	 * @param ic 
	 * @param owner the {@link User} who posted the <code>Answer</code>
	 * @param question the {@link Question} this <code>Answer</code> belongs to
	 * @param content the answer
	 */
	public Answer(int id, User owner, Question question, String content) {
		super(owner, content);
		this.question = question;
		this.id = id;
	}
	
	public String type() {
		return "Answer";
	}
	
	/**
	 * Unregisters all {@link Vote}s and itself.
	 */
	@Override
	public void unregister() {
		this.question.unregister(this);
		this.unregisterVotes();
		this.unregisterUser();
	}
	
	/**
	 * Get the {@link Question} belonging to the <code>Answer</code>.
	 * @return the {@link Question} this <code>Answer</code> belongs to
	 */
	public Question question() {
		return this.question;
	}

	public int id() {
		return this.id;
	}

}
