package models;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

/**
 * An {@link Item} which has a content and can be voted up and down.
 * 
 */

@Entity
public abstract class Entry extends MajorEntry {

	/** The different content states / versions. */
	@OneToMany(cascade = { CascadeType.MERGE, CascadeType.REMOVE,
			CascadeType.REFRESH })
	public List<ContentState> states;

	/** The related user votes. */
	@OneToMany(mappedBy = "entry", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Vote> votes;

	/** The related Comments */
	@OneToMany(mappedBy = "entry", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Comment> comments;

	/** The related files */
	@OneToMany(mappedBy = "entry", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<FileEntry> files;

	/** The generated notifications */
	@OneToMany(mappedBy = "entry", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Notification> notifications;

	public long rating;

	/**
	 * Create an <code>Entry</code>.
	 * 
	 * @param owner
	 *            the {@link User} who owns the <code>Entry</code>
	 * @param content
	 *            the content of the <code>Entry</code>
	 */
	public Entry(User owner, String content) {
		super(owner, content);
		this.votes = new ArrayList<Vote>();
		this.comments = new ArrayList<Comment>();
		this.files = new ArrayList<FileEntry>();
		this.notifications = new ArrayList<Notification>();
		this.states = new ArrayList<ContentState>();
		this.rating = 0;

	}

	/**
	 * Count all positive {@link Vote}s on an <code>Entry</code>.
	 * 
	 * @return number of positive {@link Vote}s
	 */
	public long upVotes() {
		return Vote.count("entry = ? and up = ?", this, true);
	}

	/**
	 * Number of votes.
	 * 
	 * @return the long
	 */
	public long numberOfVotes() {

		return Vote.count("entry = ?", this);
	}

	/**
	 * Count all negative {@link Vote}s on an <code>Entry</code>.
	 * 
	 * @return number of negative {@link Vote}s
	 */
	public long downVotes() {

		return Vote.count("entry = ? and up = ?", this, false);
	}

	/**
	 * Get the current rating of the <code>Entry</code>.
	 * 
	 * @return rating as an <code>Integer</code>
	 */
	@Override
	public long rating() {
		return this.rating;
	}

	public void calcRating() {

		this.rating = this.upVotes() - this.downVotes();
		this.save();
	}

	/**
	 * Edits an entry's content.
	 * 
	 * @param content
	 *            the new content
	 * 
	 * @param user
	 *            the user who edits the entry
	 */
	public void edit(String content, User user) {
		if (this.states.size() == 0) {
			ContentState state = new ContentState(this.content, this.owner)
					.save();
			this.states.add(state);
		}
		ContentState state = new ContentState(content, user).save();
		this.states.add(state);
		this.content = content;
	}

	/**
	 * Vote an <code>Entry</code> up.
	 * 
	 * @param user
	 *            the {@link User} who voted
	 * @return the {@link Vote}
	 */
	public Vote voteUp(User user) {
		Vote vote = this.vote(user, true);
		this.owner.calcReputation();
		return vote;
	}

	/**
	 * Vote an <code>Entry</code> down.
	 * 
	 * @param user
	 *            the {@link User} who voted
	 * @return the {@link Vote}
	 */
	public Vote voteDown(User user) {
		Vote vote = this.vote(user, false);
		this.owner.calcReputation();
		return vote;
	}

	/**
	 * Removes a vote.
	 * 
	 * @param vote
	 *            the vote
	 */
	public void removeVote(Vote vote) {
		if (vote != null && !vote.frozen()) {
			vote.delete();
			this.owner.calcReputation();
			this.calcRating();
		}
	}

	/**
	 * Check if a user already voted the entry.
	 * 
	 * @param user
	 *            the user
	 * @param up
	 *            true, if the vote should be positive
	 * @return true, if a vote exists
	 */
	public boolean alreadyVoted(User user, boolean up) {
		Vote alreadyVoted = Vote.find("byOwnerAndEntry", user, this).first();
		return alreadyVoted != null && alreadyVoted.up == up;
	}

	/**
	 * Check if user is allowed to vote.
	 * 
	 * @param user
	 *            the user
	 * @return true, if user is allowed to vote
	 */
	public boolean canVote(User user) {
		Vote alreadyVoted = Vote.find("byOwnerAndEntry", user, this).first();
		return user != this.owner && user.reputation >= ProfileItem.count()
				&& (alreadyVoted == null || !alreadyVoted.frozen());
	}

	/**
	 * Vote an <code>Entry</code>.
	 * 
	 * @param user
	 *            the user
	 * @param up
	 *            the up
	 * @return the vote
	 */
	private Vote vote(User user, boolean up) {

		Vote alreadyVoted = Vote.find("byOwnerAndEntry", user, this).first();

		if (user == this.owner)
			return null;
		if (alreadyVoted != null) {
			if (alreadyVoted.frozen())
				return null;
			else
				alreadyVoted.delete();
		}

		Vote vote = new Vote(user, this, up).save();
		this.votes.add(vote);
		this.calcRating();
		return vote;
	}

	/**
	 * Gets a list with all comments of this entry.
	 * 
	 * @return the list
	 */
	public List<Comment> listComments() {
		List<Comment> list = Comment.find("byEntry", this).fetch();
		return list;
	}

	/**
	 * Adds a comment to this entry
	 * 
	 * @param owner
	 *            the one who comments
	 * 
	 * @param content
	 *            the content of the comment
	 * 
	 * @return the comment
	 */
	public Comment addComment(User owner, String content) {
		Comment comment = new Comment(owner, this, content).save();
		this.comments.add(comment);
		this.save();

		if (owner != this.owner) {
			this.addNotification("has been commented");
		}
		return comment;
	}

	/**
	 * Adds a new notification.
	 * 
	 * @param activity
	 *            the activity
	 */
	public void addNotification(String activity) {

		Notification notification = new Notification(this.owner, this, activity)
				.save();
		this.owner.addNotification(notification);
		this.notifications.add(notification);
		this.save();

	}

	/**
	 * Adds a file to this entry
	 * 
	 * @param file
	 *            the file
	 * 
	 * @param user
	 *            the user
	 * 
	 * @return the file entry
	 */
	public FileEntry addFile(File file, User user) {

		FileEntry entry = FileEntry.upload(file, this, user);
		files.add(entry);

		this.save();

		return entry;
	}

	/**
	 * Gets all file entries of this entry.
	 * 
	 * @return the list
	 */
	public List<FileEntry> getFiles() {

		return FileEntry.find("byEntry", this).fetch();
	}

	/**
	 * Compares two entries by its rating.
	 */
	@Entity
	public class ContentState extends Model {
		@ManyToOne
		public User user;

		public Date timestamp;

		@Lob
		public String content;

		public ContentState(String content, User user) {
			this.timestamp = new Date();
			this.content = content;
			this.user = user;
		}
	}
}
