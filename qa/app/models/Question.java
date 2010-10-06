package models;
import java.util.*;

/**
 * A {@link Entry} containing a question as <code>content</code> and {@link Answer}s.  
 * 
 * @author Simon Marti
 * @author Mirco Kocher
 *
 */
public class Question extends Entry {

	private IDTable<Answer> answers;
	private int id;
	
	private static IDTable<Question> questions = new IDTable();
	
	/**
	 * Create a Question.
	 * @param owner the {@link User} who posted the <code>Question</code>
	 * @param content the question
	 */
	public Question(User owner, String content) {
		super(owner, content);
		this.answers = new IDTable<Answer>();
		this.id = questions.add(this);
	}

	public String type() {
		return "Question";
	}
	
	/**
	 * Unregisters all {@link Answer}s, {@link Vote}s and itself.
	 */
	@Override
	public void unregister() {
		Iterator<Answer> it = this.answers.iterator();
		this.answers = new IDTable<Answer>();
		while(it.hasNext()) {
			it.next().unregister();
		}
		questions.remove(this.id);
		this.unregisterVotes();
		this.unregisterUser();
	}
	
	/**
	 * Unregisters a deleted {@link Answer}.
	 * @param answer the {@link Answer} to unregister
	 */
	public void unregister(Answer answer) {
		this.answers.remove(answer.id());
	}

	/**
	 * Post a {@link Answer} to a <code>Question</code>
	 * @param user the {@link User} posting the {@link Answer}
	 * @param content the answer
	 * @return an {@link Answer}
	 */
	public Answer answer(User user, String content) {
		Answer answer = new Answer(this.answers.nextID(), user, this, content);
		this.answers.add(answer);
		return answer;
	}
	
	/**
	 * Checks if a {@link Answer} belongs to a <code>Question</code>
	 * @param answer the {@link Answer} to check
	 * @return true if the {@link Answer} belongs to the <code>Question</code>
	 */
	public boolean hasAnswer(Answer answer) {
		return this.answers.contains(answer);
	}
	
	/**
	 * Get the <code>id</code> of the <code>Question</code>.
	 * The <code>id</code> does never change.
	 * @return id of the <code>Question</code>
	 */
	public int id() {
		return this.id;
	}

	/**
	 * Get a <@link Collection} of all <code>Questions</code>.
	 * @return all <code>Questions</code>
	 */
	public static List<Question> questions() {
		List<Question> list = new ArrayList();
		list.addAll(questions.list());
		Collections.sort(list, new EntryComperator());
		return list;
	}
	
	/**
	 * Get the <code>Question</code> with the given id.
	 * @param id
	 * @return a <code>Question</code> or null if the given id doesn't exist.
	 */
	public static Question get(int id) {
		return questions.get(id);
	}

	/**
	 * Get all {@link Answer}s to a <code>Question</code>
	 * @return {@link Collection} of {@link Answers}
	 */
	public List<Answer> answers() {
		List<Answer> list = new ArrayList();
		list.addAll(answers.list());
		Collections.sort(list, new EntryComperator());
		return list;
	}

	/**
	 * Get a specific {@link Answer} to a <code>Question</code>
	 * @param id of the <code>Answer</code>
	 * @return {@link Answer} or null
	 */
	public Entry getAnswer(int id) {
		return this.answers.get(id);
	}

}
