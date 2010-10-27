package models;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;
import edu.emory.mathcs.backport.java.util.Collections;

@Entity
public class User extends Model {

	@OneToMany(mappedBy = "owner", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Entry> entrys;

	@OneToMany(mappedBy = "owner", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Vote> votes;

	@Required
	public String name;
	@Required
	public String password;
	@Required
	public String email;

	public Date timestamp;

	public static final int bestAnswerReputation = 50;

	/**
	 * Creates a <code>User</code> with a given name.
	 * 
	 * @param name
	 *            the name of the <code>User</code>
	 */
	public User(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = encrypt(password);
		this.entrys = new ArrayList<Entry>();
		this.votes = new ArrayList<Vote>();
		this.timestamp = new Date();
	}

	// TODO cache reputation for faster access
	/**
	 * Reputation.
	 * 
	 * @return the reputation
	 */
	public int reputation() {
		int reputation = 0;

		Iterator<Entry> it = this.entrys.iterator();
		while (it.hasNext()) {
			Entry entry = it.next();
			reputation += entry.rating();
			if (entry instanceof models.Answer
					&& ((Answer) entry).isBestAnswer())
				reputation += bestAnswerReputation;
		}

		return reputation;
	}

	// SM Using JSON Objects from http://www.json.org/java/ might be better
	/**
	 * Graph data.
	 * 
	 * @return graph data as JSON string
	 */
	public String graphData() {

		ArrayList points = new ArrayList();

		points.add(new Point(this.timestamp, 0));
		points.add(new Point(new Date(), 0));

		Iterator<Entry> en = this.entrys.iterator();
		while (en.hasNext()) {
			Entry entry = en.next();
			if (entry instanceof Answer && ((Answer) entry).isBestAnswer()) {
				points.add(new Point(((Answer) entry).bestAnswerTime,
						bestAnswerReputation));
			}

			Iterator<Vote> vt = entry.votes.iterator();
			while (vt.hasNext()) {
				Vote vote = vt.next();
				points.add(new Point(vote.freezer.timestamp, vote.up ? 1 : -1));
			}
		}

		Collections.sort(points, new PointComparator());

		int reputation = 0;
		StringBuffer data = new StringBuffer("[");
		Iterator<Point> it = points.iterator();
		while (it.hasNext()) {
			Point pt = it.next();
			reputation += pt.change;
			data.append("{\"time\": " + pt.time + ", \"value\": " + reputation
					+ "}");
			if (it.hasNext())
				data.append(',');
		}
		data.append(']');

		return data.toString();
	}

	/**
	 * A Point on the reputation graph.
	 */
	public class Point {
		public long time;
		public int change;
		public int reputation;

		public Point(Date timestamp, int change) {
			this.time = timestamp.getTime();
			this.change = change;
		}
	}

	/**
	 * Compares Points by timestamp.
	 */
	// TS Make this type safe! See Entry Comparator
	public class PointComparator implements Comparator {
		public int compare(Object arg0, Object arg1) {
			if (arg0 instanceof Point && arg1 instanceof Point) {
				return (int) (((Point) arg0).time - ((Point) arg1).time);
			} else {
				return 0;
			}
		}
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

		if (loginUser != null && loginUser.password.equals(encrypt(password)))
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
	 * Encrypt.
	 * 
	 * @param password
	 *            the password
	 * @return the encrypted password
	 */
	private static String encrypt(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(password.getBytes());
			return new BigInteger(1, md.digest(password.getBytes()))
					.toString(16);
		} catch (Exception e) {
			return password;
		}
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

	public List<Entry> getActivities(int numberOfActivitys) {

		return Entry.find("order by timestamp desc").fetch(numberOfActivitys);
	}

}
