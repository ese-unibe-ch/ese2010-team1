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

	// TODO if possible make admin panel to define profile entries
	// TODO if needed make private variables

	public String biography;
	public String lifePhilosophy;
	public String editorOfChoice;

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

	public static User connect(String username, String password) {

		User loginUser = User.find("byName", username).first();

		if (loginUser != null && loginUser.password().equals(password))
			return loginUser;
		else
			return null;

	}

	public static boolean exists(String username) {

		return User.find("byName", username).first() != null;
	}

	public Question addQuestion(String title, String content) {
		Question newQuestion = new Question(this, title, content).save();
		this.entrys.add(newQuestion);
		this.save();
		return newQuestion;

	}

	public User addAnswer(Answer answer) {
		this.entrys.add(answer);
		this.save();
		return this;
	}

	public User addVote(Vote vote) {
		this.votes.add(vote);
		this.save();
		return this;
	}

	public void setBestAnswer(Answer answer) {
		if (answer.question().owner() == this
				&& !answer.question().isBestAnswerSet()) {
			answer.isBestAnswer(true);
			answer.question().isBestAnswerSet(true);
		}
	}

}
