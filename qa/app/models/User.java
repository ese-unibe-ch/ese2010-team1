package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class User extends Model {

	@OneToMany(mappedBy = "owner", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	private List<Entry> entrys;

	@OneToMany(mappedBy = "owner", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	private List<Vote> votes;

	@Required
	private String name;
	@Required
	private String password;
	@Required
	private String email;

	/**
	 * Creates a <code>User</code> with a given name.
	 * 
	 * @param name
	 *            the name of the <code>User</code>
	 */
	public User(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.entrys = new ArrayList<Entry>();
		this.votes = new ArrayList<Vote>();

	}

	/**
	 * Returns the name of the <code>User</code>.
	 * 
	 * @return name of the <code>User</code>
	 */
	public String name() {
		return this.name;
	}

	public String password() {
		return this.password;
	}

	public String email() {
		return this.email;
	}

	/**
	 * Connect is used to login with a user.
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @return the userclass
	 */
	public static User connect(String username, String password) {

		User loginUser = User.find("byName", username).first();

		if (loginUser != null && loginUser.password().equals(password))
			return loginUser;
		else
			return null;

	}

	/**
	 * Returns whether a searched user exists.
	 * 
	 * @param username
	 *            the username
	 * @return true, if the searched user exists
	 */
	public static boolean exists(String username) {

		return User.find("byName", username).first() != null;
	}

	/**
	 * Adds the question.
	 * 
	 * @param title
	 *            the title
	 * @param content
	 *            the content
	 * @return the question
	 */
	public Question addQuestion(String title, String content) {
		Question newQuestion = new Question(this, title, content).save();
		this.entrys.add(newQuestion);
		this.save();
		return newQuestion;

	}

	/**
	 * Adds the answer to the database.
	 * 
	 * @param answer
	 *            the answer
	 * @return the user
	 */
	public User addAnswer(Answer answer) {
		this.entrys.add(answer);
		this.save();
		return this;
	}

	/**
	 * Adds the vote to the database.
	 * 
	 * @param vote
	 *            the vote
	 * @return the user
	 */
	public User addVote(Vote vote) {
		this.votes.add(vote);
		this.save();
		return this;
	}
	
	
	public long getNumberOfVotes() {
		return Vote.count("owner = ?", this);
	}
	
	public long getNumberOfQuestions() {
		return Question.count("owner = ?", this);
	}
	
	public long getNumberOfAnswers() {
		return Answer.count("owner = ?", this);		
		}
	

}
