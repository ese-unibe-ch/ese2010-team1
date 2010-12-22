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
import models.fraudpointscale.FraudPointController;
import models.helper.Utils;
import play.data.validation.Required;
import play.db.jpa.JPASupport;
import play.db.jpa.Model;

/**
 * The Class User contains every data about a given user.
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

	/** The notifications. */
	@OneToMany(mappedBy = "owner", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Notification> notifications;

	/** The liked comments. */
	@ManyToMany(mappedBy = "fans")
	public Set<Comment> likedComments = new HashSet<Comment>();

	/** The name. */
	@Required
	public String name;

	/** The (encrypted) password. */
	@Required
	public String password;

	/** The email. */
	@Required
	public String email;

	/** The timestamp of the registration. */
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

	/**
	 * Generate activation token.
	 * 
	 * @return the activation token
	 */
	public ActivationToken generateActivationToken() {
		ActivationToken token = ActivationToken.find("byUser", this).first();
		if (token != null) {
			token.delete();
		}
		token = new ActivationToken(this).save();
		return token;
	}

	/**
	 * Grants user login rights.
	 */
	public void activate() {
		this.isActivated = true;
		ActivationToken token = ActivationToken.find("byUser", this).first();
		if (token != null)
			token.delete();
		this.save();
	}

	/**
	 * Gets the activation token.
	 * 
	 * @return the activation token
	 */
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

	/**
	 * Calculates the reputation earned with entries.
	 * 
	 * @return the int
	 */
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

	/**
	 * Calculates the reputation earned with profile informations.
	 * 
	 * @return the int
	 */
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

	/**
	 * Checks if is profile filled up.
	 * 
	 * @return true, if is profile filled up
	 */
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

	/**
	 * Adds the comment.
	 * 
	 * @param entry
	 *            the entry
	 * @param content
	 *            the content
	 * @return the user
	 */
	public User addComment(Entry entry, String content) {
		Comment comment = entry.addComment(this, content);
		this.entrys.add(comment);
		this.save();
		return this;

	}

	/**
	 * Adds the file to entry.
	 * 
	 * @param file
	 *            the file
	 * @param entry
	 *            the entry
	 * @return the user
	 */
	public User addFileToEntry(File file, Entry entry) {

		FileEntry fileEntry = entry.addFile(file, this);
		this.entrys.add(fileEntry);

		this.save();
		return this;
	}

	/**
	 * Adds the notification.
	 * 
	 * @param notification
	 *            the notification
	 * @return the user
	 */
	public User addNotification(Notification notification) {

		this.notifications.add(notification);
		this.save();
		return this;
	}

	/**
	 * Gets the new notifications.
	 * 
	 * @return the new notifications
	 */
	public List<Notification> getNewNotifications() {

		return Notification.find(
				"owner =? and isNew = ? order by timestamp desc", this, true)
				.fetch();
	}

	/**
	 * Checks for new notifications.
	 * 
	 * @return true, if successful
	 */
	public boolean hasNewNotifications() {

		return this.numberOfNewNotifications() > 0;
	}

	/**
	 * Number of new notifications.
	 * 
	 * @return the long
	 */
	public long numberOfNewNotifications() {

		return Notification.count("owner = ? and isNew = ?", this, true);
	}

	/**
	 * Gets the notifications.
	 * 
	 * @param numberOfNotifications
	 *            the number of notifications
	 * @return the notifications
	 */
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

	/**
	 * Gets the number of comments.
	 * 
	 * @return the number of comments
	 */
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

	/**
	 * Gets the questions.
	 * 
	 * @return the questions
	 */
	public List<Entry> getQuestions() {

		return Question.find("owner like ? order by timestamp desc", this)
				.fetch();
	}

	/**
	 * Gets the answers.
	 * 
	 * @return the answers
	 */
	public List<Entry> getAnswers() {
		return Answer.find("owner like ? order by timestamp desc", this)
				.fetch();
	}

	/**
	 * Anonymify entries. Used before a user gets deleted.
	 */
	public void anonymify() {
		User anonym = User.find("byName", "Anonym").first();
		for (MajorEntry entry : entrys) {
			entry.owner = anonym;
			entry.save();
		}
		this.refresh();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see play.db.jpa.JPASupport#delete()
	 */
	@Override
	public <T extends JPASupport> T delete() {
		anonymify();
		entrys.clear();
		this.save();

		return super.delete();
	}

	/**
	 * Sets the new password.
	 * 
	 * @param password
	 *            the new new password
	 */
	public void setNewPassword(String password) {
		this.password = Utils.encryptStringToSHA1(password);
	}

	/**
	 * Questions.
	 * 
	 * @param page
	 * @param count
	 * 
	 * @return the list
	 */
	public List<Question> questions(int count, int page) {
		return Question.find("byOwner", this).fetch(page, count);
	}

	/**
	 * Fraud point score.
	 * 
	 * @return the long
	 */
	public long fraudPointScore() {
		return FraudPoint.count("user = ?", this);

	}

	public String fraudPointViolations() {

		FraudPointController controller = FraudPointController.getInstance();
		StringBuffer buffer = new StringBuffer();
		List<FraudPoint> fpList = FraudPoint.find("byUser", this).fetch();
		Set<String> violations = new HashSet<String>();
		for (FraudPoint p : fpList) {
			violations.add(controller.getDescription(p.rule));
		}
		for (String violation : violations) {
			buffer.append(violation + "\n");
		}

		return buffer.toString();
	}
}
