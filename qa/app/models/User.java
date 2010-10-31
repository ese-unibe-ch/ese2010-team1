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

/**
 * The Class User.
 */
@Entity
public class User extends Model {

	/** The entrys. */
	@OneToMany(mappedBy = "owner", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Entry> entrys;

	/** The votes. */
	@OneToMany(mappedBy = "owner", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Vote> votes;
	/*
	 * @OneToMany(mappedBy = "owner", cascade = { CascadeType.MERGE,
	 * CascadeType.REMOVE, CascadeType.REFRESH }) public List<FileEntry> files;
	 */
	/** The name. */
	@Required
	public String name;

	/** The password. */
	@Required
	public String password;

	/** The email. */
	@Required
	public String email;

	/** The timestamp. */
	public Date timestamp;

	/** The Constant bestAnswerReputation. */
	public static final int bestAnswerReputation = 50;

	/** The is admin. */
	public boolean isAdmin = false;

	/**
	 * Creates a <code>User</code> with a given name.
	 * 
	 * @param name
	 *            the name of the <code>User</code>
	 * @param email
	 *            the email
	 * @param password
	 *            the password
	 */
	public User(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = encrypt(password);
		this.entrys = new ArrayList<Entry>();
		this.votes = new ArrayList<Vote>();
		// this.files = new ArrayList<FileEntry>();
		this.timestamp = new Date();
	}

	// SM cache reputation for faster access
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
				points.add(new Point(
						((Answer) entry).question.bestAnswerFreezer.timestamp,
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

		/** The time. */
		public long time;

		/** The change. */
		public int change;

		/** The reputation. */
		public int reputation;

		/**
		 * Instantiates a new point.
		 * 
		 * @param timestamp
		 *            the timestamp
		 * @param change
		 *            the change
		 */
		public Point(Date timestamp, int change) {
			this.time = timestamp.getTime();
			this.change = change;
		}
	}

	/**
	 * Compares Points by timestamp.
	 */
	public class PointComparator implements Comparator<Point> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Point arg0, Point arg1) {
			return (int) (((Point) arg0).time - ((Point) arg1).time);
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

	/**
	 * Gets the number of votes.
	 * 
	 * @return the number of votes
	 */
	public long getNumberOfVotes() {
		return Vote.count("owner = ?", this);
	}

	/**
	 * Gets the number of questions.
	 * 
	 * @return the number of questions
	 */
	public long getNumberOfQuestions() {
		return Question.count("owner = ?", this);
	}

	/**
	 * Gets the number of answers.
	 * 
	 * @return the number of answers
	 */
	public long getNumberOfAnswers() {
		return Answer.count("owner = ?", this);
	}

	/**
	 * Gets the activities.
	 * 
	 * @param numberOfActivitys
	 *            the number of activitys
	 * @return the activities
	 */
	public List<Entry> getActivities(int numberOfActivitys) {

		return Entry.find("owner like ? order by timestamp desc", this).fetch(
				numberOfActivitys);
	}

}
