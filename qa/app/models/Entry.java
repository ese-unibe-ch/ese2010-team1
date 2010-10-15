package models;

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
public abstract class Entry extends Model {

	@Lob
	private String content;
	@ManyToOne
	private User owner;

	@OneToMany(mappedBy = "entry", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	private List<Vote> votes;

	private Date timestamp;

	/**
	 * Create an <code>Entry</code>.
	 * 
	 * @param owner
	 *            the {@link User} who owns the <code>Entry</code>
	 * @param content
	 *            the content of the <code>Entry</code>
	 */
	public Entry(User owner, String content) {
		this.owner = owner;
		this.content = content;
		this.timestamp = new Date();
		this.votes = new ArrayList<Vote>();
	}

	public abstract String type();

	public String content() {
		return this.content;
	}

	public User owner() {
		return this.owner;
	}

	/**
	 * Get the time the <code>Entry</code> was created.
	 * 
	 * @return the creation date as a UNIX timestamp
	 */
	public Date timestamp() {
		return this.timestamp;
	}

	/**
	 * Count all positive {@link Vote}s on an <code>Entry</code>
	 * 
	 * @return number of positive {@link Vote}s
	 */
	public int upVotes() {
		return Vote.find("byEntryAndUp", this, true).fetch().size();
	}

	public int numberOfVotes() {

		return Vote.find("byEntry", this).fetch().size();
	}

	/**
	 * Count all negative {@link Vote}s on an <code>Entry</code>
	 * 
	 * @return number of negative {@link Vote}s
	 */
	public int downVotes() {

		return Vote.find("byEntryAndUp", this, false).fetch().size();
	}

	/**
	 * Get the current rating of the <code>Entry</code>.
	 * 
	 * @return rating as an <code>Integer</code>
	 */
	public int rating() {
		return this.upVotes() - this.downVotes();
	}

	/**
	 * Vote an <code>Entry</code> up.
	 * 
	 * @param user
	 *            the {@link User} who voted
	 * @return the {@link Vote}
	 */
	public Vote voteUp(User user) {
		return this.vote(user, true);
	}

	/**
	 * Vote an <code>Entry</code> down.
	 * 
	 * @param user
	 *            the {@link User} who voted
	 * @return the {@link Vote}
	 */
	public Vote voteDown(User user) {
		return this.vote(user, false);
	}

	private Vote vote(User user, boolean up) {

		Vote alreadyVoted = Vote.find("byOwnerAndEntry", user, this).first();

		if (user == this.owner)
			return null;
		if (alreadyVoted != null) {
			alreadyVoted.delete();
		}

		Vote vote = new Vote(user, this, up).save();
		this.votes.add(vote);
		return vote;
	}
}
