package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class User extends Model {

	@OneToMany(mappedBy = "author", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Question> questions;

	@OneToMany(mappedBy = "author", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Answer> answers;

	@OneToMany(mappedBy = "user", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Vote> votes;

	private String name;
	private String password;
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
		this.questions = new ArrayList<Question>();
		this.votes = new ArrayList<Vote>();
		this.answers = new ArrayList<Answer>();
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

	public User addQuestion(String title, String content) {
		Question newQuestion = new Question(this, title, content).save();
		this.questions.add(newQuestion);
		this.save();
		return this;

	}

	public User addAnswer(Answer answer) {

		this.answers.add(answer);
		this.save();
		return this;
	}

	public User addVote(Vote vote) {

		this.votes.add(vote);
		this.save();
		return this;
	}

}
