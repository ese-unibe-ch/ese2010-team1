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

	/** The content. */
	@Lob
	public String content;

	/** The owner. */
	@ManyToOne
	public User owner;

	/** The votes. */
	@OneToMany(mappedBy = "entry", cascade = { CascadeType.MERGE,
			CascadeType.REMOVE, CascadeType.REFRESH })
	public List<Vote> votes;
	/*
	 * @OneToMany(mappedBy = "belongsTo", cascade = { CascadeType.MERGE,
	 * CascadeType.REMOVE, CascadeType.REFRESH }) public List<FileEntry> files;
	 */

	/** The timestamp. */
	public Date timestamp;

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
		// this.files = new ArrayList<FileEntry>();
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
	public long rating() {
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

	/**
	 * Check if user is allowed to vote.
	 * 
	 * @param user
	 *            the user
	 * @return true, if user is allowed to vote
	 */
	public boolean canVote(User user) {
		Vote alreadyVoted = Vote.find("byOwnerAndEntry", user, this).first();
		return user != this.owner
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
		return vote;
	}

	// TS Replace whitespace by percent symbol to get more hits
	/**
	 * Search the content field for the searchString.
	 * 
	 * @param searchString
	 *            the search string
	 * @return the result list
	 */
	public static List<Entry> searchContent(String searchString) {

		return Entry.find("byContentLike", "%" + searchString + "%").fetch();

	}
}
