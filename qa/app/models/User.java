package models;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import models.fraudpointscale.FraudPoint;
import models.helper.Utils;
import play.data.validation.Required;
import play.db.jpa.JPASupport;
import play.db.jpa.Model;

/**
 * The Class User.
 */
@Entity
public class User extends Model {

	/** The entrys. */

	@OneToMany(mappedBy = "owner", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<MajorEntry> entrys;

	/** The votes. */
	@OneToMany(mappedBy = "owner", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Vote> votes;

	@OneToMany(mappedBy = "owner", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Notification> notifications;

	@ManyToMany(mappedBy = "fans")
	public Set<Comment> likedComments = new HashSet<Comment>();

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

	/** The is admin flag. */
	public boolean isAdmin = false;

	/** The is activated flag. */
	public boolean isActivated;

	/** The cached reputation score. */
	public int reputation;

	/** The fake id used for the XML importer. */
	public long fakeId;

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
		this.password = Utils.encryptStringToSHA1(password);
		this.entrys = new ArrayList<MajorEntry>();
		this.votes = new ArrayList<Vote>();
		this.notifications = new ArrayList<Notification>();
		this.timestamp = new Date();
		this.reputation = 0;
		this.isActivated = false;
	}

	public ActivationToken generateActivationToken() {
		ActivationToken token = ActivationToken.find("byUser", this).first();
		if (token != null) {
			token.delete();
		}
		token = new ActivationToken(this).save();
		return token;
	}

	public void activate() {
		this.isActivated = true;
		ActivationToken token = ActivationToken.find("byUser", this).first();
		if (token != null)
			token.delete();
		this.save();
	}

	public String getActivationToken() {
		ActivationToken token = ActivationToken.find("byUser", this).first();
		if (token == null)
			return null;
		else
			return token.activationToken;
	}

	/**
	 * Find the Reputation value.
	 * 
	 * @return the reputation
	 */
	public int reputation() {

		return profileReputation() + entryReputation();
	}

	public int entryReputation() {

		int reputation = 0;
		Iterator<MajorEntry> it = this.entrys.iterator();
		while (it.hasNext()) {
			MajorEntry entry = it.next();
			reputation += entry.rating();
			if (entry instanceof models.Answer
					&& ((Answer) entry).isBestAnswer())
				reputation += bestAnswerReputation;
		}
		return reputation;
	}

	public int profileReputation() {

		int reputation = 0;
		List<ProfileItem> profileItems = ProfileItem.findAll();
		for (ProfileItem item : profileItems) {
			ProfileEntry ent = item.findUserEntry(this);
			if (ent != null) {
				reputation++;
			}
		}

		return reputation;
	}

	/**
	 * Calculate the actual reputation value and save it to the database.
	 */
	public void calcReputation() {

		this.reputation = reputation();
		this.save();

	}

	public boolean isProfileFilledUp() {
		List<ProfileItem> items = ProfileItem.findAll();
		for (ProfileItem item : items) {
			if (item.findUserEntry(this) == null)
				return false;
		}
		return true;
	}

	/**
	 * Creates Graph data in JSON format.
	 * 
	 * @return graph data as JSON string
	 */
	public String graphData() {

		List<Point> points = fetchGraphData();

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
	 * Fetch graph data.
	 * 
	 * @return the sorted (by timestamp) list of graph points
	 */
	private List<Point> fetchGraphData() {

		ArrayList points = new ArrayList();

		points.add(new Point(this.timestamp, 0));
		points.add(new Point(new Date(), 0));

		Iterator<MajorEntry> en = this.entrys.iterator();
		while (en.hasNext()) {
			MajorEntry entry = en.next();
			if (entry instanceof Answer && ((Answer) entry).isBestAnswer()) {
				points.add(new Point(
						((Answer) entry).question.bestAnswerFreezer.timestamp,
						bestAnswerReputation));
			}
			if (entry instanceof Entry) {
				Iterator<Vote> vt = ((Entry) entry).votes.iterator();
				while (vt.hasNext()) {
					Vote vote = vt.next();
					points.add(new Point(vote.freezer.timestamp, vote.up ? 1
							: -1));
				}
			}
		}

		Collections.sort(points, new PointComparator());

		return points;

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
			return (arg1.time < arg0.time) ? 1 : -1;
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

		if (loginUser != null
				&& loginUser.password.equals(Utils
						.encryptStringToSHA1(password)))
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

	public User addComment(Entry entry, String content) {
		Comment comment = entry.addComment(this, content);
		this.entrys.add(comment);
		this.save();
		return this;

	}

	public User addFileToEntry(File file, Entry entry) {

		FileEntry fileEntry = entry.addFile(file, this);
		this.entrys.add(fileEntry);

		this.save();
		return this;
	}

	public User addNotification(Notification notification) {

		this.notifications.add(notification);
		this.save();
		return this;
	}

	public List<Notification> getNewNotifications() {

		return Notification.find(
				"owner =? and isNew = ? order by timestamp desc", this, true)
				.fetch();
	}

	public boolean hasNewNotifications() {

		return this.numberOfNewNotifications() > 0;
	}

	public long numberOfNewNotifications() {

		return Notification.count("owner = ? and isNew = ?", this, true);
	}

	public List<Notification> getNotifications(int numberOfNotifications) {

		return Notification.find("byOwner", this).fetch(numberOfNotifications);
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

	public long getNumberOfComments() {

		return Comment.count("owner = ?", this);
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

	public List<Entry> getQuestions() {

		return Question.find("owner like ? order by timestamp desc", this)
				.fetch();
	}

	public List<Entry> getAnswers() {
		return Answer.find("owner like ? order by timestamp desc", this)
				.fetch();
	}

	public void anonymify() {
		User anonym = User.find("byName", "Anonym").first();
		for (MajorEntry entry : entrys) {
			entry.owner = anonym;
			entry.save();
		}
		this.refresh();

	}

	@Override
	public <T extends JPASupport> T delete() {
		anonymify();
		entrys.clear();
		this.save();

		return super.delete();
	}

	public void setNewPassword(String password) {
		this.password = Utils.encryptStringToSHA1(password);
	}

	public List<Question> questions(int count, int page) {
		return Question.find("byOwner", this).fetch(page, count);
	}

	public long fraudPointScore() {
		return FraudPoint.count("user = ?", this);

	}

}
